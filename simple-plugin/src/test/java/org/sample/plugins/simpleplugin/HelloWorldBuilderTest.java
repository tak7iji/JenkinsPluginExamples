package org.sample.plugins.simpleplugin;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.BufferedReader;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HelloWorldBuilderTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void simple() throws Exception {
        Pattern ptn = Pattern.compile("^Hello.*$");
        // フリースタイルプロジェクトを作成
        FreeStyleProject project = j.createFreeStyleProject();
        // BuilderとしてHelloWorldBuilderを登録
        project.getBuildersList().add(new HelloWorldBuilder("Test", null));

        // プロジェクトを実行
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        // ビルド時のコンソールログを出力
        BufferedReader reader = new BufferedReader(build.getLogReader());
        String line;
        boolean isFrench = false;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (ptn.matcher(line).matches()) {
                isFrench = true;
            }
        }

        // 「Hello」が表示されたかチェック
        assertTrue("Should say hello with English.", isFrench);

        // ビルドの実行結果がSUCCESSかチェック
        j.assertBuildStatus(Result.SUCCESS, build);
    }

    @Test
    public void checkFrench() throws Exception {
        Pattern ptn = Pattern.compile("^Bonjour.*$");
        JenkinsRule.WebClient client = j.createWebClient();

        // システムの設定ページを取得
        HtmlPage configPage = client.goTo("configure");
        // config formを取得
        HtmlForm form = configPage.getFormByName("config");
        // useFrenchにチェックを入れる
        form.getInputByName("_.useFrench").setChecked(true);
        // テキストボックスにデータを入れる
        form.getInputByName("_.sample").setValueAttribute("TestData");
        // 設定を保存
        j.submit(form);

        // フリースタイルプロジェクトを作成
        FreeStyleProject project = j.createFreeStyleProject();
        // BuilderとしてHelloWorldBuilderを登録
        project.getBuildersList().add(new HelloWorldBuilder("", null));

        // ジョブの設定ページを取得
        HtmlPage projectConfig = client.goTo("job/" + project.getName() + "/configure");
        // config formを取得
        HtmlForm projectForm = projectConfig.getFormByName("config");
        // nameに「Test」を設定
        projectForm.getInputByName("_.name").setValueAttribute("Test");
        // 設定を保存
        j.submit(projectForm);

        // プロジェクトを実行
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        // ビルド時のコンソールログを出力
        BufferedReader reader = new BufferedReader(build.getLogReader());
        String line;
        boolean isFrench = false;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (ptn.matcher(line).matches()) {
                isFrench = true;
            }
        }

        // 「Bonjour」が表示されたかチェック
        assertTrue("Should say hello with french.", isFrench);

        // ビルドの実行結果がSUCCESSかチェック
        j.assertBuildStatus(Result.SUCCESS, build);

    }

    @Test
    public void nameTooShort() throws Exception {
        JenkinsRule.WebClient client = j.createWebClient();
        // システムの設定ページを取得
        HtmlPage configPage = client.goTo("configure");
        // config formを取得
        HtmlForm form = configPage.getFormByName("config");
        // useFrenchにチェックを入れる
        form.getInputByName("_.useFrench").setChecked(true);
        // テキストボックスにデータを入れる
        form.getInputByName("_.sample").setValueAttribute("TestData");
        // 設定を保存
        j.submit(form);

        // フリースタイルプロジェクトを作成
        FreeStyleProject project = j.createFreeStyleProject();
        // BuilderとしてShell実行ビルダを登録
        project.getBuildersList().add(new HelloWorldBuilder("", null));

        // ジョブの設定ページを取得
        HtmlPage projectConfig = client.goTo("job/" + project.getName() + "/configure");
        // config formを取得
        HtmlForm projectForm = projectConfig.getFormByName("config");
        // nameに「Boo」を設定
        projectForm.getInputByName("_.name").setValueAttribute("Boo");
        // フォーカスを外す
        projectForm.blur();
        // Isn't the name too short?が出力されているか確認する
        j.assertXPathResultsContainText(projectForm, "//div[@class='warning']", Messages.HelloWorldBuilder_tooShortName());

//        // classがwarningのdivタグを取得する
//        List<HtmlElement> elements = projectForm.getElementsByAttribute("div", "class", "warning");
//        // 「Isn't the name too short?」を持つElementを取得する
//        HtmlElement warnElement = null;
//        for (HtmlElement element : elements) {
//            if ("Isn't the name too short?".equals(element.getTextContent())) {
//                warnElement = element;
//            }
//            System.out.println(element.getTextContent());
//        }
//        // Elementが存在するかチェックする
//        assertNotNull("Name is not too short!", warnElement);
    }

    @Test
    public void nameNotSet() throws Exception {
        JenkinsRule.WebClient client = j.createWebClient();
        // システムの設定ページを取得
        HtmlPage configPage = client.goTo("configure");
        // config formを取得
        HtmlForm form = configPage.getFormByName("config");
        // useFrenchにチェックを入れる
        form.getInputByName("_.useFrench").setChecked(true);
        // テキストボックスにデータを入れる
        form.getInputByName("_.sample").setValueAttribute("TestData");
        // 設定を保存
        j.submit(form);

        // フリースタイルプロジェクトを作成
        FreeStyleProject project = j.createFreeStyleProject();
        // BuilderとしてShell実行ビルダを登録
        project.getBuildersList().add(new HelloWorldBuilder("", null));

        // ジョブの設定ページを取得
        HtmlPage projectConfig = client.goTo("job/" + project.getName() + "/configure");
        // config formを取得
        HtmlForm projectForm = projectConfig.getFormByName("config");
        // Please set a nameが出力されているか確認する
        j.assertXPathResultsContainText(projectForm, "//div[@class='error']", Messages.HelloWorldBuilder_setName());

//        // classがerrorのdivタグを取得する
//        List<HtmlElement> elements = projectForm.getElementsByAttribute("div", "class", "error");
//        // 「Please set a name」を持つElementを取得する
//        HtmlElement errorElement = null;
//        for (HtmlElement element : elements) {
//            if ("Please set a name".equals(element.getTextContent())) {
//                errorElement = element;
//            }
//            System.out.println(element.getTextContent());
//        }
//        // Elementが存在するかチェックする
//        assertNotNull("Not set a name", errorElement);
    }

    @Test
    public void sampleNotSet() throws Exception {
        // システムの設定ページを取得
        HtmlPage configPage = j.createWebClient().goTo("configure");
        // config formを取得
        HtmlForm form = configPage.getFormByName("config");

        // Please set wordsが出力されているか確認する
        j.assertXPathResultsContainText(form, "//div[@class='error']", Messages.HelloWorldBuilder_setWords());

//        // classがerrorのdivタグを取得する
//        List<HtmlElement> elements = form.getElementsByAttribute("div", "class", "error");
//        // 「Please set words」を持つElementを取得する
//        HtmlElement errorElement = null;
//        for (HtmlElement element : elements) {
//            if ("Please set words".equals(element.getTextContent())) {
//                errorElement = element;
//            }
//            System.out.println(element.getTextContent());
//        }
//        // Elementが存在するかチェックする
//        assertNotNull("Not set words", errorElement);
    }

    @Test
    public void sampleTooShort() throws Exception {
        JenkinsRule.WebClient client = j.createWebClient();
        // StatusCodeが200以外でもExceptionをスローしないようにする
        client.setThrowExceptionOnFailingStatusCode(false);
        // システムの設定ページを取得
        HtmlPage configPage = client.goTo("configure");
        // config formを取得
        HtmlForm form = configPage.getFormByName("config");
        // テキストボックスにデータを入れる
        form.getInputByName("_.sample").setValueAttribute("Test");
        // 設定を保存

        int statusCode = j.submit(form).getWebResponse().getStatusCode();
        System.out.println("Status code: " + statusCode);
        // StatusCodeが400であることを確認する
        assertEquals(400, statusCode);
    }
}
