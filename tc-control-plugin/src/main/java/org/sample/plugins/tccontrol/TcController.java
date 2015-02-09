package org.sample.plugins.tccontrol;

import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import jenkins.util.xstream.XStreamDOM;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TcController extends BuildWrapper {
    private static final Pattern serverStopPtn = Pattern.compile(".*Server startup in.*");
    private static final Pattern exceptionPtn = Pattern.compile(".*Exception.*");
    private final String tcInstallationName;
    private final String javaOpts;
    private final String catalinaOpts;
    private final ServerPort serverPort;

    @DataBoundConstructor
    public TcController(String tcInstallationName, String javaOpts, String catalinaOpts, ServerPort serverPort) {
        this.tcInstallationName = tcInstallationName;
        this.javaOpts = javaOpts;
        this.catalinaOpts = catalinaOpts;
        this.serverPort = serverPort;
    }

    public TcInstallation getTcInstallation(final EnvVars env, final Node node, final BuildListener listener) {
        for (final TcInstallation installation : getDescriptor().getTcInstallations()) {
            if (tcInstallationName != null && tcInstallationName.equals(installation.getName())) {
                try {
                    return installation.forEnvironment(env).forNode(node, TaskListener.NULL);
                } catch (final IOException e) {
                    listener.fatalError("IOException while locating installation", e);
                } catch (final InterruptedException e) {
                    listener.fatalError("InterruptedException while locating installation", e);
                }
            }
        }

        return null;
    }

    @Override
    public Environment setUp(AbstractBuild build,
                                          Launcher launcher,
                                          BuildListener listener)
            throws IOException, InterruptedException {
        listener.getLogger().println("Setup TcController");
        final Computer computer = Computer.currentComputer();
        final Node node = computer.getNode();
        final EnvVars env = computer.getEnvironment();
        final Map<Object, Object> props = node.toComputer().getSystemProperties();
        final String fileSeparator = (String) props.get("file.separator");
        final String pathSeparator = (String) props.get("path.separator");

        TcInstallation installation = getTcInstallation(env, node, listener);
        final String catalinaHome = installation.getHome();
        final String classPath = catalinaHome + fileSeparator + "bin" + fileSeparator + "bootstrap.jar" + pathSeparator + catalinaHome + fileSeparator + "bin" + fileSeparator + "tomcat-juli.jar";
        final String catalinaBaseOption = "-Dcatalina.base=" + catalinaHome;
        final String catalinaHomeOption = "-Dcatalina.home=" + catalinaHome;
        final String tempDirOption = "-Dcatalina.tmpdir=" + catalinaHome + fileSeparator + "temp";
        final String logginManagerOption = "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager";
        final String loggingConfigFileOption = "-Djava.util.logging.config.file=" + catalinaHome + fileSeparator + "conf" + fileSeparator + "logging.properties";
        final String serverXmlFile = catalinaHome + fileSeparator + "conf" + fileSeparator + "server.xml";

        if (serverPort != null) {
            modifyServerPort(node, serverXmlFile, listener);
        }

        final ArgumentListBuilder startCmd = new ArgumentListBuilder("java");

        startCmd.add(loggingConfigFileOption);
        startCmd.add(logginManagerOption);
        if (javaOpts != null && !"".equals(javaOpts)) startCmd.add(javaOpts);
        if (catalinaOpts != null && !"".equals(catalinaOpts)) startCmd.add(catalinaOpts);
        startCmd.add("-classpath", classPath);
        startCmd.add(catalinaBaseOption);
        startCmd.add(catalinaHomeOption);
        startCmd.add(tempDirOption);
        startCmd.add("org.apache.catalina.startup.Bootstrap", "start");

        final Launcher.ProcStarter starter = launcher.launch();
        Proc proc = starter.cmds(startCmd).stdout(listener.getLogger()).stderr(listener.getLogger()).readStdout().start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getStdout(), (String) props.get("sun.jnu.encoding")));
        String line;
        while ((line = reader.readLine()) != null) {
            listener.getLogger().println(line);
            if (serverStopPtn.matcher(line).matches()) {
                break;
            } else if(exceptionPtn.matcher(line).matches()) {
                proc.kill();
            }
        }

        if(!proc.isAlive()){
            listener.getLogger().println("Tc process terminated.");
            restoreFile(node, serverXmlFile);
            return null;
        }

        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
                listener.getLogger().println("Tear down!");

                final ArgumentListBuilder stopCmd = new ArgumentListBuilder("java");
                stopCmd.add(logginManagerOption);
                if (javaOpts != null && !"".equals(javaOpts)) stopCmd.add(javaOpts);
                stopCmd.add("-classpath", classPath);
                stopCmd.add(catalinaBaseOption);
                stopCmd.add(catalinaHomeOption);
                stopCmd.add(tempDirOption);
                stopCmd.add("org.apache.catalina.startup.Bootstrap", "stop");

                starter.cmds(stopCmd).stdout(listener.getLogger()).stderr(listener.getLogger()).join();
                restoreFile(node, serverXmlFile);
                return true;
            }
        };
    }

    private void restoreFile(Node node, String serverXmlFile) throws IOException, InterruptedException {
        FilePath original = node.createPath(serverXmlFile + ".original");
        if (original.exists()) {
            node.createPath(serverXmlFile).delete();
            original.renameTo(node.createPath(serverXmlFile));
        }
    }

    private void modifyServerPort(Node node, String serverXmlFile, BuildListener listener) throws IOException, InterruptedException {
        FilePath serverXml = node.createPath(serverXmlFile);
        serverXml.copyTo(node.createPath(serverXmlFile + ".original"));

        XStreamDOM dom = XStreamDOM.from(serverXml.read());
        List<XStreamDOM> newChildren = new ArrayList<XStreamDOM>();
        for (XStreamDOM child : dom.getChildren()) {
            if ("Service".equals(child.getTagName())) {
                newChildren.add(newService(child));
            } else {
                newChildren.add(child);
            }
        }

        Map<String, String> attrMap = dom.getAttributeMap();
        for (String key : attrMap.keySet()) {
            if (serverPort.shutdownPort != null && !"".equals(serverPort.shutdownPort))
                attrMap.put("port", serverPort.shutdownPort);
        }
        XStreamDOM newDom = new XStreamDOM(dom.getTagName(), attrMap, newChildren);
        try {
            newDom.writeTo(serverXml.write());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private XStreamDOM newService(XStreamDOM service) {
        List<XStreamDOM> newChildren = new ArrayList<XStreamDOM>();

        for (XStreamDOM child : service.getChildren()) {
            Map<String, String> attrMap = child.getAttributeMap();

            if ("Connector".equals(child.getTagName())) {
                if (attrMap.get("protocol").toLowerCase().contains("http")) {
                    if (serverPort.httpPort != null && !"".equals(serverPort.httpPort))
                        attrMap.put("port", serverPort.httpPort);
                } else {
                    if (serverPort.ajpPort != null && !"".equals(serverPort.ajpPort))
                        attrMap.put("port", serverPort.ajpPort);
                }
            }

            newChildren.add(new XStreamDOM(child.getTagName(), attrMap, child.getChildren()));
        }

        return new XStreamDOM(service.getTagName(), service.getAttributeMap(), newChildren);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getJavaOpts() {
        return javaOpts;
    }

    public String getCatalinaOpts() {
        return catalinaOpts;
    }

    public ServerPort getServerPort() {
        return serverPort;
    }

    public String getShutdownPort() {
        return (serverPort != null) ? serverPort.shutdownPort : null;
    }

    public String getHttpPort() {
        return (serverPort != null) ? serverPort.httpPort : null;
    }

    public String getAjpPort() {
        return (serverPort != null) ? serverPort.ajpPort : null;
    }

    public static class ServerPort {
        protected final String shutdownPort;
        protected final String httpPort;
        protected final String ajpPort;

        @DataBoundConstructor
        public ServerPort(String shutdownPort, String httpPort, String ajpPort) {
            this.shutdownPort = shutdownPort;
            this.httpPort = httpPort;
            this.ajpPort = ajpPort;
        }

        public String getShutdownPort() {
            return shutdownPort;
        }

        public String getHttpPort() {
            return httpPort;
        }

        public String getAjpPort() {
            return ajpPort;
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {
        TcInstallation[] tcInstallations = new TcInstallation[0];

        public DescriptorImpl() {
            super();
            load();
        }

        public boolean isApplicable(AbstractProject<?, ?> aProject) {
            return true;
        }

        public ListBoxModel doFillTcInstallationNameItems() {
            ListBoxModel items = new ListBoxModel();
            for (TcInstallation installation : getTcInstallations()) {
                items.add(installation.getName());
            }
            return items;
        }

        public String getDisplayName() {
            return Messages.TcController_DisplayName();
        }

        public TcInstallation[] getTcInstallations() {
            return tcInstallations;
        }

        public void setTcInstallations(TcInstallation[] installations) {
            this.tcInstallations = installations;
            save();
        }
    }
}

