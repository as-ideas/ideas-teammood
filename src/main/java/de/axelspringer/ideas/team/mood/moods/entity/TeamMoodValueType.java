package de.axelspringer.ideas.team.mood.moods.entity;

public enum TeamMoodValueType {
    excellent("#afffc4"),
    good("#d2fdea"),
    average("#efeff1"),
    hard("#fff8bf"),
    bad("#ffc2c1");

    public final String hexColor;

    TeamMoodValueType(String hexColor) {
        this.hexColor = hexColor;
    }


}
