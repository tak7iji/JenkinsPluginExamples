package org.sample.plugins.simpleplugin;

import hudson.Functions;
import hudson.model.AbstractProject;
import hudson.model.Action;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/18
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldProjectAction implements Action, Serializable {
    private final AbstractProject<?, ?> owner;
    private transient WeakReference<HelloWorldProjectSubAction> subAction;

    public HelloWorldProjectAction(AbstractProject<?, ?> project) {
        this.owner = project;
    }

    public String getIconFileName() {
        return Functions.getResourcePath() + "/plugin/simple-plugin/icons/france.png";
    }

    public String getDisplayName() {
        return "HelloWorld Project Action";
    }

    public String getUrlName() {
        return "helloworld";
    }

    // Need for displaying sidepanel.jelly
    public AbstractProject<?, ?> getOwner() {
        return owner;
    }

    public Object getDynamic(final String link, final StaplerRequest req, final StaplerResponse res) {
        HelloWorldProjectSubAction sb = null;
        WeakReference<HelloWorldProjectSubAction> wr = this.subAction;
        if (wr != null) {
            sb = wr.get();
            if (sb != null) return sb;
        }
        sb = new HelloWorldProjectSubAction(owner);
        this.subAction = new WeakReference<HelloWorldProjectSubAction>(sb);
        return sb;
    }
}
