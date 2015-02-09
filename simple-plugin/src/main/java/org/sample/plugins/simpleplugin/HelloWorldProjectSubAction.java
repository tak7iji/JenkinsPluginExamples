package org.sample.plugins.simpleplugin;

import hudson.model.AbstractProject;
import hudson.model.ModelObject;
import hudson.util.Graph;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/19
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldProjectSubAction implements ModelObject {
    private final AbstractProject<?, ?> owner;

    public HelloWorldProjectSubAction(AbstractProject<?, ?> owner) {
        this.owner = owner;
    }

    public String getDisplayName() {
        return "HelloWorldSubAction2";
    }

    public AbstractProject<?, ?> getOwner() {
        return owner;
    }

    public Graph getGraph() {
        return new HelloWorldGraph(owner);
    }
}
