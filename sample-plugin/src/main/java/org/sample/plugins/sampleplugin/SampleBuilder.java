package org.sample.plugins.sampleplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 2013/10/19
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public class SampleBuilder extends Builder {
    private static Logger logger = Logger.getLogger("org.sample.plugins.sampleplugin.SampleBuilder");
    private String sampleInstallationName;

    @DataBoundConstructor
    public SampleBuilder(String sampleInstallationName) {
        this.sampleInstallationName = sampleInstallationName;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        logger.info("Builder start.");
        SampleInstallation si = getSampleInstallation();
        si = si.forNode(Computer.currentComputer().getNode(), listener);
        si = si.forEnvironment(build.getEnvironment(listener));

        listener.getLogger().println(si.getName() + ":" + si.getHome());
        return true;
    }

    public String getSampleInstallationName() {
        return sampleInstallationName;
    }

    public SampleInstallation getSampleInstallation() {
        for (SampleInstallation i : getDescriptor().getInstallations()) {
            if (sampleInstallationName != null && sampleInstallationName.equals(i.getName()))
                return i;
        }
        return null;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private SampleInstallation[] installations = new SampleInstallation[0];

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Sample Builder";
        }

        public SampleInstallation[] getInstallations() {
            return installations;
        }

        public void setInstallations(SampleInstallation[] installations) {
            List<SampleInstallation> tmpList = new ArrayList<SampleInstallation>();
            // remote empty Sample installation :
            if (installations != null) {
                Collections.addAll(tmpList, installations);
                for (SampleInstallation installation : installations) {
                    if (Util.fixEmptyAndTrim(installation.getName()) == null) {
                        tmpList.remove(installation);
                    }
                }
            }
            this.installations = tmpList.toArray(new SampleInstallation[tmpList.size()]);
            save();
        }
    }
}
