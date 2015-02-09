package org.sample.plugins.cooking;

import hudson.model.Descriptor;

/**
 * Created by mash on 13/12/13.
 */
public abstract class FoodDescriptor extends Descriptor<Food> {
    protected FoodDescriptor(Class<? extends Food> clazz) {
        super(clazz);
    }
    protected FoodDescriptor() {
    }
}
