package org.sample.plugins.tccontrol;

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
 * Date: 13/10/09
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class TcInstallation extends ToolInstallation implements NodeSpecific<TcInstallation>, EnvironmentSpecific<TcInstallation> {

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<TcInstallation> {

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            final List<TcInstallation> boundList = req.bindJSONToList(TcInstallation.class, json.get("tool"));

            setInstallations(boundList.toArray(new TcInstallation[boundList.size()]));

            return true;
        }

        public FormValidation doCheckName(@QueryParameter final String value) {
            return FormValidation.validateRequired(value);
        }

        @Override
        public String getDisplayName() {
            return Messages.TcInstallation_DisplayName();
        }

        @Override
        public TcInstallation[] getInstallations() {
            return Hudson.getInstance().getDescriptorByType(TcController.DescriptorImpl.class).getTcInstallations();
        }

        public DescriptorImpl getToolDescriptor() {
            return ToolInstallation.all().get(DescriptorImpl.class);
        }

        @Override
        public void setInstallations(final TcInstallation... installations) {
            Hudson.getInstance().getDescriptorByType(TcController.DescriptorImpl.class).setTcInstallations(installations);
        }
    }

    @DataBoundConstructor
    public TcInstallation(final String name, final String home, final List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public TcInstallation forEnvironment(EnvVars envVars) {
        return new TcInstallation(getName(), envVars.expand(getHome()), getProperties().toList());
    }

    public TcInstallation forNode(Node node, TaskListener taskListener) throws IOException, InterruptedException {
        return new TcInstallation(getName(), translateFor(node, taskListener), getProperties().toList());
    }
}
