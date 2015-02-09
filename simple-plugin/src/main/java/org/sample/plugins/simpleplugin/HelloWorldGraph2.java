package org.sample.plugins.simpleplugin;

import hudson.model.AbstractBuild;
import hudson.util.Graph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/19
 * Time: 18:47
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldGraph2 extends Graph {
    private AbstractBuild<?, ?> owner;

    public HelloWorldGraph2(AbstractBuild<?, ?> owner) {
        this(Calendar.getInstance(), 640, 480);
        this.owner = owner;
    }

    protected HelloWorldGraph2(Calendar timestamp, int defaultW, int defaultH) {
        super(timestamp, defaultW, defaultH);
    }

    @Override
    protected JFreeChart createGraph() {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        data.setValue(50, "Row", "Col 1");
        data.setValue(20, "Row", "Col 2");
        data.setValue(30, "Row", "Col 3");

        JFreeChart chart = ChartFactory.createLineChart(
                owner.getRootDir().getPath(),
                "Category Axis",
                "Value Axis",
                data,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );
        return chart;
    }
}
