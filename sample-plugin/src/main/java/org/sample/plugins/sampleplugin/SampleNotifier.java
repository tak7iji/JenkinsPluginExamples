package org.sample.plugins.sampleplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/18
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class SampleNotifier extends Notifier {
    private static Logger logger = Logger.getLogger("org.sample.plugins.sampleplugin.SampleNotifier");
    private final boolean status;
    private final long wait;

    @DataBoundConstructor
    public SampleNotifier(boolean status, long wait) {

        this.status = status;
        this.wait = wait;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        logger.info("Notifier start.");
        build.setResult((status) ? Result.SUCCESS : Result.FAILURE);
        listener.getLogger().println("SampleNotifier!");
        try {
            Thread.sleep(wait*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        logger.info("Notifier end.");
        return true;
    }

    public boolean isStatus() {
        return status;
    }

    public long getWait() {
        return wait;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Notifier Sample";
        }
    }
}
