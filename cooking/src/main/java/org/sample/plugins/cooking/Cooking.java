package org.sample.plugins.cooking;

import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * Created by mash on 13/12/13.
 */
public class Cooking extends Build<CookingProject, Cooking> {
    public Cooking(CookingProject project) throws IOException {
        super(project);
    }

    public Cooking(CookingProject project, File buildDir) throws IOException {
        super(project, buildDir);
    }

    @Override
    public void run() {
        for(Food food : project.getFoodsList()) {
            execute(new CookingJob(food));
        }
    }

    class CookingJob extends RunExecution {
        private Food food;
        public CookingJob(Food food) {
            this.food = food;
        }

        @Override
        public Result run(BuildListener buildListener) throws Exception, RunnerAbortedException {
            return food.cooking(buildListener);
        }

        @Override
        public void post(BuildListener buildListener) throws Exception {
            food.post(buildListener);
        }

        @Override
        public void cleanUp(@Nonnull BuildListener buildListener) throws Exception {
            food.cleanup(buildListener);
        }
    }
}
