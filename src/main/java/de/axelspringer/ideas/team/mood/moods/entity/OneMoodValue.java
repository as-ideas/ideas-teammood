package de.axelspringer.ideas.team.mood.moods.entity;

public class OneMoodValue {

    public TeamMoodValueType mood;
    public OneMoodComment comment;
    public long nativeDate;
    public String formattedDate;

    public TeamMoodValueType getMood() {
        return mood;
    }

    public String getComment() {
        return comment.getBody();
    }

    public String getHexColor() {
        return getMood().hexColor;
    }

    public String getFormattedDate() {
        return formattedDate;
    }
}
