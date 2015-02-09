package org.sample.plugins.simpleplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class HelloWorldBuilder extends Builder {

    static Logger logger = Logger.getLogger("org.sample.plugins.simpleplugin.HelloWorldBuilder");
    private final String name;
    private final Block block;

    @DataBoundConstructor
    public HelloWorldBuilder(String name, Block block) {
        this.name = name;
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public Block getBlock() {
        return block;
    }

    public String getText() {
        return (block != null) ? block.getText() : null;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new HelloWorldProjectAction(project);
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        listener.getLogger().println("Builder prebuild.");
        return true;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener)
            throws IOException, InterruptedException {

        listener.getLogger().println("Builder perform.");

        final Computer computer = Computer.currentComputer();
        final Node node = computer.getNode();
        final Map<Object, Object> props = node.toComputer().getSystemProperties();
        final boolean isWindows = ((String) props.get("os.name")).startsWith("Windows");

        ArgumentListBuilder cmd = new ArgumentListBuilder();
        if (isWindows) {
            cmd.add("cmd", "/c");
        }
        cmd.add("echo");

        Launcher.ProcStarter starter = launcher.launch().stdout(listener.getLogger()).stderr(listener.getLogger());
        if (getDescriptor().getUseFrench())
            cmd.add("Bonjour, " + name + "!");
        else
            cmd.add("Hello, " + name + "!");

        starter.cmds(cmd).join();

        build.addAction(new HelloWorldAction(build));
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public static class Block {
        private String text;

        @DataBoundConstructor
        public Block(String text) {

            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private boolean useFrench;
        private String sample;

        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_setName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_tooShortName());
            return FormValidation.ok();
        }

        public FormValidation doCheckSample(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error(Messages.HelloWorldBuilder_setWords());
            }
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        public String getDisplayName() {
            return Messages.HelloWorldBuilder_sayHelloWorld();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            logger.info(formData.toString());
            useFrench = formData.getBoolean("useFrench");
            sample = formData.getString("sample");
            if (sample.length() < 5) {
                throw new FormException("Sample word must be longer than 5 characters.", "sample");
            }
            save();
            return super.configure(req, formData);
        }

        public boolean getUseFrench() {
            return useFrench;
        }
    }
}

