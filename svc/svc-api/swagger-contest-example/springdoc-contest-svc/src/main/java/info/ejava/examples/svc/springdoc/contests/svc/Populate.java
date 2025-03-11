package info.ejava.examples.svc.springdoc.contests.svc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import info.ejava.examples.svc.springdoc.contest.dto.ContestDTOFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(name = "test", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class Populate implements CommandLineRunner {
    private final ContestService contestService;
    private ContestDTOFactory contestDTOFactory = new ContestDTOFactory();

    @Override
    public void run(String... args) throws Exception {
        int count = 20;
        log.info("populating {} contents", count);
        contestDTOFactory.listBuilder().make(count, count)
                .getContests()
                .stream()
                .forEach(q -> contestService.createContest(q));

    }
}
