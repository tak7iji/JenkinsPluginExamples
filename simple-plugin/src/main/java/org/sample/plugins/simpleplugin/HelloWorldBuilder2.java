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

public class HelloWorldBuilder2 extends Builder {

    static Logger logger = Logger.getLogger("org.sample.plugins.simpleplugin.HelloWorldBuilder");
    private final String name;

    @DataBoundConstructor
    public HelloWorldBuilder2(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
        cmd.add(Aisatsu.all().get(0).getMessage() + name + "!");
        starter.cmds(cmd).join();

        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
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

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        public String getDisplayName() {
            return Messages.HelloWorldBuilder2_sayHelloWorld();
        }
    }
}

