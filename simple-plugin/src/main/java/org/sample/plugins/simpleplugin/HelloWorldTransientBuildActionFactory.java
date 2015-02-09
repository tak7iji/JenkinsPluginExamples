package org.sample.plugins.simpleplugin;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TransientBuildActionFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/26
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
//@Extension
public class HelloWorldTransientBuildActionFactory extends TransientBuildActionFactory {
    @Override
    public Collection<? extends Action> createFor(Run target) {
        return Collections.singleton(new HelloWorldAction((AbstractBuild<?, ?>)target));
    }
}
