package org.sample.plugins.simpleplugin;

import hudson.Functions;
import hudson.model.AbstractBuild;
import jenkins.model.ModelObjectWithContextMenu;
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
public class HelloWorldSubAction implements ModelObjectWithContextMenu {
    private final AbstractBuild<?, ?> owner;
    private transient WeakReference<HelloWorldSubAction2> subAction2;

    public HelloWorldSubAction(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

    public String getDisplayName() {
        return "HelloWorldSubAction";
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public Object getDynamic(final String link, final StaplerRequest req, final StaplerResponse res) {
        HelloWorldSubAction2 sb = null;
        WeakReference<HelloWorldSubAction2> wr = this.subAction2;
        if (wr != null) {
            sb = wr.get();
            if (sb != null) return sb;
        }
        sb = new HelloWorldSubAction2(owner);
        this.subAction2 = new WeakReference<HelloWorldSubAction2>(sb);
        return sb;
    }

    public ContextMenu doContextMenu(StaplerRequest req, StaplerResponse res) throws Exception {
        return new ContextMenu().add("sub2", "HelloWorldSubAction2")
                .add(owner.getProject().getAbsoluteUrl() + "helloworld",
                        req.getContextPath() + Functions.getResourcePath() + "/plugin/simple-plugin/icons/france.png",
                        "Goto Project Action");
    }
}
