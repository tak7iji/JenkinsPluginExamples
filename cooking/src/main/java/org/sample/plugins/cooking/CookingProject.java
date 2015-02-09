package org.sample.plugins.cooking;

import hudson.Extension;
import hudson.model.*;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by mash on 13/12/13.
 */
public class CookingProject extends Project<CookingProject, Cooking> implements TopLevelItem {
    private DescribableList<Food, Descriptor<Food>> foods;

    public CookingProject(ItemGroup<Item> parent, String name) {
        super(parent, name);
    }

    public Map<Descriptor<Food>, Food> getFoods() {
        return getFoodsList().toMap();
    }

    public synchronized DescribableList<Food,Descriptor<Food>> getFoodsList() {
        if (foods == null) {
            foods = new DescribableList(this);
        }
        return foods;
    }

    public List<FoodDescriptor> getFoodDescriptors() {
        return Food.all();
    }

    @Override
    protected Class<Cooking> getBuildClass() {
        return Cooking.class;
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    @Override
    public void submit(StaplerRequest req, StaplerResponse res) throws IOException, ServletException,
            Descriptor.FormException {
        super.submit(req,res);

        JSONObject json = req.getSubmittedForm();

        getFoodsList().rebuildHetero(req, json, Food.all(), "food");
    }

    @Extension
    public static class DescriptorImpl extends AbstractProject.AbstractProjectDescriptor {
        @Override
        public String getDisplayName() {
            return "Cooking Project";
        }

        @Override
        public CookingProject newInstance(ItemGroup parent, String name) {
            final CookingProject p = new CookingProject(parent, name);
            return p;
        }

    }
}
