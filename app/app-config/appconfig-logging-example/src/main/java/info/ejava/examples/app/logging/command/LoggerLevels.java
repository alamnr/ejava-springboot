package info.ejava.examples.app.logging.command;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("levels")
public class LoggerLevels implements CommandLineRunner {

    private static final boolean DEBUG_ENABLED = log.isDebugEnabled();
    @Override
    public void run(String... args) throws Exception {
        log.trace("trace message"); 
        log.debug("debug message");
        log.info("info message");
        log.warn("warn message");
        log.error("error message");
        
        if (log.isDebugEnabled()) { 
            log.debug("debug for expensiveToLog: !");
        }

        if (DEBUG_ENABLED) { 
            log.debug("debug for expensiveToLog: {} !", 5624);
        }
    }
    
}
