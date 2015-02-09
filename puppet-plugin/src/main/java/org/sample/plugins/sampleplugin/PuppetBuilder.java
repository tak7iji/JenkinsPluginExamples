package org.sample.plugins.sampleplugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.plugins.ansicolor.AnsiColorBuildWrapper;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.sample.plugins.sampleplugin.PuppetBuilder.ExecMode.ExecType;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Logger;

public class PuppetBuilder extends Builder {
    static Logger logger = Logger.getLogger("org.sample.plugins.sampleplugin.PuppetBuilder");

    private final String puppetInstallationName;
    private final String facterInstallationName;
    private final ExecMode execMode;
    private final String rubyPath;
    private final String modulePath;
    private final boolean noop;
    private final boolean debug;
    private final boolean color;

    public static class ExecMode {
        public enum ExecType {FILE, MANIFEST}

        protected final ExecType execType;
        protected final String file;
        protected final String manifest;

        @DataBoundConstructor
        public ExecMode(ExecType value, String file, String manifest) {
            this.execType = value;
            this.file = file;
            this.manifest = manifest;
        }

        public ExecType getExecType() {
            return execType;
        }

        public String getFile() {
            return file;
        }

        public String getManifest() {
            return manifest;
        }
    }

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public PuppetBuilder(String puppetInstallationName, String facterInstallationName, ExecMode execMode, String rubyPath, String modulePath, boolean debug, boolean noop, boolean color) {
        this.puppetInstallationName = puppetInstallationName;
        this.facterInstallationName = facterInstallationName;
        this.execMode = execMode;
        this.rubyPath = rubyPath;
        this.modulePath = modulePath;
        this.debug = debug;
        this.noop = noop;
        this.color = color;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getRubyPath() {
        return rubyPath;
    }

    public String getModulePath() {
        return modulePath;
    }

    public boolean getNoop() {
        return noop;
    }

    public boolean getDebug() {
        return debug;
    }

    public boolean getColor() {
        return color;
    }

    public String getFile() {
        return execMode.file;
    }

    public String getManifest() {
        return execMode.manifest;
    }

    public ExecType getExecType() {
        return execMode.execType;
    }

    public PuppetInstallation getPuppetInstallation(final EnvVars env, final Node node, final BuildListener listener) {
        for (final PuppetInstallation installation : getDescriptor().getPuppetInstallations()) {
            if (puppetInstallationName != null && puppetInstallationName.equals(installation.getName())) {
                try {
                    return installation.forEnvironment(env).forNode(node, TaskListener.NULL);
                } catch (final IOException e) {
                    listener.fatalError("IOException while locating installation", e);
                } catch (final InterruptedException e) {
                    listener.fatalError("InterruptedException while locating installation", e);
                }
            }
        }

        return null;
    }

    public FacterInstallation getFacterInstallation(final EnvVars env, final Node node, final BuildListener listener) {
        for (final FacterInstallation installation : getDescriptor().getFacterInstallations()) {
            if (facterInstallationName != null && facterInstallationName.equals(installation.getName())) {
                try {
                    return installation.forEnvironment(env).forNode(node, TaskListener.NULL);
                } catch (final IOException e) {
                    listener.fatalError("IOException while locating installation", e);
                } catch (final InterruptedException e) {
                    listener.fatalError("InterruptedException while locating installation", e);
                }
            }
        }

        return null;
    }

    public String getPuppetInstallationName() {
        return puppetInstallationName;
    }

    public String getFacterInstallationName() {
        return facterInstallationName;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        boolean exitCode = false;

        try {
            String file;
            final Computer computer = Computer.currentComputer();
            final Node node = computer.getNode();
            final EnvVars env = computer.getEnvironment();
            final Map<Object, Object> props = node.toComputer().getSystemProperties();
            final String fileSeparator = (String) props.get("file.separator");
            final String pathSeparator = (String) props.get("path.separator");

            final PuppetInstallation puppetInstallation = getPuppetInstallation(env, node, listener);
            final FacterInstallation facterInstallation = getFacterInstallation(env, node, listener);

            String puppetPath = (puppetInstallation != null) ? puppetInstallation.getHome() : null;
            String facterPath = (facterInstallation != null) ? facterInstallation.getHome() : null;
            StringBuilder rubyLib = new StringBuilder();

            final ArgumentListBuilder cmd = new ArgumentListBuilder();

            final boolean isWindows = ((String) props.get("os.name")).startsWith("Windows");
            final boolean setPuppetPath = (puppetPath != null && !"".equals(puppetPath));
            final boolean setFacterPath = (facterPath != null && !"".equals(facterPath));
            final boolean setRubyPath = (rubyPath != null && !"".equals(rubyPath));

            /*
             * install type
             *   Install via package manager
             *     puppet command locate on puppet installed directory (/usr/bin at *nix, specified directory at Windows)
             *   Install from Tarball
             *     puppet command locate on ruby installed directory
             *   Running directly from source
             *     puppet command locate on puppet installed directory that you specified
             *
             *   If puppetPath is set
             *    If <puppetPath>/bin/puppet(.bat) is exists, then use this path
             *    If <puppetPath>/bin/puppet(.bat) is not exists, then use (<rubyPath>/bin/)ruby(.exe) <puppetPath>/bin/puppet
             *   Else use (<rubyPath>/bin/)puppet.bat
             */
            final String puppetCmdPath = puppetPath + fileSeparator + "bin" + fileSeparator;
            final String rubyCmdPath = rubyPath + fileSeparator + "bin" + fileSeparator;
            final String puppetCmd = "puppet" + ((isWindows) ? ".bat" : "");
            final String rubyCmd = "ruby" + ((isWindows) ? ".exe" : "");

            if (setPuppetPath) {
                if (node.createPath(puppetCmdPath + puppetCmd).exists()) {
                    cmd.add(puppetCmdPath + puppetCmd);
                } else {
                    cmd.add(((setRubyPath) ? rubyCmdPath : "") + rubyCmd, puppetCmdPath + "puppet");
                }
            } else {
                cmd.add(((setRubyPath) ? rubyCmdPath : "") + puppetCmd);
            }

            if (setPuppetPath) {
                rubyLib.append(puppetPath).append(fileSeparator).append("lib").append(pathSeparator);
            }
            if (setFacterPath) {
                rubyLib.append(facterPath).append(fileSeparator).append("lib");
            }

            if (rubyLib.length() > 0) env.put("RUBYLIB", rubyLib.toString());
            listener.getLogger().println("RUBYLIB: " + env.get("RUBYLIB"));
            listener.getLogger().println("PATH: " + env.get("PATH"));

            switch (execMode.execType) {
                case FILE:
                    file = ((node.createPath(execMode.file).exists()) ? "" : build.getWorkspace().getRemote() + fileSeparator) + execMode.file;
                    break;
                case MANIFEST:
                    file = createManifestFile(build.getWorkspace()).getRemote();
                    break;
                default:
                    build.setResult(Result.FAILURE);
                    return false;
            }

            cmd.add("apply", file);

            OutputStream stream = listener.getLogger();

            if (!color) {
                cmd.add("--color", "false");
            } else {
                BuildableItemWithBuildWrappers proj = (BuildableItemWithBuildWrappers)build.getProject();
                AnsiColorBuildWrapper ansicolor = (AnsiColorBuildWrapper) proj.getBuildWrappersList().get(AnsiColorBuildWrapper.class);
                if(ansicolor==null) {
                    ansicolor = new AnsiColorBuildWrapper(null);
                    proj.getBuildWrappersList().add(ansicolor);
                    proj.save();
                    stream = ansicolor.decorateLogger(build,listener.getLogger());
                }
            }

            if (noop) cmd.add("--noop");
            if (debug) cmd.add("--debug");
            cmd.add("--modulepath", build.getWorkspace().getRemote() + fileSeparator + "modules" + ((modulePath.length() > 0) ? pathSeparator + ((new File(modulePath).isAbsolute()) ? "" : build.getWorkspace().getRemote() + fileSeparator) + modulePath : ""));

            if (launcher.decorateByEnv(env).launch().stdout(stream).stderr(stream).cmds(cmd).join() == 0)
                exitCode = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exitCode;
    }

    public FilePath createManifestFile(@Nonnull FilePath dir) throws IOException, InterruptedException {
        return dir.createTextTempFile("puppet", ".pp", execMode.manifest, false);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link PuppetBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/PuppetBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public DescriptorImpl() {
            super();
            load();
        }

        public ListBoxModel doFillPuppetInstallationNameItems() {
            ListBoxModel items = new ListBoxModel();
            for (PuppetInstallation installation : getPuppetInstallations()) {
                items.add(installation.getName());
            }
            return items;
        }

        public ListBoxModel doFillFacterInstallationNameItems() {
            ListBoxModel items = new ListBoxModel();
            for (FacterInstallation installation : getFacterInstallations()) {
                items.add(installation.getName());
            }
            return items;
        }

        private PuppetInstallation[] puppetInstallations = new PuppetInstallation[0];

        public PuppetInstallation[] getPuppetInstallations() {
            return puppetInstallations;
        }

        public void setPuppetInstallations(PuppetInstallation... puppetInstallations) {
            this.puppetInstallations = puppetInstallations;
            save();
        }

        private FacterInstallation[] facterInstallations = new FacterInstallation[0];

        public FacterInstallation[] getFacterInstallations() {
            return facterInstallations;
        }

        public void setFacterInstallations(FacterInstallation... facterInstallations) {
            this.facterInstallations = facterInstallations;
            save();
        }

        @Override
        public boolean configure(final StaplerRequest req, JSONObject json) {
            save();
            return true;
        }

//        @Override
//        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
//            boolean isColored = formData.getBoolean("color");
//            for (String val : req.getParameterValues("json")) {
//                JSONObject obj = JSONObject.fromObject(val).getJSONObject("hudson-plugins-ansicolor-AnsiColorBuildWrapper");
//                if (obj.isEmpty() && isColored) {
//                    throw new FormException("Enable AnsiColor Plugin, if colored output enable.", "color");
//                }
//            }
//
//            return super.newInstance(req, formData);
//
//        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckFile(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a manifest file name");
            return FormValidation.ok();
        }

        public FormValidation doCheckModulePath(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.warning("Use default module path");
            return FormValidation.ok();
        }

//        public FormValidation doCheckColor(@QueryParameter boolean value)
//                throws IOException, ServletException {
//            if (value) {
//                if (Hudson.getInstance().getPlugin("ansicolor") == null) {
//                    return FormValidation.error("Please install or enable the AnsiColor Plugin");
//                }
//            }
//
//            return FormValidation.ok();
//        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return Messages.PuppetBuilder_DisplayName();
        }
    }
}

