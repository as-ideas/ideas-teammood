package de.axelspringer.ideas.team.mood;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
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
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class TeamMoodApplication {

    private final static Logger LOG = LoggerFactory.getLogger(TeamMoodApplication.class);

    public static void main(String[] args) throws Exception {
        checkParameters();

        new HelloJob().execute(null);

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

    private static void checkParameters() {
        // TODO Should not start if parameters are missing
    }

    public static class HelloJob implements Job {

        private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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
                emailTemplate = handlebars.compile("email");
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            LOG.info("START TeamMood mail sending");
            try {

                MailContent emailContent = new MailContent();
                emailContent.title = "TeamMood for Ideas: KW" + getCalendarWeek();
                emailContent.teams = new ArrayList<>();
                emailContent.start = start();
                emailContent.end = end();

                for (String apiKey : teamMoodProperties.getTeamApiKeys()) {
                    Team team = teamMood.loadTeamMoodForLastSevenDays(apiKey);
                    emailContent.teams.add(team);
                }

                String subject = "TeamMood for Ideas: KW" + getCalendarWeek();


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

        private String getCalendarWeek() {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            return "" + cal.get(Calendar.WEEK_OF_YEAR);
        }

        private String start() {
            return dateTimeFormatter.format(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)));
        }

        private String end() {
            return dateTimeFormatter.format(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)));
        }


    }
}
