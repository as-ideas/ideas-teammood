package de.axelspringer.ideas.team.mood;

import com.qmetric.spark.authentication.AuthenticationDetails;
import de.axelspringer.ideas.team.mood.controller.TeamMoodController;
import de.axelspringer.ideas.team.mood.util.AdvancedSparkBasicAuthenticationFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.before;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class TeamMoodApplication {

    private final static Logger LOG = LoggerFactory.getLogger(TeamMoodApplication.class);

    public static void main(String[] args) throws Exception {
        int herokuAssignedPort = getHerokuAssignedPort();
        LOG.info("Starting on port '{}'", herokuAssignedPort);
        port(herokuAssignedPort);
        staticFiles.location("/public");

        if (StringUtils.isNotBlank(TeamMoodProperties.INSTANCE.getBasicAuthPassword())) {
            before(new AdvancedSparkBasicAuthenticationFilter("/*", new AuthenticationDetails("ideas", TeamMoodProperties.INSTANCE.getBasicAuthPassword())));
        }
        new TeamMoodController().initController();
    }


    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
