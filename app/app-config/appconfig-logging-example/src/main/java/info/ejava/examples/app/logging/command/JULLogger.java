package info.ejava.examples.app.logging.command;

import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Component
@Profile("factory")
public class JULLogger implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(JULLogger.class.getName());
    @Override
    public void run(String... args) throws Exception {
        log.info("Java Util Logger message");
    }

}