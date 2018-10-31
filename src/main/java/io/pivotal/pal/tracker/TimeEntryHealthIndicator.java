package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    private final JdbcTimeEntryRepository jdbcTimeEntryRepository;

    public TimeEntryHealthIndicator(JdbcTimeEntryRepository jdbcTimeEntryRepository){
        this.jdbcTimeEntryRepository = jdbcTimeEntryRepository;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        if(jdbcTimeEntryRepository.list().size()>5){
            builder.down();
        }else builder.up();
        return builder.build();
    }
}
