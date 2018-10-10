package de.axelspringer.ideas.team.mood.moods.entity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Team implements Comparable<Team> {

    public String id;
    public String teamName;
    public TeamMoodDay[] days;

    public List<OneMoodValue> getAllValuesSorted() {
        List<OneMoodValue> result = new ArrayList<>();
        for (TeamMoodDay day : days) {
            for (OneMoodValue value : day.values) {
                if (value.comment != null) {
                    result.add(value);
                }
            }
        }

        result.sort(Comparator.comparing(o -> o.mood));
        return result;
    }

    public String getUrl() {
        return "https://app.teammood.com/app#/" + id + "/calendar";
    }

    public String getAverageMood() {
        int count = 0;
        double result = 0;
        for (TeamMoodDay day : days) {
            for (OneMoodValue value : day.values) {
                count++;
                result += value.mood.moodValue;
            }
        }
        result = result / count;

        return new DecimalFormat("#.0").format(result);
    }

    public String getParticipation() {
        int sum = 0;
        for (TeamMoodDay day : days) {
            sum += day.getParticipationRate();
        }
        if (days.length == 0) {
            return "0%";
        } else {
            return "" + (sum / days.length) + "%";
        }
    }


    public String getTeamName() {
        return teamName;
    }

    public TeamMoodDay[] getDays() {
        return days;
    }

    @Override
    public int compareTo(Team o) {
        return this.id.compareTo(o.id);
    }
}
