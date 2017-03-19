package de.axelspringer.ideas.team.mood.moods.entity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Team {

    public String id;
    public String teamName;
    public TeamMoodDay[] days;
    public Integer numberOfMembers;

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
        // 5 days * Number
        int maxCount = 5 * numberOfMembers;
        int count = 0;
        for (TeamMoodDay day : days) {
            for (OneMoodValue value : day.values) {
                count++;
            }
        }
        return "" + (100 * count / maxCount) + "%";
    }


    public String getTeamName() {
        return teamName;
    }

    public TeamMoodDay[] getDays() {
        return days;
    }
}
