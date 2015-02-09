package org.sample.plugins.puppetlint;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import hudson.tools.ZipExtractionInstaller;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/13
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class PuppetLintInstallation extends ToolInstallation
        implements NodeSpecific<PuppetLintInstallation>, EnvironmentSpecific<PuppetLintInstallation> {

    @DataBoundConstructor
    public PuppetLintInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public PuppetLintInstallation forEnvironment(EnvVars envVars) {
        return new PuppetLintInstallation(getName(), envVars.expand(getHome()), getProperties().toList());
    }

    public PuppetLintInstallation forNode(Node node, TaskListener taskListener) throws IOException,
            InterruptedException {
        return new PuppetLintInstallation(getName(), translateFor(node, taskListener), getProperties().toList());
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<PuppetLintInstallation> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public List<ZipExtractionInstaller> getDefaultInstallers() {
            return Collections.singletonList(new ZipExtractionInstaller("",
                                                                        "https://github.com/rodjek/puppet-lint/archive/0.3.2.tar.gz",
                                                                        "puppet-lint-0.3.2"));
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            super.configure(req, formData);
            save();
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.PuppetLint_Installation_GetDisplayName();
        }
    }

}
