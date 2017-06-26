package de.axelspringer.ideas.team.mood;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.qmetric.spark.authentication.AuthenticationDetails;
import com.qmetric.spark.authentication.BasicAuthenticationFilter;
import de.axelspringer.ideas.team.mood.controller.TeamMoodController;
import de.axelspringer.ideas.team.mood.mail.MailContent;
import de.axelspringer.ideas.team.mood.mail.MailSender;
import de.axelspringer.ideas.team.mood.moods.TeamMood;
import de.axelspringer.ideas.team.mood.moods.entity.Team;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

import static de.axelspringer.ideas.team.mood.TeamMoodWeek.getCurrentCalendarWeek;
import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static spark.Spark.before;
import static spark.Spark.port;

public class TeamMoodApplication {

    private final static Logger LOG = LoggerFactory.getLogger(TeamMoodApplication.class);

    public static void main(String[] args) throws Exception {
        //new HelloJob().execute(null);
        before(new BasicAuthenticationFilter("/path/*",
                new AuthenticationDetails("ideas", TeamMoodProperties.INSTANCE.getBasicAuthPassword())));
        port(getHerokuAssignedPort());
        new TeamMoodController().initController();
        startScheduler();
    }

    private static void startScheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();

        JobDetail jobDetail = newJob(HelloJob.class).build();
        scheduler.scheduleJob(jobDetail, trigger());
    }

    private static Trigger trigger() {
        return newTrigger()
                .withIdentity("weekklySaturdayTrigger", "group1")
                .startNow()
                .withSchedule(weeklyOnDayAndHourAndMinute(DateBuilder.SATURDAY, 11, 0)) // fire every saturday at 11:00
                .build();
    }

    public static class HelloJob implements Job {


        private final TeamMood teamMood;
        private final MailSender mailSender;
        private final TeamMoodProperties teamMoodProperties;
        private final Handlebars handlebars;
        private final Template emailTemplate;

        public HelloJob() {
            this.teamMoodProperties = TeamMoodProperties.INSTANCE;
            this.mailSender = new MailSender(this.teamMoodProperties.getUsername(), this.teamMoodProperties.getPassword());
            this.teamMood = new TeamMood();
            this.handlebars = new Handlebars();

            try {
                emailTemplate = handlebars.compile("templates/email");
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            LOG.info("START TeamMood mail sending");
            try {

                MailContent emailContent = new MailContent();
                emailContent.title = "TeamMood for Ideas: KW" + getCurrentCalendarWeek();
                emailContent.teams = new ConcurrentSkipListSet<>();
                emailContent.start = TeamMoodWeek.start(TeamMoodWeek.currentWeek());
                emailContent.end = TeamMoodWeek.end(TeamMoodWeek.currentWeek());

                teamMoodProperties.getTeamApiKeys().parallelStream().forEach((apiKey) -> {
                    Team team = teamMood.loadTeamMoodForLastSevenDays(apiKey);
                    emailContent.teams.add(team);
                });

                String subject = "TeamMood for Ideas: KW" + getCurrentCalendarWeek();


                String htmlBody = emailTemplate.apply(emailContent);
//                FileUtils.writeStringToFile(new File("~/ideas-teammood/target/mail.html"), htmlBody);
                for (String mailAddress : teamMoodProperties.getEmailAddresses()) {
                    mailSender.send(mailAddress, subject, htmlBody);
                }

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            LOG.info("END TeamMood mail sending");
        }


    }


    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
