package model;

import java.time.LocalDateTime;

public class ActivityEntry {
    private LocalDateTime timestamp;
    private String actionDescription;

    public ActivityEntry(LocalDateTime timestamp, String actionDescription) {
        this.timestamp = timestamp;
        this.actionDescription = actionDescription;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getActionDescription() { return actionDescription; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + actionDescription;
    }
}
