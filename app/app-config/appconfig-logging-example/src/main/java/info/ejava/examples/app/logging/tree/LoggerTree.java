package info.ejava.examples.app.logging.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
//@Profile("!rollover")
@Profile("tree")
public class LoggerTree implements CommandLineRunner {
    private static final Logger[] loggers = {
            LoggerFactory.getLogger("X"),
            LoggerFactory.getLogger("X.1"),
            LoggerFactory.getLogger("X.2"),
            LoggerFactory.getLogger("X.3"),
            LoggerFactory.getLogger("Y.3"),
            LoggerFactory.getLogger("Y.3.p"),
            LoggerFactory.getLogger("security"),
        };

    @Override
    public void run(String... args) throws Exception {
        for (Logger log: loggers) {
            log.trace("{} trace", log.getName());
            log.debug("{} debug", log.getName());
            log.info("{} info", log.getName());
            log.warn("{} warn", log.getName());
            log.error("{} error", log.getName());
        }
    }
}
