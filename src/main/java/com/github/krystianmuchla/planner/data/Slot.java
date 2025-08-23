package com.github.krystianmuchla.planner.data;

import java.util.List;

public class Slot {
    public final String id;
    public final List<Kid> kids;

    public Slot(String id, List<Kid> kids) {
        this.id = id;
        this.kids = kids;
    }
}
