package org.sample.plugins.sampleplugin;

import hudson.Extension;
import hudson.model.BuildableItem;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

public class SampleTrigger extends Trigger<BuildableItem> {
    static Logger logger = Logger.getLogger("SampleTrigger");

    private boolean start;

//    @DataBoundConstructor
//    public ServerStartupTrigger(String spec) throws ANTLRException {
//        super(spec);
//    }

    @DataBoundConstructor
    public SampleTrigger() {

    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public final class WakeUpCause extends Cause {

        @Override
        public String getShortDescription() {
            return "Wake Up!";
        }
    }

    @Override
    public void start(BuildableItem project, boolean isNewInstance) {
        super.start(project, isNewInstance);
        logger.info("Start Trigger");
        start = true;

        Thread th = new Thread(new JobRunner(this));
        th.start();

    }

    @Override
    public void stop() {
        logger.info("Stop Trigger");
        start = false;
    }

    class JobRunner implements Runnable {

        private Trigger trigger;

        public JobRunner(Trigger trigger) {
            this.trigger = trigger;
        }

        public void run() {
            while (start) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                trigger.run();
            }
        }
    }

    @Override
    public void run() {
        logger.info("Run Trigger");

//        job.scheduleBuild(0, new WakeUpCause());
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends TriggerDescriptor {

        public DescriptorImpl() {
            super();
            load();
        }

        public String getDisplayName() {
            return Messages.SampleTrigger_DisplayName();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }

        @Override
        public boolean isApplicable(Item item) {
            return item instanceof BuildableItem;
        }
    }
}

