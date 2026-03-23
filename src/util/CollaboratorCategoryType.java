package util;

public enum CollaboratorCategoryType {
    JUNIOR(10),
    INTERMEDIATE(5),
    SENIOR(2);

    private final int taskLimit;

    CollaboratorCategoryType(int taskLimit) {
        this.taskLimit = taskLimit;
    }

    public int getTaskLimit() {
        return taskLimit;
    }
}
