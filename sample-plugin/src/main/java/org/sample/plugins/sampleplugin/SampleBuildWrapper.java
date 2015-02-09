package org.sample.plugins.sampleplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tools.ToolProperty;
import hudson.tools.ToolPropertyDescriptor;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/21
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class SampleBuildWrapper extends BuildWrapper {
    private final String installationName;

    @DataBoundConstructor
    public SampleBuildWrapper(String installationName) {
        this.installationName = installationName;
    }

    public SampleInstallation2 getSampleInstallation() {
        for (SampleInstallation2 i : Jenkins.getInstance().getDescriptorByType(SampleInstallation2.DescriptorImpl.class).getInstallations()) {
            if (installationName != null && installationName.equals(i.getName()))
                return i;
        }
        return null;
    }

    @Override
    public BuildWrapper.Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().println("Setup before build.");
        listener.getLogger().println("Installation Name: " + installationName);

        SampleInstallation2 i = getSampleInstallation();
        listener.getLogger().println("forNode");
        i = i.forNode(Computer.currentComputer().getNode(), listener);
        listener.getLogger().println("forEnvironment");
        i = i.forEnvironment(build.getEnvironment(listener));

        listener.getLogger().println("Tool Home: " + i.getHome());

        return new BuildWrapper.Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
                Computer comp = Computer.currentComputer();
                Node node = comp.getNode();
                Launcher launcher = node.createLauncher(listener);
                launcher.launch().stdout(listener.getLogger()).stderr(listener.getLogger()).cmds("echo", "Tear down after build.").join();
                return true;
            }
        };
    }

    public String getInstallationName() {
        return installationName;
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public String getDisplayName() {
            return "Sample BuildWrapper";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            return true;
        }
    }
}
