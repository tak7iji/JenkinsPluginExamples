package org.sample.plugins.simpleplugin;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import org.kohsuke.stapler.StaplerProxy;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/19
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldSubAction4 implements ModelObject, StaplerProxy {
    public HelloWorldSubAction4() {
    }

    public String getDisplayName() {
        return "HelloWorldSubAction4";
    }

    public Object getTarget() {
        return new HelloWorldSubAction5();
    }
}
