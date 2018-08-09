package com.cs.logs.service

import com.cs.logs.infrastructure.EventRepository
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class EventProcessorSpec extends Specification {

	def repository = Mock(EventRepository)
	def processor = new EventProcessor(repository);

	def 'processed events have to be saved'() {
		given: "example events come from file"
		def events = Files.lines(Paths.get(readResource("example.log").toURI()));

		when: "the processor reads events from stream"
		processor.parseAndSave(events)

		then: "the event instances must be saved"
		1 * repository.prepareStorage()
		1 * repository.save(_)
	}

	private static URL readResource(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name)
	}

}