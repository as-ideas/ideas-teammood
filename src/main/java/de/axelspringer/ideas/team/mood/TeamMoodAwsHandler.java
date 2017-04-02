package de.axelspringer.ideas.team.mood;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import de.axelspringer.ideas.team.mood.TeamMoodApplication.HelloJob;
import org.quartz.JobExecutionException;

public class TeamMoodAwsHandler implements RequestHandler<String, String> {
    @Override
    public String handleRequest(String input, Context context) {

        HelloJob helloJob = new HelloJob();

        try {
            helloJob.execute(null);
        } catch (JobExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }
}
