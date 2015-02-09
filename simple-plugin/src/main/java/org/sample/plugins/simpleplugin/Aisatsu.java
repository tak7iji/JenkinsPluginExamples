package org.sample.plugins.simpleplugin;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Hudson;

/**
 * Created by mash on 13/12/13.
 */
public abstract class Aisatsu implements ExtensionPoint {
    public static ExtensionList<Aisatsu> all() {
        return Hudson.getInstance().getExtensionList(Aisatsu.class);
    }

    public abstract String getMessage();

    @Extension(ordinal = -1)
    public static class SayHello extends Aisatsu {

        @Override
        public String getMessage() {
            return "Hello, ";
        }
    }
}
