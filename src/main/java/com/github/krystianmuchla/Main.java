package com.github.krystianmuchla;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.Literal;

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
            Teacher teacher = Csv.readTeacher();
            Kid[] kids = Csv.readKids();
            Plan plan = createPlan(teacher, kids);
            Csv.writePlan(plan);
        } catch (Exception exception) {
            // todo write error to file
            throw new RuntimeException(exception);
        }
    }

    private static Plan createPlan(Teacher teacher, Kid[] kids) {
        Boolean[] rawTeacher = teacher.availabilities;
        Boolean[][] rawKids = Arrays.stream(kids).map(kid -> kid.availabilities).toArray(Boolean[][]::new);

        Loader.loadNativeLibraries();

        CpModel model = new CpModel();
        Literal[][] variables = Cp.defineVariables(model, rawTeacher, rawKids);
        Cp.defineConstraints(model, variables, rawTeacher, rawKids);
        Cp.defineObjectiveFunction(model, variables, rawTeacher, rawKids);

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (!ACCEPTABLE_STATUSES.contains(status)) {
            throw new RuntimeException("Could not find an optimal or feasible solution");
        }

        Plan plan = new Plan();
        Map<Integer, Kid> kidMap = Arrays.stream(kids).collect(Collectors.toMap(kid -> kid.id, Function.identity()));
        for (int slot = 0; slot < teacher.availabilities.length; slot++) {
            List<Kid> planSlot = new ArrayList<>();
            for (int kid = 0; kid < kids.length; kid++) {
                Boolean kidAttending = solver.booleanValue(variables[slot][kid]);
                if (kidAttending) {
                    planSlot.add(kidMap.get(kid));
                }
            }
            plan.slots.add(planSlot);
        }

        return plan;
    }
}
