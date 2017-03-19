package de.axelspringer.ideas.team.mood.moods;

import com.google.gson.Gson;
import de.axelspringer.ideas.team.mood.moods.entity.OneMoodValue;
import de.axelspringer.ideas.team.mood.moods.entity.ParticipationResponse;
import de.axelspringer.ideas.team.mood.moods.entity.Team;
import de.axelspringer.ideas.team.mood.moods.entity.TeamMoodDay;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TeamMood {

    private static final String URL_TO_TEAM_MOOD_WITH_PLACEHOLDER = "https://app.teammood.com/api/%s/moods?since=7";
    private static final String URL_TO_PARTICIPATION_WITH_PLACEHOLDER = "https://app.teammood.com/%s/participation";

    private final static Logger LOG = LoggerFactory.getLogger(TeamMood.class);

    private Gson gson = new Gson();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public Team loadTeamMoodForLastSevenDays(String teamApiKey) {
        LOG.info("Loading data from TeamMood!");
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(String.format(URL_TO_TEAM_MOOD_WITH_PLACEHOLDER, teamApiKey));

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                String resultString = EntityUtils.toString(entity);
                checkStatusCode(response, resultString);

                Team team = gson.fromJson(resultString, Team.class);
                for (TeamMoodDay day : team.days) {
                    LocalDateTime dateTimeOfDay = epochMilisToLocalDateTime(day);

                    for (OneMoodValue value : day.values) {
                        value.nativeDate = day.nativeDate;
                        value.formattedDate = WordUtils.capitalizeFully(dateTimeOfDay.getDayOfWeek().name()) + ", " + dateTimeFormatter.format(dateTimeOfDay);
                    }
                }

                team.id = teamApiKey;
                team.numberOfMembers = loadNumberOfMembers(teamApiKey);

                return team;
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private LocalDateTime epochMilisToLocalDateTime(TeamMoodDay day) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(day.nativeDate), ZoneId.of("Europe/Berlin"));
    }

    public Integer loadNumberOfMembers(String teamApiKey) {
        LOG.info("Loading participation from TeamMood!");
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(String.format(URL_TO_PARTICIPATION_WITH_PLACEHOLDER, teamApiKey));

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                String resultString = EntityUtils.toString(entity);
                checkStatusCode(response, resultString);

                return gson.fromJson(resultString, ParticipationResponse.class).activeMembersCount;
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void checkStatusCode(CloseableHttpResponse response, String resultString) {
        if (response.getStatusLine().getStatusCode() > 200) {
            LOG.warn(resultString);
            throw new RuntimeException("Could not read answer from TEAM MOOD.");
        }
    }
}
