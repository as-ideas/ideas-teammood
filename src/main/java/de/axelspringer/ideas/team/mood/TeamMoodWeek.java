package de.axelspringer.ideas.team.mood;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class TeamMoodWeek {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static TeamMoodWeek currentWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return new TeamMoodWeek(cal.get(Calendar.WEEK_OF_YEAR) + 1);
    }

    private int weekNumber;
    private LocalDate week;

    public TeamMoodWeek(String weekNumber) {
        this(Integer.valueOf(weekNumber));
    }

    public TeamMoodWeek(int weekNumber) {
        this.weekNumber = weekNumber;
        this.week = week(weekNumber);
    }

    public String start() {
        return dateTimeFormatter.format(monday());
    }

    public String end() {
        return dateTimeFormatter.format(friday());
    }

    public String startFormattedWithTeamMoodSettings() {
        return dateTimeFormatter2.format(monday());
    }

    public String endFormattedWithTeamMoodSettings() {
        return dateTimeFormatter2.format(saturday());
    }

    private LocalDate monday() {
        return week.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
    }

    private LocalDate friday() {
        return week.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }

    private LocalDate saturday() {
        return week.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
    }

    private LocalDate week(Integer number) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.WEEK_OF_YEAR, number);
        cal.set(Calendar.DAY_OF_WEEK, 4);
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneId.of("Europe/Berlin")).toLocalDate();
    }


    public String weekNumber() {
        return "" + weekNumber;
    }
}
