package org.sample.plugins.tccontrol;

import hudson.Extension;
import hudson.tools.DownloadFromUrlInstaller;
import hudson.tools.ToolInstallation;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/09
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class TcInstaller extends DownloadFromUrlInstaller {
    private static Logger logger = Logger.getLogger("org.sample.plugins.tccontrol.TcInstaller");
    @DataBoundConstructor
    public TcInstaller(String id) {
        super(id);
        logger.info("ID: "+id);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends DownloadFromUrlInstaller.DescriptorImpl<TcInstaller> {
        public String getDisplayName() {
            return Messages.TcInstaller_DisplayName();
        }

        @Override
        public boolean isApplicable(Class<? extends ToolInstallation> toolType) {
            return toolType == TcInstallation.class;
        }
    }
}
