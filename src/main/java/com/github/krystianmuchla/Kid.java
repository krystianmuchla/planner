package com.github.krystianmuchla;

public class Kid {
    final Integer id;
    final String firstName;
    final String lastName;
    final String grade;
    final Boolean[] availabilities;

    public Kid(Integer id, String firstName, String lastName, String grade, Boolean[] availabilities) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
        this.availabilities = availabilities;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " " + grade;
    }
}
