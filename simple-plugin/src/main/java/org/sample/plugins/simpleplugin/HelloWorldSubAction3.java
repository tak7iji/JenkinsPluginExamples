package org.sample.plugins.simpleplugin;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import hudson.util.Graph;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/19
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldSubAction3 implements ModelObject {
    private final AbstractBuild<?, ?> owner;

    public HelloWorldSubAction3(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

    public String getDisplayName() {
        return "HelloWorldSubAction3";
    }

    public AbstractBuild<?, ?> getOwner() {
        return (AbstractBuild<?, ?>) owner;
    }
}
