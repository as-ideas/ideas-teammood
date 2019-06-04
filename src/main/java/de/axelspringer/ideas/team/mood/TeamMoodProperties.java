package de.axelspringer.ideas.team.mood;

import java.util.Arrays;
import java.util.List;

public class TeamMoodProperties {

    public static final TeamMoodProperties INSTANCE = new TeamMoodProperties();

    private String elasticMailApiKey;
    private String basicAuthPassword;
    private String teamMoodAPIUsername;
    private String teamMoodAPIPassword;
    private List<String> teamApiKeys;
    private List<String> emails;


    public List<String> getTeamApiKeys() {
        if (teamApiKeys == null) {
            teamApiKeys = Arrays.asList(getProperty("de.axelspringer.ideas.team.mood.api.keys").split(","));
        }
        return teamApiKeys;
    }

    public List<String> getEmailAddresses() {
        if (emails == null) {
            emails = Arrays.asList(getProperty("de.axelspringer.ideas.team.mood.email.addresses").split(","));
        }
        return emails;
    }

    public String getBasicAuthPassword() {
        if (basicAuthPassword == null) {
            basicAuthPassword = getProperty("de.axelspringer.ideas.team.mood.basicAuthPassword");
        }
        return basicAuthPassword;
    }

    public String getElasticMailApiKey() {
        if (elasticMailApiKey == null) {
            elasticMailApiKey = getProperty("de.axelspringer.ideas.team.mood.elasticMailApiKey");
        }
        return elasticMailApiKey;
    }

    public String getTeamMoodAPIUsername() {
        if (teamMoodAPIUsername == null) {
            teamMoodAPIUsername = getProperty("de.axelspringer.ideas.team.mood.apiUsername");
        }
        return teamMoodAPIUsername;
    }

    public String getTeamMoodAPIPassword() {
        if (teamMoodAPIPassword == null) {
            teamMoodAPIPassword = getProperty("de.axelspringer.ideas.team.mood.apiPassword");
        }
        return teamMoodAPIPassword;
    }

    private String getProperty(String s) {
        String result = System.getProperty(s);
        if (result == null) {
            result = System.getenv(s);
        }
        return result;

    }


}
