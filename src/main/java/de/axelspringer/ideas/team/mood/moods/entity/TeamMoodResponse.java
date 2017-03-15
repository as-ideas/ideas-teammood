package de.axelspringer.ideas.team.mood.moods.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TeamMoodResponse {
    public String teamName;
    public TeamMoodDay[] days;

    public List<TeamMoodValue> getAllValuesSorted() {
        List<TeamMoodValue> result = new ArrayList<>();
        for (TeamMoodDay day : days) {
            for (TeamMoodValue value : day.values) {
                if (value.comment != null) {
                    result.add(value);
                }
            }
        }

        result.sort(Comparator.comparing(o -> o.mood));
        return result;
    }
}
