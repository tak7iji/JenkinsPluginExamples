package org.sample.plugins.sampleplugin;

import hudson.Extension;
import hudson.tools.DownloadFromUrlInstaller;
import hudson.tools.ToolInstallation;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/18
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class SampleInstaller extends DownloadFromUrlInstaller {
    @DataBoundConstructor
    public SampleInstaller(String id) {
        super(id);
    }

    @Extension
    public static class DescriptorImpl extends DownloadFromUrlInstaller.DescriptorImpl<SampleInstaller> {

        @Override
        public String getDisplayName() {
            return "Sample Installer";
        }

        @Override
        public boolean isApplicable(Class<? extends ToolInstallation> toolType) {
            return toolType == SampleInstallation.class;
        }
    }
}
