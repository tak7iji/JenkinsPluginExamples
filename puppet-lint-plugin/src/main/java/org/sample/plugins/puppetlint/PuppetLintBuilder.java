package org.sample.plugins.puppetlint;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.InstallSourceProperty;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class PuppetLintBuilder extends Builder {

    @DataBoundConstructor
    public PuppetLintBuilder() {
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException,
            InterruptedException {
        final Computer computer = Computer.currentComputer();
        final Node node = computer.getNode();
        final Map<Object, Object> props = node.toComputer().getSystemProperties();
        final String fileSeparator = (String) props.get("file.separator");

        AbstractProject proj = build.getProject();
        DescribableList publishers = proj.getPublishersList();
        if (publishers.get(PuppetLintPublisher.class) == null) {
            publishers.add(new PuppetLintPublisher());
        }

        String resultFile = build.getWorkspace().getRemote() + fileSeparator + "puppet-lint.csv";

        PuppetLintInstallation.DescriptorImpl desc = (PuppetLintInstallation.DescriptorImpl) Jenkins.getInstance()
                                                                                                    .getDescriptor(
                                                                                                            PuppetLintInstallation.class);
        InstallSourceProperty prop = new InstallSourceProperty(desc.getDefaultInstallers());

        PuppetLintInstallation pli = new PuppetLintInstallation("puppetlint", null, Collections.singletonList(prop));
        pli = pli.forNode(Computer.currentComputer().getNode(), listener);
        pli = pli.forEnvironment(build.getEnvironment(listener));

        FileOutputStream log = new FileOutputStream(resultFile);
        launcher.launch()
                .stdout(log)
                .stderr(listener.getLogger())
                .cmds(pli.getHome() + fileSeparator + "bin" + fileSeparator + "puppet-lint",
                      "--no-80chars-check",
                      "--log-format",
                      "%{path},%{KIND},%{linenumber},\"%{message}\"",
                      build.getWorkspace().getRemote())
                .join();
        log.close();
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Puppet-Lint";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}

