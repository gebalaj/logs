package com.cs.logs.service;

import com.cs.logs.infrastructure.EventRepository;
import com.cs.logs.model.Event;
import com.cs.logs.model.EventLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class EventProcessor {

    private final static Logger log = LoggerFactory.getLogger(EventProcessor.class);
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private EventRepository repository;

    public void parseAndSave(Stream<String> data) {
        repository.prepareStorage();

        ObjectMapper mapper = new ObjectMapper();
        try (Stream<String> lines = data) {
            Iterators
                    .partition(parse(mapper, lines).iterator(), BATCH_SIZE)
                    .forEachRemaining(event -> repository.save(event));
        }
    }

    public Stream<Event> parse(ObjectMapper mapper, Stream<String> lines) {
        return lines
                .map(line -> deserialize(mapper, line, EventLog.class))
                .map(groupAndCountDuration(EventLog::getId))
                .filter(Objects::nonNull);
    }


    private static <T> T deserialize(final ObjectMapper mapper, final String source, final Class<T> targetClass) {
        if(source == null || targetClass == null) {
            return null;
        }

        try {
            return mapper.readValue(source, targetClass);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private static Function<EventLog, Event> groupAndCountDuration(Function<EventLog, String> keyExtractor) {
        ConcurrentHashMap<String, EventLog> map = new ConcurrentHashMap<>();
        return log -> groupAndCountDuration(map, keyExtractor, log);
    }

    private static Event groupAndCountDuration(ConcurrentHashMap<String, EventLog> map,
                                               Function<EventLog, String> keyExtractor,
                                               EventLog log) {
        String key = keyExtractor.apply(log);
        EventLog previous = map.putIfAbsent(key, log);
        long duration = countDuration(previous, log);
        if (duration < 0) {
            return null;
        }
        map.remove(key);
        return Event.create(log.getId(), duration)
                .withAlert(duration > 4)
                .withHost(log.getHost())
                .withType(log.getType());
    }

    private static long countDuration(EventLog previous, EventLog log) {
        long duration = -1;
        if (previous != null) {
            if (log.isFinished() && previous.isStarted()) {
                duration = log.getTimestamp() - previous.getTimestamp();
            } else if (previous.isFinished() && log.isStarted()) {
                duration = previous.getTimestamp() - log.getTimestamp();
            }
        }
        return duration;
    }
}
