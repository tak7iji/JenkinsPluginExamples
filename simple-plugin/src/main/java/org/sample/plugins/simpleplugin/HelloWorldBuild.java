package org.sample.plugins.simpleplugin;

import hudson.model.Build;
import hudson.model.Project;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/25
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldBuild extends Build<HelloWorldProject, HelloWorldBuild> {
    public HelloWorldBuild(HelloWorldProject project) throws IOException {
        super(project);
    }
    public HelloWorldBuild(HelloWorldProject project, File buildDir) throws IOException {
        super(project, buildDir);
    }
}
