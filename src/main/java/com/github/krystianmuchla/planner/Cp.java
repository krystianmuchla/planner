package com.github.krystianmuchla.planner;

import com.github.krystianmuchla.planner.data.Kid;
import com.github.krystianmuchla.planner.data.Variables;
import com.google.ortools.sat.*;

import java.util.ArrayList;
import java.util.List;

// Constraint programming
public class Cp {
    public static Variables defineVariables(CpModel model, int slotsSize, Kid[] kids) {
        BoolVar[][] slots = new BoolVar[slotsSize][kids.length];
        IntVar[] slotSizes = new IntVar[slotsSize];
        IntVar maxSlotSize = model.newIntVar(0, kids.length, "maxSlotSize");
        for (int slot = 0; slot < slotsSize; slot++) {
            slotSizes[slot] = model.newIntVar(0, kids.length, "slotSize_" + slot);
            for (int kid = 0; kid < kids.length; kid++) {
                slots[slot][kid] = model.newBoolVar("slot_" + slot + "_kid_" + kid);
            }
        }
        return new Variables(slots, slotSizes, maxSlotSize);
    }

    public static void defineConstraints(CpModel model, Variables variables, int slotsSize, Kid[] kids) {
        BoolVar[][] slots = variables.slots;
        IntVar[] slotSizes = variables.slotSizes;
        IntVar maxSlotSize = variables.maxSlotSize;
        for (int kid = 0; kid < kids.length; kid++) {
            List<Literal> kidSlots = new ArrayList<>();
            for (int slot = 0; slot < slotsSize; slot++) {
                kidSlots.add(slots[slot][kid]);
            }
            model.addExactlyOne(kidSlots);
        }
        for (int slot = 0; slot < slotsSize; slot++) {
            for (int kid = 0; kid < kids.length; kid++) {
                Boolean kidAvailable = kids[kid].isAvailable(slot);
                if (!kidAvailable) {
                    model.addEquality(slots[slot][kid], 0);
                }
            }
        }
        for (int slot = 0; slot < slotsSize; slot++) {
            LinearExprBuilder builder = LinearExpr.newBuilder();
            for (int kid = 0; kid < kids.length; kid++) {
                builder.add(slots[slot][kid]);
            }
            model.addEquality(slotSizes[slot], builder);
            model.addLessOrEqual(slotSizes[slot], maxSlotSize);
        }
        for (int kid = 0; kid < kids.length; kid++) {
            if (kids[kid].individual) {
                for (int slot = 0; slot < slotsSize; slot++) {
                    model.addLessOrEqual(slotSizes[slot], 1).onlyEnforceIf(slots[slot][kid]);
                }
            }
        }
    }

    public static void defineObjectiveFunction(CpModel model, Variables variables) {
        IntVar maxSlotSize = variables.maxSlotSize;
        model.minimize(maxSlotSize);
    }
}
