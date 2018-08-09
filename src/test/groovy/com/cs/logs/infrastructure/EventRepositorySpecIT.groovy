package com.cs.logs.infrastructure

import com.cs.logs.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class EventRepositorySpecIT extends Specification {

	@Autowired
	EventRepository repository;

	def 'should save events'() {
		given:
		def events = [Event.create("scsmbstgra", 1).withHost("host"),
					  Event.create("scsmbstgrb", 10).withAlert(true),
					  Event.create("scsmbstgrc", 8).withAlert(true)]

		repository.prepareStorage()

		when:
		repository.save(events)

		then:
		repository.count() == events.size()
	}

}