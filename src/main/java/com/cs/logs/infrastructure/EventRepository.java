package com.cs.logs.infrastructure;

import com.cs.logs.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EventRepository {

    private final static Logger log = LoggerFactory.getLogger(EventRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void prepareStorage() {
        jdbcTemplate.execute("DROP TABLE events IF EXISTS");
        jdbcTemplate.execute("CREATE CACHED TABLE events(" +
                "id VARCHAR(255), duration BIGINT, alert BOOLEAN, type VARCHAR(255), host VARCHAR(255))");
        log.info("Created event table");
    }

    public void save(List<Event> events) {
        String sql = "INSERT INTO events(id, duration, alert, type, host) VALUES (?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Event e = events.get(i);
                ps.setString(1, e.getId());
                ps.setLong(2, e.getDuration());
                ps.setBoolean(3, e.isAlert());
                ps.setString(4, e.getType());
                ps.setString(5, e.getHost());
            }

            public int getBatchSize() {
                return events.size();
            }
        });
    }

    public int count() {
        Number number = jdbcTemplate.queryForObject("SELECT count(*) FROM events", Integer.class);
        return (number != null ? number.intValue() : 0);
    }

}
