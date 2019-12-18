package io.pivotal.metric_registrar.examples.spring_actuator_to_logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// The @EnableScheduling annotation is required to run the emitter on a scheduled basis
@EnableScheduling
public class SpringActuatorToLogsExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringActuatorToLogsExampleApplication.class, args);
	}

	// This bean is added so we can use dependency injection to get a logger into our emitter class
	// We preferred injecting the logger to enable mocking it in the unit tests of the emitter
	@Bean
	public Logger logger() {
		return LoggerFactory.getLogger("ExampleApplication");
	}
}
