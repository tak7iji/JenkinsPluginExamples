package org.sample.plugins.sampleplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 2013/10/17
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public class SampleRecorder extends Recorder {
    private static Logger logger = Logger.getLogger("org.sample.plugins.sampleplugin.SampleRecorder");

    private final boolean status;
    private final BuildStepMonitor monitor;
    private final long wait;

    public boolean getStatus() {
        return status;
    }

    public BuildStepMonitor getMonitor() {
        return monitor;
    }

    @DataBoundConstructor
    public SampleRecorder(boolean status, BuildStepMonitor monitor, long wait) {
        this.status = status;
        this.monitor = monitor;
        this.wait = wait;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        logger.info("Recorder start.");
        build.setResult((status) ? Result.SUCCESS : Result.FAILURE);
        listener.getLogger().println("SampleRecorder!");
        try {
            Thread.sleep(wait*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        logger.info("Recorder end.");
        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        logger.info("Monitor Type: "+monitor);
        return monitor;
    }

    public long getWait() {
        return wait;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Recorder Sample";
        }
    }
}
