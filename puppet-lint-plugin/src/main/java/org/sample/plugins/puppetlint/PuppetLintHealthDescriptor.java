package org.sample.plugins.puppetlint;

import hudson.plugins.analysis.core.AbstractHealthDescriptor;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.AnnotationProvider;
import org.jvnet.localizer.Localizable;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 2013/11/06
 * Time: 22:51
 * To change this template use File | Settings | File Templates.
 */
public class PuppetLintHealthDescriptor extends AbstractHealthDescriptor {
    public PuppetLintHealthDescriptor(HealthDescriptor healthDescriptor) {
        super(healthDescriptor);
    }

    @Override
    protected Localizable createDescription(AnnotationProvider result) {
        if (result.getNumberOfAnnotations() == 0) {
            return Messages._PuppetLint_ResultAction_HealthReportNoItem();
        } else {
            return Messages._PuppetLint_ResultAction_HealthReportItem(result.getNumberOfAnnotations());
        }
    }
}
