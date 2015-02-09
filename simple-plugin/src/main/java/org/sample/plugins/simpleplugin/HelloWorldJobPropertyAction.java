package org.sample.plugins.simpleplugin;

import hudson.Functions;
import hudson.model.AbstractProject;
import hudson.model.Action;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/18
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldJobPropertyAction implements Action, Serializable {
    private static final Logger logger = Logger.getLogger("org.sample.plugins.simpleplugin.HelloWorldAction");
    private final AbstractProject project;

    public HelloWorldJobPropertyAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return Functions.getResourcePath() + "/plugin/simple-plugin/icons/france.png";
    }

    public String getDisplayName() {
        return "HelloWorld JobProperty Action";
    }

    public String getUrlName() {
        return "";
    }
}
