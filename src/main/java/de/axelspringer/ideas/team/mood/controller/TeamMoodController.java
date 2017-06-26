package de.axelspringer.ideas.team.mood.controller;

import de.axelspringer.ideas.team.mood.TeamMoodProperties;
import de.axelspringer.ideas.team.mood.TeamMoodWeek;
import de.axelspringer.ideas.team.mood.mail.MailContent;
import de.axelspringer.ideas.team.mood.moods.TeamMood;
import de.axelspringer.ideas.team.mood.moods.entity.Team;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static spark.Spark.get;


public class TeamMoodController {

    private TeamMoodProperties teamMoodProperties = TeamMoodProperties.INSTANCE;

    private TeamMood teamMood = new TeamMood();

    public void initController() {
        HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();

        get("/", (request, response) -> {
            return new ModelAndView(new HashMap(), "index.hbs");
        }, engine);

        get("/week/:week", (request, response) -> {

            String currentWeek = request.params(":week");
            validateCurrentWeek(currentWeek);
            LocalDate week = TeamMoodWeek.week(Integer.valueOf(currentWeek));

            MailContent emailContent = new MailContent();
            emailContent.title = "TeamMood for Ideas: KW" + TeamMoodWeek.currentWeek();
            emailContent.teams = new ConcurrentSkipListSet<>();
            emailContent.start = TeamMoodWeek.start(week);
            emailContent.end = TeamMoodWeek.end(week);
            emailContent.weekNumber = currentWeek;

            teamMoodProperties.getTeamApiKeys().parallelStream().forEach((apiKey) -> {
                Team team = teamMood.loadTeamMoodForWeek(apiKey, week);
                emailContent.teams.add(team);
            });

            return new ModelAndView(emailContent, "email.hbs");
        }, engine);
    }

    private void validateCurrentWeek(String currentWeek) {
        // TODO check if is number
        // TODO check if is at least this week
    }
}
