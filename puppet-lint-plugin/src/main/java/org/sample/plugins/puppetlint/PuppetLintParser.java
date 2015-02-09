package org.sample.plugins.puppetlint;

import hudson.plugins.analysis.core.AbstractAnnotationParser;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/06
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
public class PuppetLintParser extends AbstractAnnotationParser {
    protected PuppetLintParser(String defaultEncoding) {
        super(defaultEncoding);
    }

    @Override
    public Collection<FileAnnotation> parse(InputStream inputStream, String s) throws InvocationTargetException {
        ArrayList<FileAnnotation> annotations = new ArrayList<FileAnnotation>();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int lineNumber;
        try {
            while ((line = br.readLine()) != null) {
                Priority priority;
                String[] result = line.split(",");
                if ("WARNING".equals(result[1])) {
                    priority = Priority.NORMAL;
                } else if ("ERROR".equals(result[1])) {
                    priority = Priority.HIGH;
                } else {
                    priority = Priority.LOW;
                }

                lineNumber = Integer.parseInt(result[2]);
                Warning warning = new Warning(priority, result[3], lineNumber, lineNumber, "", "");
                warning.setFileName(result[0]);
                annotations.add(warning);
            }
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }

        return annotations;
    }
}
