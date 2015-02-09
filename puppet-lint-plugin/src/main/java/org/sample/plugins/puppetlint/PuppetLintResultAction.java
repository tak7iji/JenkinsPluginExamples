package org.sample.plugins.puppetlint;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/06
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class PuppetLintResultAction extends AbstractResultAction<PuppetLintResult> {
    public PuppetLintResultAction(AbstractBuild<?, ?> owner,
                                  HealthDescriptor healthDescriptor,
                                  PuppetLintResult result) {
        super(owner, new PuppetLintHealthDescriptor(healthDescriptor), result);
    }

    @Override
    protected PluginDescriptor getDescriptor() {
        return new PuppetLintDescriptor();
    }

    public String getDisplayName() {
        return Messages.PuppetLint_ProjectAction_Name();
    }
}
