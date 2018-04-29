package de.axelspringer.ideas.team.mood.moods.entity;

public class TeamMoodDay {

    public String date; //": "Tuesday, March 14, 2017",
    public Boolean today;
    public String dateShort; // ": "Tue 3/14/17",
    public long nativeDate; //": 1489446000000,
    public OneMoodValue[] values;
    public long participationRate; // 42

    public String getDate() {
        return date;
    }

    public Boolean getToday() {
        return today;
    }

    public String getDateShort() {
        return dateShort;
    }

    public long getNativeDate() {
        return nativeDate;
    }

    public OneMoodValue[] getValues() {
        return values;
    }

    public long getParticipationRate() {
        return participationRate;
    }
}
