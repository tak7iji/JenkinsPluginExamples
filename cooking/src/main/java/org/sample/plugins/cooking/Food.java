package org.sample.plugins.cooking;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Hudson;
import hudson.model.Result;

/**
 * Created by mash on 13/12/13.
 */
public abstract class Food implements Describable<Food>, ExtensionPoint {
    private final String name;
    private final String precook;
    private final String cook;
    private final String cleanup;

    public Food(String name, String precook, String cook, String cleanup) {
        this.name = name;
        this.precook = precook;
        this.cook = cook;
        this.cleanup = cleanup;
    }

    public FoodDescriptor getDescriptor() {
        return (FoodDescriptor) Hudson.getInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<Food, FoodDescriptor> all() {
        return Hudson.getInstance().<Food, FoodDescriptor>getDescriptorList(Food.class);
    }

    public String getName() {
        return name;
    }

    public String getPrecook() {
        return precook;
    }

    public String getCook() {
        return cook;
    }

    public String getCleanup() {
        return cleanup;
    }

    public abstract Result cooking(BuildListener buildListener);

    public abstract void cleanup(BuildListener buildListener);

    public abstract void post(BuildListener buildListener);
}
