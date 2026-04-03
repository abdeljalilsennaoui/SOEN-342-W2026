package model;

import util.CollaboratorCategoryType;

public class Collaborator {
    private static int counter = 1;

    private String collaboratorId;
    private String name;
    private CollaboratorCategoryType categoryType;
    private int taskLimit;

    public Collaborator(String name, CollaboratorCategoryType categoryType) {
        this.collaboratorId = "COL-" + counter++;
        this.name = name;
        this.categoryType = categoryType;
        this.taskLimit = categoryType.getTaskLimit();
    }

    public boolean canAcceptTask(int openTaskCount) {
        return openTaskCount < taskLimit;
    }

    public String getCollaboratorId() { return collaboratorId; }
    public String getName() { return name; }
    public CollaboratorCategoryType getCategoryType() { return categoryType; }
    public int getTaskLimit() { return taskLimit; }
    public void setTaskLimit(int taskLimit) {
        if (taskLimit <= 0) throw new IllegalArgumentException("Task limit must be a positive integer.");
        this.taskLimit = taskLimit;
    }
    public void setCollaboratorId(String collaboratorId) { this.collaboratorId = collaboratorId; }
    public static void resetCounter(int n) { counter = n; }

    @Override
    public String toString() {
        return "Collaborator{id='" + collaboratorId + "', name='" + name
                + "', category=" + categoryType + ", limit=" + taskLimit + "}";
    }
}
