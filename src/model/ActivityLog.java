package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityLog {
    private List<ActivityEntry> entries;

    public ActivityLog() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(String actionDescription) {
        entries.add(new ActivityEntry(LocalDateTime.now(), actionDescription));
    }

    public void addRestoredEntry(LocalDateTime timestamp, String actionDescription) {
        entries.add(new ActivityEntry(timestamp, actionDescription));
    }

    public List<ActivityEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public List<ActivityEntry> getEntriesByDateRange(LocalDate from, LocalDate to) {
        return entries.stream()
                .filter(e -> {
                    LocalDate date = e.getTimestamp().toLocalDate();
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .collect(Collectors.toList());
    }
}
