package es.danielmc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableCaching
public class DispositivosSpringBootApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DispositivosSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Aplicación iniciada correctamente");
    }
}
