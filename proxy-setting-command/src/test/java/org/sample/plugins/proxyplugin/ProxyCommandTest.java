package org.sample.plugins.proxyplugin;

import hudson.cli.CLICommand;
import hudson.cli.CLICommandInvoker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/01
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class ProxyCommandTest {
    @Rule
    public final JenkinsRule j = new JenkinsRule();

    private CLICommandInvoker command;

    @Before
    public void setUp() {
        command = new CLICommandInvoker(j, new ProxySettingCommand());
    }

    @Test
    public void commandTest() {
        CLICommandInvoker.Result result = command.invoke();
        j.assertStringContains(result.stderr(), "Argument \"PROXY_HOST\" is required");
        System.out.println("Stderr: "+result.stderr());
    }

    @Test
    public void illegalOption() {
        CLICommandInvoker.Result result = command.invokeWithArgs("aaa", "bbb");
        j.assertStringContains(result.stderr(), "\"bbb\" is not a valid value for \"aaa\"");
        System.out.println("Stderr: "+result.stderr());
    }
}
