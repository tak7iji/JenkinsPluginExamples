package org.sample.plugins.puppetlint;

import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.*;
import hudson.plugins.analysis.util.PluginLogger;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/06
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class PuppetLintPublisher extends HealthAwarePublisher {
    private static Logger logger = Logger.getLogger("org.sample.plugins.puppetlint.PuppetLintPublisher");
    static final String PLUGIN_NAME = "PUPPETLINT";

    public PuppetLintPublisher() {
        this(null,
             null,
             null,
             null,
             false,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             false,
             false,
             false,
             false,
             false);
    }

    @DataBoundConstructor
    public PuppetLintPublisher(String healthy,
                               String unHealthy,
                               String thresholdLimit,
                               String defaultEncoding,
                               boolean useDeltaValues,
                               String unstableTotalAll,
                               String unstableTotalHigh,
                               String unstableTotalNormal,
                               String unstableTotalLow,
                               String unstableNewAll,
                               String unstableNewHigh,
                               String unstableNewNormal,
                               String unstableNewLow,
                               String failedTotalAll,
                               String failedTotalHigh,
                               String failedTotalNormal,
                               String failedTotalLow,
                               String failedNewAll,
                               String failedNewHigh,
                               String failedNewNormal,
                               String failedNewLow,
                               boolean canRunOnFailed,
                               boolean useStableBuildAsReference,
                               boolean shouldDetectModules,
                               boolean canComputeNew,
                               boolean canResolveRelativePaths) {
        super(healthy,
              unHealthy,
              thresholdLimit,
              defaultEncoding,
              useDeltaValues,
              unstableTotalAll,
              unstableTotalHigh,
              unstableTotalNormal,
              unstableTotalLow,
              unstableNewAll,
              unstableNewHigh,
              unstableNewNormal,
              unstableNewLow,
              failedTotalAll,
              failedTotalHigh,
              failedTotalNormal,
              failedTotalLow,
              failedNewAll,
              failedNewHigh,
              failedNewNormal,
              failedNewLow,
              canRunOnFailed,
              useStableBuildAsReference,
              shouldDetectModules,
              canComputeNew,
              canResolveRelativePaths,
              PLUGIN_NAME);
    }

    @Override
    protected BuildResult perform(AbstractBuild<?, ?> build, PluginLogger pluginLogger) throws InterruptedException,
            IOException {
        FilesParser parser = new FilesParser(PLUGIN_NAME, "**/puppet-lint.csv",
                                             new PuppetLintParser(getDefaultEncoding()),
                                             shouldDetectModules(), isMavenBuild(build));
        ParserResult project = build.getWorkspace().act(parser);

        PuppetLintResult result = new PuppetLintResult(build,
                                                       new BuildHistory(build,
                                                                        PuppetLintResultAction.class,
                                                                        getUseStableBuildAsReference()),
                                                       project,
                                                       getDefaultEncoding());
        pluginLogger.log(result.toString());
        build.getActions().add(new PuppetLintResultAction(build, this, result));

        return result;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new PuppetLintProjectAction(project);
    }

    @Override
    public PuppetLintDescriptor getDescriptor() {
        return (PuppetLintDescriptor) super.getDescriptor();
    }

    public MatrixAggregator createAggregator(MatrixBuild build, Launcher launcher, BuildListener listener) {
        return null;
    }
}
