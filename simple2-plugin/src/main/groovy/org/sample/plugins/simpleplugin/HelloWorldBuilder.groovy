package org.sample.plugins.simpleplugin

import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.model.Computer
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import hudson.util.ArgumentListBuilder
import hudson.util.FormValidation
import net.sf.json.JSONObject
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter
import org.kohsuke.stapler.StaplerRequest

public class HelloWorldBuilder extends Builder {
    def name
    def block

    @DataBoundConstructor
    HelloWorldBuilder(String name, Block block) {
        this.name = name
        this.block = block
    }

    public static class Block {
        def text

        @DataBoundConstructor
        Block(String text) {
            this.text = text
        }
    }

    String getText() {
        block?.text
    }

    @Override
    boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        def props = Computer.currentComputer().node.toComputer().systemProperties

        def cmd = ArgumentListBuilder.newInstance()
        if (props."os.name" =~ /^Windows.*/) {
            cmd.add "cmd", "/c"
        }
        cmd.add "echo"

        cmd.add "${Messages."HelloWorldBuilder_useFrench_${getDescriptor().useFrench}"()}, $name !"
        launcher.launch().stdout(listener.logger).stderr(listener.logger).cmds(cmd).join()

        true
    }

    @Override
    DescriptorImpl getDescriptor() {
        super.getDescriptor() as DescriptorImpl
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        def useFrench
        String displayName = Messages.HelloWorldBuilder_displayName()

        DescriptorImpl() {
            load()
        }

        @Override
        boolean isApplicable(Class<? extends AbstractProject> aClass) {
            true
        }

        FormValidation doCheckName(@QueryParameter String value) {
            switch (value.size()) {
                case 0:
                    return FormValidation.error(Messages.HelloWorldBuilder_setName())
                case 1..3:
                    return FormValidation.warning(Messages.HelloWorldBuilder_tooShortName())
            }

            FormValidation.ok();
        }

        @Override
        boolean configure(StaplerRequest req, JSONObject formData) {
            useFrench = formData.getBoolean "useFrench"
            save();

            super.configure req, formData
        }
    }
}
