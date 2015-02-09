package org.sample.plugins.sampleplugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/21
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class SampleInstallation2 extends ToolInstallation implements EnvironmentSpecific<SampleInstallation2>, NodeSpecific<SampleInstallation2> {
    @DataBoundConstructor
    public SampleInstallation2(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public SampleInstallation2 forEnvironment(EnvVars envVars) {
        return new SampleInstallation2(getName(), envVars.expand(getHome()), getProperties().toList());
    }

    public SampleInstallation2 forNode(Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new SampleInstallation2(getName(), translateFor(node, taskListener), getProperties().toList());
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<SampleInstallation2> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            super.configure(req, formData);
            save();
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Sample Installation 2";
        }
    }
}
