package org.sample.plugins.simpleplugin;

import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/25
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldProject extends Project<HelloWorldProject, HelloWorldBuild> implements TopLevelItem {
    private String test;

    public HelloWorldProject(ItemGroup<Item> parent, String name) {
        super(parent, name);
    }

    @Override
    protected Class<HelloWorldBuild> getBuildClass() {
        return HelloWorldBuild.class;
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public String getTest() {
        return test;
    }

    @Override
    public void submit(StaplerRequest req, StaplerResponse res)
            throws ServletException, Descriptor.FormException, IOException {
        super.submit(req, res);
        test = Util.fixEmpty(req.getParameter("test").trim());

        JSONObject json = req.getSubmittedForm();

        getBuildersList().rebuild(req, json, Builder.all());

        getBuildWrappersList().add(new HelloWorldBuildWrapper());
        getPublishersList().add(new HelloWorldNotifier());
        getPublishersList().add(new HelloWorldRecorder());
    }

    @Extension
    public static class DescriptorImpl extends AbstractProjectDescriptor {

        @Override
        public String getDisplayName() {
            return "HelloWorld Project";
        }

        @Override
        public HelloWorldProject newInstance(ItemGroup itemGroup, String s) {
            final HelloWorldProject hwp = new HelloWorldProject(itemGroup, s);
            return hwp;
        }
    }
}
