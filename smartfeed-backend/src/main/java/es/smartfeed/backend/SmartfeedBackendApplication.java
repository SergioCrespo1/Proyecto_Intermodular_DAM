package es.smartfeed.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// RM - Añadi estas dos lineas por que los paquetes estan fuera de "es.smartfeed.backend"
@EntityScan(basePackages = "domain.model")
@EnableJpaRepositories(basePackages = "repository")
public class SmartfeedBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartfeedBackendApplication.class, args);
    }

}
