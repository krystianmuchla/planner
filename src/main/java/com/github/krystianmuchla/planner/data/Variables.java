package com.github.krystianmuchla.planner.data;

import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.IntVar;

public class Variables {
    public final BoolVar[][] slots;
    public final IntVar[] slotSizes;
    public final IntVar[] slotDeviations;
    public final IntVar maxSlotSize;

    public Variables(BoolVar[][] slots, IntVar[] slotSizes, IntVar[] slotDeviations, IntVar maxSlotSize) {
        this.slots = slots;
        this.slotSizes = slotSizes;
        this.slotDeviations = slotDeviations;
        this.maxSlotSize = maxSlotSize;
    }
}
