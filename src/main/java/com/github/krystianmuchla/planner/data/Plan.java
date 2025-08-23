package com.github.krystianmuchla.planner.data;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    public final List<Slot> slots;

    public Plan() {
        this(new ArrayList<>());
    }

    public Plan(List<Slot> slots) {
        this.slots = slots;
    }
}
