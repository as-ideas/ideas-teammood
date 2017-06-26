package de.axelspringer.ideas.team.mood.controller;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import de.axelspringer.ideas.team.mood.TeamMoodApplication;
import de.axelspringer.ideas.team.mood.TeamMoodProperties;
import de.axelspringer.ideas.team.mood.TeamMoodWeek;
import de.axelspringer.ideas.team.mood.mail.MailContent;
import de.axelspringer.ideas.team.mood.mail.MailSender;
import de.axelspringer.ideas.team.mood.moods.TeamMood;
import de.axelspringer.ideas.team.mood.moods.entity.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static spark.Spark.get;


public class TeamMoodController {

    private final static Logger LOG = LoggerFactory.getLogger(TeamMoodApplication.class);

    private TeamMoodProperties teamMoodProperties = TeamMoodProperties.INSTANCE;

    private TeamMood teamMood = new TeamMood();
    private MailSender mailSender = new MailSender();

    public void initController() {
        LOG.info("Initializing controller.");

        HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();

        get("/", (request, response) -> {
            return new ModelAndView(new HashMap(), "index.hbs");
        }, engine);

        get("/week/:week", (request, response) -> {

            String currentWeek = request.params(":week");
            LOG.info("Loading data for week '{}'.", currentWeek);
            validateCurrentWeek(currentWeek);
            LocalDate week = TeamMoodWeek.week(Integer.valueOf(currentWeek));

            MailContent emailContent = createMailContent(week);

            return new ModelAndView(emailContent, "email.hbs");
        }, engine);

        get("/week/:week/send", (request, response) -> {

            String currentWeek = request.params(":week");
            LOG.info("Loading data for week '{}'.", currentWeek);
            validateCurrentWeek(currentWeek);
            LocalDate week = TeamMoodWeek.week(Integer.valueOf(currentWeek));

            MailContent emailContent = createMailContent(week);

            Template emailTemplate = new Handlebars().compile("templates/email");

            String subject = "TeamMood for Ideas: KW" + TeamMoodWeek.currentWeekNumber();
            String htmlBody = emailTemplate.apply(emailContent);
            for (String mailAddress : teamMoodProperties.getEmailAddresses()) {
                mailSender.send(mailAddress, subject, htmlBody);
            }

            return "{}";
        });
    }

    private MailContent createMailContent(LocalDate week) {
        MailContent emailContent = new MailContent();
        emailContent.title = "TeamMood for Ideas: KW" + TeamMoodWeek.currentWeekNumber();
        emailContent.teams = new ConcurrentSkipListSet<>();
        emailContent.start = TeamMoodWeek.start(week);
        emailContent.end = TeamMoodWeek.end(week);
        emailContent.weekNumber = "" + TeamMoodWeek.currentWeekNumber();

        teamMoodProperties.getTeamApiKeys().parallelStream().forEach((apiKey) -> {
            Team team = teamMood.loadTeamMoodForWeek(apiKey, week);
            emailContent.teams.add(team);
        });
        return emailContent;
    }

    private void validateCurrentWeek(String currentWeek) {
        // TODO check if is number
        // TODO check if is at least this week
    }
}
