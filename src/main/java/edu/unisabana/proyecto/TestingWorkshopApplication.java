package edu.unisabana.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para el taller de Testing.
 * Permite ejecutar la aplicación y acceder a los repositorios JPA con H2.
 */
@SpringBootApplication
public class TestingWorkshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestingWorkshopApplication.class, args);
    }
}
