package org.sample.plugins.tccontrol;

import hudson.ProxyConfiguration;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
import hudson.tools.*;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/10/29
 * Time: 13:39
 * To change this template use File | Settings | File Templates.
 */
public class TcControlTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void beforeTest() throws Exception {
        // 社内環境などプロキシが必要な場合はここで設定しておく
        ProxyConfiguration config = new ProxyConfiguration("", 80);
        j.getInstance().proxy = config;
        j.getInstance().proxy.save();
    }

    @Test
    public void startStopTest() {
        try {
            // *.zip/*.tar.gzをインストールする場合のToolInstallerを定義
            // ZipExtractionInstallerを利用
            // 第一引数はラベル -> このテストではラベル指定しないのでnullのままとする
            // 第二引数はダウンロードURL
            // 第三引数は展開されるディレクトリ
            ZipExtractionInstaller zipInstaller = new ZipExtractionInstaller(null, "http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.42/bin/apache-tomcat-7.0.42.zip", "apache-tomcat-7.0.42");

            installTest(zipInstaller);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // @LocalDataを付与することでテスト用のリソースを利用することを宣言する
    // 詳細はhttps://wiki.jenkins-ci.org/display/JENKINS/Unit+Test#UnitTest-@LocalDataを参照
    // 今回はconfig.xml（中身は<hudson></hudson>のみ）とupdates/org.sample.plugins.tccontrol.TcInstallerファイルのみ配置しておく
    // テスト時にupdates/org.sample.plugins.tccontrol.TcInstallerがテスト用のJENKINS_HOMEディレクトリにコピーされる
    @Test
    @LocalData
    public void startStopTest2() {
        try {
            // 自動インストール用のToolInstallerを定義
            // TcInstallerを利用
            // 第一引数はupdates/org.sample.plugins.tccontrol.TcInstallerファイルに登録されたエントリの「ID」
            // ここでは「7.0.42」とする
            TcInstaller tcInstaller = new TcInstaller("7.0.42");
            installTest(tcInstaller);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(Jenkins.getInstance().getRootDir().getName());
    }

    private void installTest(ToolInstaller installer) throws Exception {
        List<ToolInstaller> installers = new ArrayList<ToolInstaller>();
        // 渡されたinstallerをListに追加
        installers.add(installer);

        List<ToolProperty<ToolInstallation>> properties = new ArrayList<ToolProperty<ToolInstallation>>();
        InstallSourceProperty prop = new InstallSourceProperty(installers);
        // InstallSourcePropertyにinstallersを登録
        properties.add(prop);

        // TcInstallationのインスタンスを作成
        // 第一引数は設定名
        // 第二引数はツールのホームディレクトリ -> 今回は自動インストールなので指定しない
        // 第三引数は先ほど作成したプロパティリスト -> コンストラクタ内でリスト内のプロパティとこのInstallationが結びつけられる
        TcInstallation installation = new TcInstallation("sample", "", properties);
        TcInstallation[] installations = {installation};

        // TcControllerを作成
        // 各引数はTcControllerのソースを参照
        TcController wrapper = new TcController("sample", "", "", new TcController.ServerPort("8105", "8180", "8109"));
        // Descriptorを取得してinstallationsを登録
        TcController.DescriptorImpl controller = j.getInstance().getDescriptorByType(TcController.DescriptorImpl.class);
        controller.setTcInstallations(installations);

        // フリースタイルプロジェクトを作成
        FreeStyleProject project = j.createFreeStyleProject("test");
        // BuilderとしてShell実行ビルダを登録
        project.getBuildersList().add(new Shell("echo Build!"));
        // BuildWrapperとしてTcControllerを登録
        project.getBuildWrappersList().add(wrapper);
        // プロジェクトを実行
        Future<FreeStyleBuild> build = project.scheduleBuild2(0);
        // 実行結果が正常であることを確認
        j.assertBuildStatusSuccess(build);
    }

}
