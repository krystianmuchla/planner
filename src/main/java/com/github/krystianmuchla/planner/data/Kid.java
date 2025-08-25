package com.github.krystianmuchla.planner.data;

public class Kid {
    public final Integer id;
    public final String firstName;
    public final String lastName;
    public final String grade;
    public final Boolean individual;
    public final Boolean[] availabilities;

    public Kid(
        Integer id,
        String firstName,
        String lastName,
        String grade,
        Boolean individual,
        Boolean[] availabilities
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
        this.individual = individual;
        this.availabilities = availabilities;
    }

    public Boolean isAvailable(int slot) {
        return availabilities[slot];
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " " + grade;
    }
}
