package org.sample.plugins.sampleplugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolProperty;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/18
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class SampleInstallation extends ToolInstallation implements EnvironmentSpecific<SampleInstallation>, NodeSpecific<SampleInstallation> {
    static Logger logger = Logger.getLogger("org.sample.plugins.sampleplugin.SampleInstallation");

    @DataBoundConstructor
    public SampleInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public SampleInstallation forEnvironment(EnvVars envVars) {
        return new SampleInstallation(getName(), envVars.expand(getHome()), getProperties().toList());
    }

    public SampleInstallation forNode(Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new SampleInstallation(getName(), translateFor(node, taskListener), getProperties().toList());
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<SampleInstallation> {
        @Override
        public String getDisplayName() {
            return "Sample Installation";
        }

        @Override
        public List<? extends ToolInstaller> getDefaultInstallers() {
            return Collections.singletonList(new SampleInstaller(null));
        }

        // overriding them for backward compatibility.
        // newer code need not do this
        @Override
        public SampleInstallation[] getInstallations() {
            return Jenkins.getInstance().getDescriptorByType(SampleBuilder.DescriptorImpl.class).getInstallations();
        }

        // overriding them for backward compatibility.
        // newer code need not do this
        @Override
        public void setInstallations(SampleInstallation... installations) {
            Jenkins.getInstance().getDescriptorByType(SampleBuilder.DescriptorImpl.class).setInstallations(installations);
        }


    }
}
