package com.github.krystianmuchla;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.Literal;

import java.util.ArrayList;

// Constraint programming
public class Cp {
    public static Literal[][] defineVariables(CpModel model, boolean[] teacher, boolean[][] kids) {
        var variables = new Literal[teacher.length][kids.length];
        for (var slot = 0; slot < teacher.length; slot++) {
            for (var kid = 0; kid < kids.length; kid++) {
                variables[slot][kid] = model.newBoolVar("slot_%d_kid_%d".formatted(slot, kid));
            }
        }
        return variables;
    }

    public static void defineConstraints(CpModel model, Literal[][] variables, boolean[] teacher, boolean[][] kids) {
        for (var kid = 0; kid < kids.length; kid++) {
            var slots = new ArrayList<Literal>();
            for (var slot = 0; slot < teacher.length; slot++) {
                slots.add(variables[slot][kid]);
            }
            model.addExactlyOne(slots);
        }
        for (var slot = 0; slot < teacher.length; slot++) {
            var teacherAvailable = teacher[slot];
            for (var kid = 0; kid < kids.length; kid++) {
                var kidAvailable = kids[kid][slot];
                if (!teacherAvailable || !kidAvailable) {
                    model.addEquality(variables[slot][kid], 0);
                }
            }
        }
    }

    public static void defineObjectiveFunction(CpModel model, Literal[][] variables, boolean[] teacher, boolean[][] kids) {
        var slotSize = new IntVar[teacher.length];
        for (int slot = 0; slot < teacher.length; slot++) {
            var builder = LinearExpr.newBuilder();
            for (int kid = 0; kid < kids.length; kid++) {
                builder.add(variables[slot][kid]);
            }
            slotSize[slot] = model.newIntVar(0, kids.length, "slotSize_" + slot);
            model.addEquality(slotSize[slot], builder);
        }
        var maxSlotSize = model.newIntVar(0, kids.length, "maxSlotSize");
        for (int slot = 0; slot < teacher.length; slot++) {
            model.addLessOrEqual(slotSize[slot], maxSlotSize);
        }
        model.minimize(maxSlotSize);
    }
}
