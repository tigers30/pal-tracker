package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository (DataSource dataSource){
       this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public TimeEntry create(TimeEntry entry) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(connection->{
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?,?,?,?)", RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, entry.getProjectId());
            preparedStatement.setLong(2,entry.getUserId());
            preparedStatement.setDate(3, Date.valueOf(entry.getDate()));
            preparedStatement.setInt(4,entry.getHours());
            return preparedStatement;
        }, keyHolder);
        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return this.jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?", new Object[]{timeEntryId}, extractor);
    }

    @Override
    public TimeEntry update(long eq, TimeEntry entry) {
        this.jdbcTemplate.update("UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?", entry.getProjectId(), entry.getUserId(), entry.getDate(), entry.getHours(), eq);
        return find(eq);
    }

    @Override
    public void delete(long timeEntryId) {
        jdbcTemplate.execute("DELETE FROM time_entries where id = " + timeEntryId);
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> timeEntries = this.jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries", new RowMapper<TimeEntry>() {
            @Override
            public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new TimeEntry(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getDate(4).toLocalDate(), rs.getInt(5));
            }
        });
        return timeEntries;
    }

    private static ResultSetExtractor<TimeEntry> extractor = (resultSet)->{
        if(resultSet.next()){
            return new TimeEntry(resultSet.getLong("id"), resultSet.getLong("project_id"), resultSet.getLong("user_id"), resultSet.getDate("date").toLocalDate(), resultSet.getInt("hours"));
        }else{
            return null;
        }
    };
}
