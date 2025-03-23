package info.ejava.examples.app.logging.command;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("factory")
public class LombokDeclaredSlf4jLogger implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
      log.info("Lombok declared slf4j logger")  ;
    }
    
}
