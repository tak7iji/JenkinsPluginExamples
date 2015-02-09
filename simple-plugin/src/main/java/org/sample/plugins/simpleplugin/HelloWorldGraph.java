package org.sample.plugins.simpleplugin;

import hudson.model.AbstractProject;
import hudson.util.Graph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: mash
 * Date: 13/11/19
 * Time: 18:47
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldGraph extends Graph {
    private AbstractProject<?, ?> owner;

    public HelloWorldGraph(AbstractProject<?, ?> owner) {
        this(Calendar.getInstance(), 640, 480);
        this.owner = owner;
    }

    protected HelloWorldGraph(Calendar timestamp, int defaultW, int defaultH) {
        super(timestamp, defaultW, defaultH);
    }

    @Override
    protected JFreeChart createGraph() {
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Category 1", 50);
        data.setValue("Category 2", 20);
        data.setValue("Category 3", 30);

        JFreeChart chart = ChartFactory.createPieChart(
                "Simple Pie Chart",
                data,
                true,
                true,
                false
        );
        return chart;
    }
}
