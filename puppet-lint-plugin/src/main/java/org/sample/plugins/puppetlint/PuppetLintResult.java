package org.sample.plugins.puppetlint;

import com.thoughtworks.xstream.XStream;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ResultAction;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/06
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
public class PuppetLintResult extends BuildResult {
    protected PuppetLintResult(AbstractBuild<?, ?> build,
                               BuildHistory history,
                               ParserResult result,
                               String defaultEncoding) {
        super(build, history, result, defaultEncoding);
        serializeAnnotations(result.getAnnotations());
    }

    @Override
    protected void configure(final XStream xstream) {
        xstream.alias("warning", Warning.class);
    }

    @Override
    protected String getSerializationFileName() {
        return "puppet-lint-warning.xml";
    }

    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return PuppetLintResultAction.class;
    }

    @Override
    public String getSummary() {
        return "PuppetLint: " + createDefaultSummary(PuppetLintDescriptor.RESULT_URL,
                                                     getNumberOfAnnotations(),
                                                     getNumberOfModules());
    }

    @Override
    protected String createDeltaMessage() {
        return createDefaultDeltaMessage(PuppetLintDescriptor.RESULT_URL,
                                         getNumberOfNewWarnings(),
                                         getNumberOfFixedWarnings());
    }

    public String getDisplayName() {
        return Messages.PuppetLint_ProjectAction_Name();
    }

}
