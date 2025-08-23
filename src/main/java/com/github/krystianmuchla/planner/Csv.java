package com.github.krystianmuchla.planner;

import com.github.krystianmuchla.planner.data.Availability;
import com.github.krystianmuchla.planner.data.Kid;
import com.github.krystianmuchla.planner.data.Plan;
import com.github.krystianmuchla.planner.data.Teacher;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Csv {
    private static final String TEACHER_PATH = "teacher.csv";
    private static final String GRADES_PATH = "grades.csv";
    private static final String KIDS_PATH = "kids.csv";
    private static final String PLAN_PATH = "plan.csv";

    public static Teacher readTeacher() throws IOException, CsvException {
        List<Availability> availabilities = new ArrayList<>();
        try (CSVReader reader = getReader(TEACHER_PATH)) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                String id = row[0];
                Boolean available = Boolean.parseBoolean(row[1]);
                Availability availability = new Availability(id, available);
                availabilities.add(availability);
            }
        }
        return new Teacher(availabilities.toArray(new Availability[0]));
    }

    public static Kid[] readKids() throws IOException, CsvException {
        Map<String, Grade> grades = readGrades().stream().collect(Collectors.toMap(grade -> grade.name, Function.identity()));
        List<Kid> kids = new ArrayList<>();
        try (CSVReader reader = getReader(KIDS_PATH)) {
            int id = 0;
            String[] row;
            while ((row = reader.readNext()) != null) {
                String firstName = row[0];
                String lastName = row[1];
                String grade = row[2];
                Boolean[] availabilities = grades.get(grade).availabilities.toArray(new Boolean[0]);
                kids.add(new Kid(id++, firstName, lastName, grade, availabilities));
            }
        }
        return kids.toArray(new Kid[0]);
    }

    public static void writePlan(Plan plan) throws IOException {
        List<String[]> rows = new ArrayList<>();
        String[] header = new String[plan.slots.size()];
        for (int x = 0; x < header.length; x++) {
            header[x] = plan.slots.get(x).id;
        }
        rows.add(header);
        boolean next = true;
        for (int y = 0; next; y++) {
            String[] row = new String[plan.slots.size()];
            boolean any = false;
            for (int x = 0; x < row.length; x++) {
                List<Kid> kids = plan.slots.get(x).kids;
                if (kids.size() > y) {
                    any = true;
                    row[x] = kids.get(y).toString();
                }
            }
            if (any) {
                rows.add(row);
            } else {
                next = false;
            }
        }
        try (CSVWriter writer = getWriter(PLAN_PATH)) {
            writer.writeAll(rows);
        }
    }

    private static List<Grade> readGrades() throws IOException, CsvException {
        List<Grade> grades = new ArrayList<>();
        try (CSVReader reader = getReader(GRADES_PATH)) {
            List<String[]> rows = reader.readAll();
            try {
                String[] names = rows.remove(0);
                for (int x = 1; x < names.length; x++) {
                    String name = names[x];
                    Grade grade = new Grade(name);
                    grades.add(grade);
                }
            } catch (IndexOutOfBoundsException exception) {
                return grades;
            }
            for (String[] row : rows) {
                for (int x = 1; x < row.length; x++) {
                    Boolean availability = Boolean.parseBoolean(row[x]);
                    grades.get(x - 1).availabilities.add(availability);
                }
            }
        }
        return grades;
    }

    private static CSVReader getReader(String path) throws FileNotFoundException {
        return new CSVReader(new FileReader(path));
    }

    private static CSVWriter getWriter(String path) throws IOException {
        return new CSVWriter(new FileWriter(path));
    }

    private static class Grade {
        final String name;
        final List<Boolean> availabilities;

        Grade(String name) {
            this(name, new ArrayList<>());
        }

        Grade(String name, List<Boolean> availabilities) {
            this.name = name;
            this.availabilities = availabilities;
        }
    }
}
