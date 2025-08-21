package com.github.krystianmuchla;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    final List<List<Kid>> slots;

    public Plan() {
        this(new ArrayList<>());
    }

    public Plan(List<List<Kid>> slots) {
        this.slots = slots;
    }
}
