package org.sample.plugins.simpleplugin;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import hudson.util.Graph;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/19
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldSubAction2 implements ModelObject {
    private final AbstractBuild<?, ?> owner;
    private transient WeakReference<HelloWorldSubAction3> subAction3;

    public HelloWorldSubAction2(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

    public String getDisplayName() {
        return "HelloWorldSubAction2";
    }

    public AbstractBuild<?, ?> getOwner() {
        return (AbstractBuild<?, ?>) owner;
    }

    public Graph getGraph() {
        return new HelloWorldGraph2(owner);
    }

    public Object getDynamic(final String link, final StaplerRequest req, final StaplerResponse res) {
        HelloWorldSubAction3 sb = null;
        WeakReference<HelloWorldSubAction3> wr = this.subAction3;
        if (wr != null) {
            sb = wr.get();
            if (sb != null) return sb;
        }
        sb = new HelloWorldSubAction3(owner);
        this.subAction3 = new WeakReference<HelloWorldSubAction3>(sb);
        return sb;
    }
}
