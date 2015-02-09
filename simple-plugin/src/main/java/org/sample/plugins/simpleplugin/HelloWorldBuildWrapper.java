package org.sample.plugins.simpleplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 2013/11/21
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldBuildWrapper extends BuildWrapper {
    @DataBoundConstructor
    public HelloWorldBuildWrapper() {
    }

    @Override
    public BuildWrapper.Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {
        listener.getLogger().println("BuildWrapper setUp");

        return new BuildWrapper.Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) {
                listener.getLogger().println("BuildWrapper tearDown");
                return true;
            }
        };
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public String getDisplayName() {
            return "HelloWorld BuildWrapper";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            return true;
        }
    }
}
