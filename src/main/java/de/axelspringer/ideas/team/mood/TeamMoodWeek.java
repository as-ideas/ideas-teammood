package de.axelspringer.ideas.team.mood;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class TeamMoodWeek {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String getCalendarWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return "" + cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static String start() {
        return dateTimeFormatter.format(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)));
    }

    public static String end() {
        return dateTimeFormatter.format(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)));
    }
}
