package org.sample.plugins.sampleplugin;

import hudson.Extension;
import hudson.tools.DownloadFromUrlInstaller;
import hudson.tools.ToolInstallation;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 2013/10/03
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class PuppetInstaller extends DownloadFromUrlInstaller {

    @DataBoundConstructor
    public PuppetInstaller(String id) {
        super(id);
    }

    @Extension
    public static final class DescriptorImpl extends DownloadFromUrlInstaller.DescriptorImpl<PuppetInstaller> {
        public String getDisplayName() {
            return Messages.PuppetInstaller_DisplayName();
        }

        @Override
        public boolean isApplicable(Class<? extends ToolInstallation> toolType) {
            return toolType == PuppetInstallation.class;
        }
    }

}
