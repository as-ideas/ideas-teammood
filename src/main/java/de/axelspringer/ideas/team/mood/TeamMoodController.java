package de.axelspringer.ideas.team.mood;

import de.axelspringer.ideas.team.mood.mail.MailContent;
import de.axelspringer.ideas.team.mood.moods.TeamMood;
import de.axelspringer.ideas.team.mood.moods.entity.Team;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static de.axelspringer.ideas.team.mood.TeamMoodWeek.getCalendarWeek;
import static spark.Spark.get;


public class TeamMoodController {

    private TeamMoodProperties teamMoodProperties = TeamMoodProperties.INSTANCE;

    private TeamMood teamMood = new TeamMood();

    public void init() {
        HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();

        get("/", (request, response) -> new ModelAndView(new HashMap(), "index.hbs"), engine);
        get("/week/:week", (request, response) -> {

            MailContent emailContent = new MailContent();
            emailContent.title = "TeamMood for Ideas: KW" + getCalendarWeek();
            emailContent.teams = new ConcurrentSkipListSet<>();
            emailContent.start = TeamMoodWeek.start();
            emailContent.end = TeamMoodWeek.end();

            teamMoodProperties.getTeamApiKeys().parallelStream().forEach((apiKey) -> {
                Team team = teamMood.loadTeamMoodForLastSevenDays(apiKey);
                emailContent.teams.add(team);
            });

            String currentWeek = request.params(":week");
            validateCurrentWeek(currentWeek);
            return new ModelAndView(emailContent, "email.hbs");
        }, engine);
    }

    private void validateCurrentWeek(String currentWeek) {
        // TODO check if is number
        // TODO check if is at least this week
    }
}
