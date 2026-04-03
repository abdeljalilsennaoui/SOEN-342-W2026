package model;

import util.CompletionStatus;

public class Subtask {
    private static int counter = 1;

    private String subtaskId;
    private String title;
    private CompletionStatus completionStatus;

    public Subtask(String title) {
        this.subtaskId = "SUB-" + counter++;
        this.title = title;
        this.completionStatus = CompletionStatus.OPEN;
    }

    public void setCompletion(CompletionStatus completionStatus) {
        this.completionStatus = completionStatus;
    }

    public String getSubtaskId() { return subtaskId; }
    public String getTitle() { return title; }
    public CompletionStatus getCompletionStatus() { return completionStatus; }

    public void setSubtaskId(String subtaskId) { this.subtaskId = subtaskId; }
    public static void resetCounter(int n) { counter = n; }

    @Override
    public String toString() {
        return "Subtask{id='" + subtaskId + "', title='" + title
                + "', status=" + completionStatus + "}";
    }
}
