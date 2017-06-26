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
    private static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("dd-MM-YYYY");

    public static String start(LocalDate time) {
        return dateTimeFormatter.format(time.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)));
    }

    public static String end(LocalDate time) {
        return dateTimeFormatter.format(time.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)));
    }

    public static String startFormattedWithTeamMoodSettings(LocalDate time) {
        return dateTimeFormatter2.format(time.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)));
    }

    public static String endFormattedWithTeamMoodSettings(LocalDate time) {
        return dateTimeFormatter2.format(time.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)));
    }

    public static LocalDate week(Integer number) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.WEEK_OF_YEAR, number + 1);
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneId.of("Europe/Berlin")).toLocalDate();
    }

    public static LocalDate currentWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneId.of("Europe/Berlin")).toLocalDate();
    }

    @Deprecated
    public static String getCurrentCalendarWeek() {
        return "" + currentWeekNumber();
    }

    public static Integer currentWeekNumber() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.WEEK_OF_YEAR) + 1;
    }
}
