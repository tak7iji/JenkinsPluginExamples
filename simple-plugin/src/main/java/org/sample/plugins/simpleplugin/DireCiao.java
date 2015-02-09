package org.sample.plugins.simpleplugin;

/**
 * Created by mash on 13/12/13.
 */

import hudson.Extension;

@Extension
public class DireCiao extends Aisatsu {
    @Override
    public String getMessage() {
        return "Ciao, ";
    }
}
