package model;

import util.CompletionStatus;
import java.time.LocalDate;

public class TaskOccurrence {
    private static int counter = 1;

    private String occurrenceId;
    private LocalDate dueDate;
    private CompletionStatus completionStatus;

    public TaskOccurrence(LocalDate dueDate) {
        this.occurrenceId = "OCC-" + counter++;
        this.dueDate = dueDate;
        this.completionStatus = CompletionStatus.OPEN;
    }

    public void complete() {
        this.completionStatus = CompletionStatus.COMPLETED;
    }

    public String getOccurrenceId() { return occurrenceId; }
    public LocalDate getDueDate() { return dueDate; }
    public CompletionStatus getCompletionStatus() { return completionStatus; }

    public void setOccurrenceId(String occurrenceId) { this.occurrenceId = occurrenceId; }
    public static void resetCounter(int n) { counter = n; }

    @Override
    public String toString() {
        return "Occurrence{id='" + occurrenceId + "', due=" + dueDate
                + ", status=" + completionStatus + "}";
    }
}
