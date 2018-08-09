package com.cs.logs;

import com.cs.logs.model.EventLog;
import com.cs.logs.service.EventProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PerformanceComparison {

	public static void main(String[] arg){
        for (int i=0; i < 5; i++) {
            EventProcessor processor = new EventProcessor();
            System.out.println("********************* PerformanceComparison ****************");
            System.out.println("1) Start processing sequence stream");
            final long start = System.currentTimeMillis();
            processor.parse(new ObjectMapper(), sampleData()).forEach(c -> c.getId());
            System.out.println("Execution time: " + (System.currentTimeMillis() - start));
            System.out.println("************************************************************");
            System.out.println("2) Start processing parallel stream");
            final long start2 = System.currentTimeMillis();
            processor.parse(new ObjectMapper(), sampleData().parallel()).forEach(c -> c.getId());
            System.out.println("Execution time: " + (System.currentTimeMillis() - start2));
            System.out.println("**************************  End   **************************");
        }
	}

	private static Stream<String> sampleData() {
		return IntStream.range(0, 1_000_000).parallel().mapToObj(i ->
				EventLog.create("id" + i, (i % 2 == 0 ? EventLog.State.FINISHED : EventLog.State.STARTED), Instant.now().toEpochMilli()).toString()
		);
	}
}