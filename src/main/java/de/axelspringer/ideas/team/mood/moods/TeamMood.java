package de.axelspringer.ideas.team.mood.moods;

import com.google.gson.Gson;
import de.axelspringer.ideas.team.mood.TeamMoodWeek;
import de.axelspringer.ideas.team.mood.letsencrypt.TeamMoodHttpClientFactory;
import de.axelspringer.ideas.team.mood.moods.entity.OneMoodValue;
import de.axelspringer.ideas.team.mood.moods.entity.ParticipationResponse;
import de.axelspringer.ideas.team.mood.moods.entity.Team;
import de.axelspringer.ideas.team.mood.moods.entity.TeamMoodDay;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TeamMood {

    private static final String URL_TO_TEAM_MOOD = "https://app.teammood.com/api/%s/moods?start=%s&end=%s";
    private static final String URL_TO_TEAM_MOOD_WITH_LAST_SEVEN_DAYS_WITH_PLACEHOLDER = "https://app.teammood.com/api/%s/moods?since=7";
    private static final String URL_TO_PARTICIPATION_WITH_PLACEHOLDER = "https://app.teammood.com/%s/participation";

    private final static Logger LOG = LoggerFactory.getLogger(TeamMood.class);

    private Gson gson = new Gson();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public Team loadTeamMoodForLastSevenDays(String teamApiKey) {
        LOG.info("Loading data from TeamMood!");
        String url = String.format(URL_TO_TEAM_MOOD_WITH_LAST_SEVEN_DAYS_WITH_PLACEHOLDER, teamApiKey);
        return getMoodDataFromTeamMood(teamApiKey, url);

    }

    public Integer loadNumberOfMembers(String teamApiKey) {
        LOG.info("Loading participation from TeamMood!");
        try {
            CloseableHttpClient httpclient = getaDefault();
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

    public Team loadTeamMoodForWeek(String teamApiKey, LocalDate week) {
        String start = TeamMoodWeek.startFormattedWithTeamMoodSettings(week);
        String end = TeamMoodWeek.endFormattedWithTeamMoodSettings(week);

        LOG.info("Loading data from TeamMood from '{}' to '{}'!", start, end);
        String url = String.format(URL_TO_TEAM_MOOD, teamApiKey, start, end);
        return getMoodDataFromTeamMood(teamApiKey, url);
    }

    private CloseableHttpClient getaDefault() {
        return new TeamMoodHttpClientFactory().init();
    }

    private LocalDateTime epochMilisToLocalDateTime(TeamMoodDay day) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(day.nativeDate), ZoneId.of("Europe/Berlin"));
    }

    private Team getMoodDataFromTeamMood(String teamApiKey, String url) {
        try {
            CloseableHttpClient httpclient = getaDefault();
            HttpGet httpGet = new HttpGet(url);

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

    private void checkStatusCode(CloseableHttpResponse response, String resultString) {
        if (response.getStatusLine().getStatusCode() > 200) {
            LOG.warn(resultString);
            throw new RuntimeException("Could not read answer from TEAM MOOD.");
        }
    }
}
