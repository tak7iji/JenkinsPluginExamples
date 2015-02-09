package org.sample.plugins.proxyplugin;

import hudson.Extension;
import hudson.ProxyConfiguration;
import hudson.cli.CLICommand;
import jenkins.model.Jenkins;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Extension
public class ProxySettingCommand extends CLICommand {
    @Argument(index=0, required=true, metaVar="PROXY_HOST", usage="Proxy Hostname")
    private String host;

    @Argument(index=1, required=true, metaVar="PROXY_PORT", usage="Proxy Port")
    private int port;

    @Option(name="-testurl", usage="url for connection test")
    private String testUrl;

    @Option(name="-timeout", usage="connection timeout")
    private int timeout;

    @Option(name="-testonly", usage="not save setting")
    private boolean testonly;

    @Override
    public String getShortDescription() {
        return "proxy-setting";
    }

    @Override
    protected int run() throws Exception {
        int status = 0;

        Jenkins jenkins = Jenkins.getInstance();

        ProxyConfiguration config = new ProxyConfiguration(host, port);
        jenkins.proxy = config;

        if (testUrl != null) {
            try {
                URLConnection conn = ProxyConfiguration.open(new URL(testUrl));
                if (timeout > 0) {
                    conn.setConnectTimeout(timeout);
                }
                conn.connect();
            } catch (Exception e) {
                status = 1;
                stdout.println("Invalid proxy host or port.");
            }
        }
        if (!testonly && status == 0) {
            jenkins.proxy.save();
        } else {
            jenkins.proxy = null;
        }

        return status;
    }
}

