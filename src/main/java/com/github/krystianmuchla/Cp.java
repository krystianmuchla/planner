package com.github.krystianmuchla;

import com.google.ortools.sat.*;

import java.util.ArrayList;
import java.util.List;

// Constraint programming
public class Cp {
    public static Literal[][] defineVariables(CpModel model, Boolean[] teacher, Boolean[][] kids) {
        Literal[][] variables = new Literal[teacher.length][kids.length];
        for (int slot = 0; slot < teacher.length; slot++) {
            for (int kid = 0; kid < kids.length; kid++) {
                variables[slot][kid] = model.newBoolVar("slot_" + slot + "_kid_" + kid);
            }
        }
        return variables;
    }

    public static void defineConstraints(CpModel model, Literal[][] variables, Boolean[] teacher, Boolean[][] kids) {
        for (int kid = 0; kid < kids.length; kid++) {
            List<Literal> slots = new ArrayList<>();
            for (int slot = 0; slot < teacher.length; slot++) {
                slots.add(variables[slot][kid]);
            }
            model.addExactlyOne(slots);
        }
        for (int slot = 0; slot < teacher.length; slot++) {
            Boolean teacherAvailable = teacher[slot];
            for (int kid = 0; kid < kids.length; kid++) {
                Boolean kidAvailable = kids[kid][slot];
                if (!teacherAvailable || !kidAvailable) {
                    model.addEquality(variables[slot][kid], 0);
                }
            }
        }
    }

    public static void defineObjectiveFunction(CpModel model, Literal[][] variables, Boolean[] teacher, Boolean[][] kids) {
        IntVar[] slotSize = new IntVar[teacher.length];
        for (int slot = 0; slot < teacher.length; slot++) {
            LinearExprBuilder builder = LinearExpr.newBuilder();
            for (int kid = 0; kid < kids.length; kid++) {
                builder.add(variables[slot][kid]);
            }
            slotSize[slot] = model.newIntVar(0, kids.length, "slotSize_" + slot);
            model.addEquality(slotSize[slot], builder);
        }
        IntVar maxSlotSize = model.newIntVar(0, kids.length, "maxSlotSize");
        for (int slot = 0; slot < teacher.length; slot++) {
            model.addLessOrEqual(slotSize[slot], maxSlotSize);
        }
        model.minimize(maxSlotSize);
    }
}
