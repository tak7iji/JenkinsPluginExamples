package org.sample.plugins.sampleplugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/02
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public class FacterInstallation extends ToolInstallation implements NodeSpecific<FacterInstallation>, EnvironmentSpecific<FacterInstallation> {

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<FacterInstallation> {

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            final List<FacterInstallation> boundList = req.bindJSONToList(FacterInstallation.class, json.get("tool"));

            setInstallations(boundList.toArray(new FacterInstallation[boundList.size()]));

            return true;
        }

        public FormValidation doCheckName(@QueryParameter final String value) {
            return FormValidation.validateRequired(value);
        }

        @Override
        public String getDisplayName() {
            return Messages.FacterInstallation_DisplayName();
        }

        @Override
        public FacterInstallation[] getInstallations() {
            return Hudson.getInstance().getDescriptorByType(PuppetBuilder.DescriptorImpl.class).getFacterInstallations();
        }

        public DescriptorImpl getToolDescriptor() {
            return ToolInstallation.all().get(DescriptorImpl.class);
        }

        @Override
        public void setInstallations(final FacterInstallation... installations) {
            Hudson.getInstance().getDescriptorByType(PuppetBuilder.DescriptorImpl.class).setFacterInstallations(installations);
        }
    }

    @DataBoundConstructor
    public FacterInstallation(final String name, final String home, final List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public FacterInstallation forEnvironment(EnvVars envVars) {
        return new FacterInstallation(getName(), envVars.expand(getHome()), getProperties().toList());
    }

    public FacterInstallation forNode(Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new FacterInstallation(getName(), translateFor(node, taskListener), getProperties().toList());
    }
}
