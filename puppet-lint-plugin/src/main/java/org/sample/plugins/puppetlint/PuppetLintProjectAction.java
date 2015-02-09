package org.sample.plugins.puppetlint;

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.core.ResultAction;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/11
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class PuppetLintProjectAction extends AbstractProjectAction<ResultAction<PuppetLintResult>> {
    public PuppetLintProjectAction(AbstractProject<?, ?> project) {
        super(project,
              PuppetLintResultAction.class,
              Messages._PuppetLint_ProjectAction_Name(),
              Messages._PuppetLint_ProjectAction_Trend_Name(),
              PuppetLintDescriptor.PLUGIN_ID,
              PuppetLintDescriptor.ICON_URL,
              PuppetLintDescriptor.RESULT_URL);
    }
}
