package io.pivotal.pal.tracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private HashMap<Long, TimeEntry> timeEntries = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        long id = timeEntries.size() + 1L;
        TimeEntry createdTimeEntry = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        timeEntries.put(id, createdTimeEntry);
        return createdTimeEntry;
    }

    public TimeEntry find(long id) {
        return timeEntries.get(id);
    }


    public List<TimeEntry> list() {

        return new ArrayList<>( timeEntries.values());
    }

    public void delete(long id) {
        timeEntries.remove(id);
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry timeEntryToUpdate = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        timeEntries.put(id, timeEntryToUpdate);
        return timeEntryToUpdate;
    }
}
