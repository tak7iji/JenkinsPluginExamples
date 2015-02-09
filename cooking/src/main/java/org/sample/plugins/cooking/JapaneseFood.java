package org.sample.plugins.cooking;

import hudson.Extension;
import hudson.model.BuildListener;
import hudson.model.Result;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by mash on 13/12/13.
 */
public class JapaneseFood extends Food {
    @DataBoundConstructor
    public JapaneseFood(String name, String precook, String cook, String cleanup) {
        super(name, precook, cook, cleanup);
    }

    @Override
    public Result cooking(BuildListener buildListener) {
        buildListener.getLogger().println(getPrecook());
        buildListener.getLogger().println(getCook());
        return Result.SUCCESS;
    }

    @Override
    public void cleanup(BuildListener buildListener) {
        buildListener.getLogger().println(getCleanup());
    }

    @Override
    public void post(BuildListener buildListener) {
        //No-op
    }

    @Extension
    public static class DescriptorImpl extends FoodDescriptor {

        @Override
        public String getDisplayName() {
            return "Japanese Food";
        }
    }
}
