package org.sample.plugins.simpleplugin;

import hudson.Extension;
import hudson.model.Api;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;
import jenkins.model.AbstractTopLevelItem;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/20
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldTopLevelItem extends AbstractTopLevelItem {
    public HelloWorldTopLevelItem(ItemGroup parent, String name) {
        super(parent, name);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends TopLevelItemDescriptor {

        @Override
        public String getDisplayName() {
            return "HelloWorld TopLevelItem";
        }

        @Override
        public TopLevelItem newInstance(ItemGroup itemGroup, String s) {
            return new HelloWorldTopLevelItem(itemGroup, s);
        }
    }
}
