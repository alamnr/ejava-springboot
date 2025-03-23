package info.ejava.examples.app.logging.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component

public class SLF4JLogger implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SLF4JLogger.class);
    @Override
    public void run(String... args) throws Exception {
        log.info("declared slf4j logger message");
    }
    
}
