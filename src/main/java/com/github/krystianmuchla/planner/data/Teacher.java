package com.github.krystianmuchla.planner.data;

public class Teacher {
    public final Availability[] availabilities;

    public Teacher(Availability[] availabilities) {
        this.availabilities = availabilities;
    }

    public int getSlotsLength() {
        return availabilities.length;
    }

    public Boolean isAvailable(int slot) {
        return availabilities[slot].available;
    }
}
