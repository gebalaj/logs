package com.cs.logs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Event {

	private String id;
	private long duration;
	private String type;
	private String host;
	private boolean alert;

	private Event(String id, long duration) {
		this.id = id;
		this.duration = duration;
	}

	@JsonCreator
	public static Event create(@JsonProperty("id") String id,
                               @JsonProperty("duration") long duration) {
		return new Event(id, duration);
	}

	public Event withType(String type) {
		this.type = type;
		return this;
	}

	public Event withAlert(boolean alert) {
		this.alert = alert;
		return this;
	}

	public Event withHost(String host) {
		this.host = host;
		return this;
	}

	public String getId() {
		return id;
	}

	public long getDuration() {
		return duration;
	}

	public String getType() {
		return type;
	}

	public String getHost() {
		return host;
	}

	public boolean isAlert() {
		return alert;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Event)) {
			return false;
		}
		Event event = (Event) o;
		return Objects.equals(getId(), event.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return String.format("{\"id\":\"%s\", \"duration\":%d, \"alert\":\"%s\"", id, duration, alert)
				+ (type == null ? "" : String.format(", \"type\":\"%s\"", type))
				+ (host == null ? "" : String.format(", \"host\":\"%s\"", host))
				+ "}";
	}

}
