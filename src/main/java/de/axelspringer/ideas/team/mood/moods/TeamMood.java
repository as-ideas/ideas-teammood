package de.axelspringer.ideas.team.mood.moods;

import com.google.gson.Gson;
import de.axelspringer.ideas.team.mood.moods.entity.TeamMoodResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamMood {

    private static final String URL_TO_TEAM_MOOD_WITH_PLACEHOLDER = "https://app.teammood.com/api/%s/moods?since=7";
    private final static Logger LOG = LoggerFactory.getLogger(TeamMood.class);

    private Gson gson = new Gson();

    public TeamMoodResponse loadTeamMoodForLastSevenDays(String teamApiKey) {
        LOG.info("Loading data from TeamMood!");
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(String.format(URL_TO_TEAM_MOOD_WITH_PLACEHOLDER, teamApiKey));

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                String resultString = EntityUtils.toString(entity);
                if (response.getStatusLine().getStatusCode() > 200) {
                    LOG.warn(resultString);
                    throw new RuntimeException("Could not read answer from TEAM MOOD.");
                } else {
                    return gson.fromJson(resultString, TeamMoodResponse.class);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
