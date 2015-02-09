package org.sample.plugins.puppetlint;

import hudson.Extension;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/06
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
@Extension
public class PuppetLintDescriptor extends PluginDescriptor {
    static final String PLUGIN_ID = "puppetlint";
    static final String RESULT_URL = PluginDescriptor.createResultUrlName(PLUGIN_ID);
    private static final String ICON_BASE_URL = "/plugin/puppetlint/icons";
    static final String ICON_URL = ICON_BASE_URL + "/checkstyle-24x24.png";

    public PuppetLintDescriptor() {
        super(PuppetLintPublisher.class);
    }

    @Override
    public String getPluginName() {
        return PLUGIN_ID;
    }

    @Override
    public String getIconUrl() {
        return ICON_URL;
    }

    @Override
    public String getDisplayName() {
        return Messages.PuppetLint_Publisher_GetDisplayName();
    }

    @Override
    public String getSummaryIconUrl() {
        return ICON_BASE_URL + "/checkstyle-48x48.png";
    }
}
