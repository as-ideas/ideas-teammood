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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
            TeamMoodWeek week = new TeamMoodWeek(currentWeek);

            MailContent emailContent = createMailContent(week);

            return new ModelAndView(emailContent, "email.hbs");
        }, engine);

        get("/week/:week/send", (request, response) -> {
            String currentWeek = request.params(":week");
            sendMailForWeek(teamMoodProperties.getEmailAddresses(), currentWeek);
            return "{}";
        });


        get("/week/:week/send-all", (request, response) -> {
            String currentWeek = request.params(":week");
            sendMailForWeek(teamMoodProperties.getAllEmailAddresses(), currentWeek);
            return "{}";
        });
    }

    private void sendMailForWeek(List<String> addresses, String currentWeek) throws IOException {
        LOG.info("Loading data for week '{}'.", currentWeek);
        validateCurrentWeek(currentWeek);
        TeamMoodWeek week = new TeamMoodWeek(currentWeek);

        MailContent emailContent = createMailContent(week);

        Template emailTemplate = new Handlebars().compile("templates/email");

        String subject = "TeamMood for Ideas: KW" + week.weekNumber();
        String htmlBody = emailTemplate.apply(emailContent);
        for (String mailAddress : addresses) {
            mailSender.send(mailAddress, subject, htmlBody);
        }
    }

    private MailContent createMailContent(TeamMoodWeek week) {
        MailContent emailContent = new MailContent();
        emailContent.title = "TeamMood for Ideas: KW" + week.weekNumber();
        emailContent.teams = new ConcurrentSkipListSet<>();
        emailContent.start = week.start();
        emailContent.end = week.end();
        emailContent.weekNumber = "" + week.weekNumber();

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
