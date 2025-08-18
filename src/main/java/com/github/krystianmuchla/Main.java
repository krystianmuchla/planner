package com.github.krystianmuchla;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;

import java.util.ArrayList;
import java.util.Set;

public class Main {
    private static final Set<CpSolverStatus> ACCEPTABLE_STATUSES = Set.of(CpSolverStatus.OPTIMAL, CpSolverStatus.FEASIBLE);

    public static void main(String[] args) {
        var teacher = new boolean[]{false, true, true, false};
        var kids = new boolean[][]{
            {false, true, true, true},
            {false, true, false, false},
            {false, true, false, false},
            {false, false, true, false},
        };
        Loader.loadNativeLibraries();
        var model = new CpModel();
        var variables = Cp.defineVariables(model, teacher, kids);
        Cp.defineConstraints(model, variables, teacher, kids);
        Cp.defineObjectiveFunction(model, variables, teacher, kids);

        var solver = new CpSolver();
        var status = solver.solve(model);

        if (ACCEPTABLE_STATUSES.contains(status)) {
            throw new RuntimeException("Could not find an optimal or feasible solution");
        }

        for (var slot = 0; slot < teacher.length; slot++) {
            var list = new ArrayList<Boolean>();
            for (var kid = 0; kid < kids.length; kid++) {
                list.add(solver.booleanValue(variables[slot][kid]));
            }
            var result = new StringBuilder();
            for (var kid : list) {
                result.append(kid ? "1" : "0");
            }
            System.out.printf("slot %d: %s%n", slot, result);
        }
    }
}
