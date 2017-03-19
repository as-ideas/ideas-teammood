package de.axelspringer.ideas.team.mood.moods.entity;

public enum TeamMoodValueType {
    excellent("#afffc4", 4),
    good("#d2fdea", 3),
    average("#efeff1", 2),
    hard("#fff8bf", 1),
    bad("#ffc2c1", 0);

    public final String hexColor;
    public final Integer moodValue;

    TeamMoodValueType(String hexColor, Integer moodValue) {
        this.hexColor = hexColor;
        this.moodValue = moodValue;
    }


}
