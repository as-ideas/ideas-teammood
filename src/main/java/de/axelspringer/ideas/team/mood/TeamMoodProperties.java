package de.axelspringer.ideas.team.mood;

import java.util.Arrays;
import java.util.List;

public class TeamMoodProperties {

    public static final TeamMoodProperties INSTANCE = new TeamMoodProperties();

    private String username;

    private String password;
    private String basicAuthPassword;

    private List<String> teamApiKeys;

    private List<String> emails;

    public String getUsername() {
        if (username == null) {
            username = getProperty("de.axelspringer.ideas.team.mood.mail.user");
        }
        return username;
    }

    public String getPassword() {
        if (password == null) {
            password = getProperty("de.axelspringer.ideas.team.mood.mail.password");
        }
        return password;
    }

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

    private String getProperty(String s) {
        String result = System.getProperty(s);
        if (result == null) {
            result = System.getenv(s);
        }
        return result;
    }
}
