package pl.com.seremak.simplebills.planning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class PlanningApplication {

    public static void main(final String[] args) {
        SpringApplication.run(PlanningApplication.class, args);
    }

}
