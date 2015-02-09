package org.sample.plugins.simpleplugin;

import hudson.Functions;
import hudson.model.*;
import hudson.util.HttpResponses;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/18
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
@ExportedBean
public class HelloWorldAction implements Action {
    private static final Logger logger = Logger.getLogger("org.sample.plugins.simpleplugin.HelloWorldAction");
    private final AbstractBuild<?, ?> owner;
    private transient WeakReference<HelloWorldSubAction> subAction;
    private static String message2 = "HelloWorldAction";
    public static ModelObject[] actions = {new HelloWorldSubAction4()};
    public static List<? extends ModelObject> actionsList = Arrays.asList(actions);
    public static Map<String, ? extends ModelObject> actionsMap = Collections.singletonMap("hwsa4", new HelloWorldSubAction4());

    public HelloWorldAction(AbstractBuild<?, ?> build) {
        this.owner = build;
    }

    public String getIconFileName() {
        // /plugin/<artifactId>/path/to/image
        return Functions.getResourcePath() + "/plugin/simple-plugin/icons/france.png";
//        return null;
    }

    @Exported
    public String getDisplayName() {
        return "HelloWorld Action";
    }

    public String getMessage() {
        return "Summary";
    }

    public String getUrlName() {
        return "simpleplugin";
    }

    // Need for displaying sidepanel.jelly
    @Exported
    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public int getNumber() {
        return owner.getNumber();
    }

    public Object getDynamic(final String link, final StaplerRequest req, final StaplerResponse res) {
        // Dynamic Action Method => /sub
        return getObject();
    }

    private Object getObject() {
        HelloWorldSubAction sb = null;
        WeakReference<HelloWorldSubAction> wr = this.subAction;
        if (wr != null) {
            sb = wr.get();
            if (sb != null) return sb;
        }
        sb = new HelloWorldSubAction(owner);
        this.subAction = new WeakReference<HelloWorldSubAction>(sb);

        return sb;
    }

    // String Getter => /message2
    public Object getMessage2() {
        return getObject();
    }

    // Action Method => /hello
    public Object doHello() {
        logger.info("Hello");
        return new HelloWorldSubAction5();
    }

    public HttpResponse doIndex() {
        return HttpResponses.plainText("OK");
    }

    public Api getApi() {
        return new Api(this);
    }
}
