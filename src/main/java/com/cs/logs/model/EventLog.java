package com.cs.logs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class EventLog {

	public enum State {
		STARTED, FINISHED
	}

	private String id;
	private State state;
	private long timestamp;
	private String type;
	private String host;

	private EventLog(String id, State state, long timestamp) {
		this.id = id;
		this.state = state;
		this.timestamp = timestamp;
	}

	@JsonCreator
	public static EventLog create(@JsonProperty("id") String id,
								  @JsonProperty("state") State state,
								  @JsonProperty("timestamp") long timestamp) {
		return new EventLog(id, state, timestamp);
	}

	public EventLog withType(String type) {
		this.type = type;
		return this;
	}

	public EventLog withHost(String host) {
		this.host = host;
		return this;
	}

	public String getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getType() {
		return type;
	}

	public String getHost() {
		return host;
	}

	public boolean isStarted() {
		return state == State.STARTED;
	}

	public boolean isFinished() {
		return state == State.FINISHED;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof EventLog)) {
			return false;
		}
		EventLog eventLog = (EventLog) o;
		return Objects.equals(id, eventLog.id) &&
				state == eventLog.state;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, state);
	}

	@Override
	public String toString() {
		return String.format("{\"id\":\"%s\", \"state\":\"%s\", \"timestamp\":%d", id, state, timestamp)
				+ (type == null ? "" : String.format(", \"type\":\"%s\"", type))
				+ (host == null ? "" : String.format(", \"host\":\"%s\"", host))
				+ "}";
	}

}
