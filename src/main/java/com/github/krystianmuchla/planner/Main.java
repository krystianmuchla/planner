package com.github.krystianmuchla.planner;

import com.github.krystianmuchla.planner.data.*;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    private static final Set<CpSolverStatus> ACCEPTABLE_STATUSES = new HashSet<>();

    static {
        ACCEPTABLE_STATUSES.add(CpSolverStatus.OPTIMAL);
        ACCEPTABLE_STATUSES.add(CpSolverStatus.FEASIBLE);
    }

    public static void main(String[] args) {
        try {
            Kid[] kids = Csv.readKids();
            if (kids.length == 0) {
                throw new IllegalStateException("Could not create plan for empty kid list");
            }
            String[] slots = resolveSlots(kids[0]);
            Plan plan = createPlan(slots, kids);
            Csv.writePlan(plan);
        } catch (Exception exception) {
            try (FileWriter fileWriter = new FileWriter("error.txt")) {
                StringWriter stringWriter = new StringWriter();
                exception.printStackTrace(new PrintWriter(stringWriter));
                fileWriter.write(stringWriter.toString());
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
            System.exit(1);
        }
    }

    private static String[] resolveSlots(Kid kid) {
        return Arrays.stream(kid.availabilities).map(availability -> availability.id).toArray(String[]::new);
    }

    private static Plan createPlan(String[] slots, Kid[] kids) {
        Loader.loadNativeLibraries();

        CpModel model = new CpModel();
        Variables variables = Cp.defineVariables(model, slots.length, kids);
        Cp.defineConstraints(model, variables, slots.length, kids);
        Cp.defineObjectiveFunction(model, variables);

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (!ACCEPTABLE_STATUSES.contains(status)) {
            throw new RuntimeException("Could not find an optimal or feasible solution, received: " + status);
        }

        Plan plan = new Plan();
        Map<Integer, Kid> kidMap = Arrays.stream(kids).collect(Collectors.toMap(kid -> kid.id, Function.identity()));
        for (int slotNumber = 0; slotNumber < slots.length; slotNumber++) {
            String slotId = slots[slotNumber];
            List<Kid> slotKids = new ArrayList<>();
            for (int kidId = 0; kidId < kids.length; kidId++) {
                Boolean kidAttending = solver.booleanValue(variables.slots[slotNumber][kidId]);
                if (kidAttending) {
                    slotKids.add(kidMap.get(kidId));
                }
            }
            Slot slot = new Slot(slotId, slotKids);
            plan.slots.add(slot);
        }

        return plan;
    }
}
