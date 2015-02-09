package org.sample.plugins.sampleplugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.*;
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
public class PuppetInstallation extends ToolInstallation implements NodeSpecific<PuppetInstallation>, EnvironmentSpecific<PuppetInstallation> {

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<PuppetInstallation> {

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws Descriptor.FormException {
            final List<PuppetInstallation> boundList = req.bindJSONToList(PuppetInstallation.class, json.get("tool"));

            setInstallations(boundList.toArray(new PuppetInstallation[boundList.size()]));

            return true;
        }

        public FormValidation doCheckName(@QueryParameter final String value) {
            return FormValidation.validateRequired(value);
        }

        @Override
        public String getDisplayName() {
            return Messages.PuppetInstallation_DisplayName();
        }

        @Override
        public PuppetInstallation[] getInstallations() {
            return Hudson.getInstance().getDescriptorByType(PuppetBuilder.DescriptorImpl.class).getPuppetInstallations();
        }

        public DescriptorImpl getToolDescriptor() {
            return ToolInstallation.all().get(DescriptorImpl.class);
        }

        @Override
        public void setInstallations(final PuppetInstallation... installations) {
            Hudson.getInstance().getDescriptorByType(PuppetBuilder.DescriptorImpl.class).setPuppetInstallations(installations);
        }
    }

    @DataBoundConstructor
    public PuppetInstallation(final String name, final String home, final List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public PuppetInstallation forEnvironment(EnvVars envVars) {
        return new PuppetInstallation(getName(), envVars.expand(getHome()), getProperties().toList());
    }

    public PuppetInstallation forNode(Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new PuppetInstallation(getName(), translateFor(node, taskListener), getProperties().toList());
    }
}
