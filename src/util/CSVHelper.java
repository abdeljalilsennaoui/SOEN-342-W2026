package util;

import model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private static final String HEADER = "TaskName,Description,Subtask,Status,Priority,DueDate,"
            + "ProjectName,ProjectDescription,Collaborator,CollaboratorCategory";

    public static void exportToCSV(List<Task> tasks, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(HEADER);

            for (Task task : tasks) {
                String projectName = "";
                String projectDesc = "";
                if (task.getProject() != null) {
                    projectName = escapeCsv(task.getProject().getName());
                    projectDesc = escapeCsv(task.getProject().getDescription());
                }

                // if task has subtasks, write one row per subtask
                if (!task.getSubtasks().isEmpty()) {
                    for (Subtask sub : task.getSubtasks()) {
                        String collabName = "";
                        String collabCat = "";
                        if (!task.getCollaborators().isEmpty()) {
                            Collaborator c = task.getCollaborators().get(0);
                            collabName = escapeCsv(c.getName());
                            collabCat = c.getCategoryType().name();
                        }
                        writer.println(
                                escapeCsv(task.getTitle()) + ","
                                + escapeCsv(task.getDescription()) + ","
                                + escapeCsv(sub.getTitle()) + ","
                                + task.getCompletionStatus() + ","
                                + task.getPriorityLevel() + ","
                                + (task.getDueDate() != null ? task.getDueDate() : "") + ","
                                + projectName + ","
                                + projectDesc + ","
                                + collabName + ","
                                + collabCat
                        );
                    }
                } else {
                    // one row per collaborator, or one row if no collaborators
                    if (!task.getCollaborators().isEmpty()) {
                        for (Collaborator c : task.getCollaborators()) {
                            writer.println(
                                    escapeCsv(task.getTitle()) + ","
                                    + escapeCsv(task.getDescription()) + ","
                                    + ","
                                    + task.getCompletionStatus() + ","
                                    + task.getPriorityLevel() + ","
                                    + (task.getDueDate() != null ? task.getDueDate() : "") + ","
                                    + projectName + ","
                                    + projectDesc + ","
                                    + escapeCsv(c.getName()) + ","
                                    + c.getCategoryType().name()
                            );
                        }
                    } else {
                        writer.println(
                                escapeCsv(task.getTitle()) + ","
                                + escapeCsv(task.getDescription()) + ","
                                + ","
                                + task.getCompletionStatus() + ","
                                + task.getPriorityLevel() + ","
                                + (task.getDueDate() != null ? task.getDueDate() : "") + ","
                                + projectName + ","
                                + projectDesc + ","
                                + ","
                        );
                    }
                }
            }
        }
    }

    public static List<String[]> parseCSV(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] fields = parseCsvLine(line);
                rows.add(fields);
            }
        }
        return rows;
    }

    // simple CSV line parser that handles quoted fields
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());

        // pad to 10 columns
        while (fields.size() < 10) {
            fields.add("");
        }
        return fields.toArray(new String[0]);
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        return LocalDate.parse(dateStr.trim());
    }

    public static CompletionStatus parseStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) return CompletionStatus.OPEN;
        try {
            return CompletionStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CompletionStatus.OPEN;
        }
    }

    public static PriorityLevel parsePriority(String priorityStr) {
        if (priorityStr == null || priorityStr.trim().isEmpty()) return PriorityLevel.MEDIUM;
        try {
            return PriorityLevel.valueOf(priorityStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PriorityLevel.MEDIUM;
        }
    }

    public static CollaboratorCategoryType parseCategory(String catStr) {
        if (catStr == null || catStr.trim().isEmpty()) return null;
        try {
            return CollaboratorCategoryType.valueOf(catStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
