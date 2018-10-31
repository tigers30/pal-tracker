package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {
    private final CounterService counterService;
    private final GaugeService gaugeService;
    private TimeEntryRepository timeEntryRepository;


    public TimeEntryController(TimeEntryRepository timeEntryRepository, CounterService counterService, GaugeService gaugeService) {
        this.timeEntryRepository = timeEntryRepository;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry timeEntryCreated= timeEntryRepository.create(timeEntryToCreate);
        this.counterService.increment("TimeEntry.create");
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity<>(timeEntryCreated, HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {
        TimeEntry timeEntryIdCreated = timeEntryRepository.find(timeEntryId);
        if (timeEntryIdCreated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            this.counterService.increment("TimeEntry.read");
            return new ResponseEntity<>(timeEntryIdCreated, HttpStatus.OK);
        }
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        List<TimeEntry> timeEntryListCreated= timeEntryRepository.list();
        this.counterService.increment("TimeEntry.listed");
        return new ResponseEntity<>(timeEntryListCreated, HttpStatus.OK);
    }

    @PutMapping("/time-entries/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry expected) {
        TimeEntry updatedTimeEntry = timeEntryRepository.update(timeEntryId, expected);

        if (updatedTimeEntry == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            this.counterService.increment("TimeEntry.update");
            return new ResponseEntity<>(updatedTimeEntry, HttpStatus.OK);
        }
    }

    @DeleteMapping("/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long timeEntryId) {
        timeEntryRepository.delete(timeEntryId);
        this.counterService.increment("TimeEntry.delete");
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
