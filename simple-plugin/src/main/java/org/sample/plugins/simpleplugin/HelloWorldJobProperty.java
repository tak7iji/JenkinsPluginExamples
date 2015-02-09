package org.sample.plugins.simpleplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collection;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/20
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldJobProperty extends JobProperty<AbstractProject<?, ?>> {
    private final String prop;

    @DataBoundConstructor
    public HelloWorldJobProperty(String prop) {
        this.prop = prop;
    }

    public String getProp() {
        return prop;
    }

    @Override
    public Collection<? extends Action> getJobActions(AbstractProject<?, ?> project) {
        if (prop != null && !"".equals(prop)) return Collections.singleton(new HelloWorldJobPropertyAction(project));

        return Collections.emptyList();
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        listener.getLogger().println("JobProperty prebuild.");
        return true;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        listener.getLogger().println("JobProperty perform.");
        return true;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {

        public DescriptorImpl() {
            super(HelloWorldJobProperty.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "HelloWorld Properties";
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }
    }
}
