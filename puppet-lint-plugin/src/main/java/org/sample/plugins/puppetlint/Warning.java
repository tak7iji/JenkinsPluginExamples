package org.sample.plugins.puppetlint;

import hudson.plugins.analysis.util.model.AbstractAnnotation;
import hudson.plugins.analysis.util.model.Priority;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/06
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class Warning extends AbstractAnnotation {
    public Warning(Priority priority, String message, int start, int end, String category, String type) {
        super(priority, message, start, end, category, type);
        setOrigin("puppet-lint");
    }

    public String getToolTip() {
        return "";
    }
}
