package com.cs.logs;

import com.cs.logs.model.EventLog;
import com.cs.logs.service.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static eu.amidst.core.utils.FixedBatchParallelSpliteratorWrapper.toFixedBatchStream;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.range;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final String SAMPLE_FILENAME = "sample.log";

    @Autowired
	private EventProcessor processor;

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	@ConfigurationProperties("spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Override
	public void run(String... strings) throws IOException {
		if (strings.length == 0) {
			printUsageHelp();
			return;
		}

		final Path inputPath = "sample".equals(strings[0])
				? sampleInput()
				: Paths.get(strings[0]);

		if (Files.notExists(inputPath)) {
			log.error(String.format("Cannot locate '%s'.", inputPath));
			printUsageHelp();
			return;
		}

		System.out.println("Start processing...");
		final long start = System.nanoTime();
		processor.parseAndSave(Files.lines(inputPath));
		final double execTime = System.nanoTime() - start;
		printTimeInSec("Execution time", execTime);
	}

	private Path sampleInput() throws IOException {
        final long start = System.nanoTime();
		String idPrefix = "scsmbstgr";
		final Path inputPath = Paths.get(SAMPLE_FILENAME);
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(inputPath))) {
			range(0, 10_000_000).parallel().forEach(i -> {
				writer.println(EventLog.create(idPrefix + i, EventLog.State.STARTED, Instant.now().toEpochMilli()));
				writer.println(EventLog.create(idPrefix + i, EventLog.State.FINISHED, Instant.now().toEpochMilli()));
			});
		}
        final double execTime = System.nanoTime() - start;
		printTimeInSec("Sample file generation", execTime);
		return inputPath;
	}

    private static double inSecond(double execTime) {
        return execTime / SECONDS.toNanos(1);
    }

    private void printUsageHelp() {
		printSeparator();
		System.out.println("usage: ");
		System.out.println("	gradlew bootRun --args [absolute file path]");
		System.out.println("or	");
		System.out.println("	gradlew bootRun --args sample");
		printSeparator();
	}

	private void printTimeInSec(String message, double execTime) {
		printSeparator();
		System.out.format("%s: %.2f s\n", message, inSecond(execTime));
		printSeparator();
	}

	private void printSeparator() {
		System.out.println();
		System.out.println("**********************************************************");
		System.out.println();
	}

}