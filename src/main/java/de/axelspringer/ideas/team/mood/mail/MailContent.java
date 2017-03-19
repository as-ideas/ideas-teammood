package de.axelspringer.ideas.team.mood.mail;

import de.axelspringer.ideas.team.mood.moods.entity.OneMoodValue;
import de.axelspringer.ideas.team.mood.moods.entity.Team;
import de.axelspringer.ideas.team.mood.moods.entity.TeamMoodDay;

import java.text.DecimalFormat;
import java.util.List;

public class MailContent {

    public String title;
    public String start;
    public String end;
    public List<Team> teams;


    public String getTitle() {
        return title;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getAverageMood() {
        int count = 0;
        double result = 0;

        for (Team team : teams) {
            for (TeamMoodDay day : team.getDays()) {
                for (OneMoodValue value : day.values) {
                    count++;
                    result += value.mood.moodValue;
                }
            }
        }

        result = result / count;

        return new DecimalFormat("#.0").format(result);
    }

    public String getParticipation() {
        // 5 days * Number
        int maxCount = 0;
        int count = 0;
        for (Team team : teams) {
            maxCount += (team.numberOfMembers * 5);
            for (TeamMoodDay day : team.getDays()) {
                for (OneMoodValue value : day.values) {
                    count++;
                }
            }
        }
        return "" + (100 * count / maxCount) + "%";
    }
}
