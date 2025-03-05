package info.ejava.examples.svc.content.quotes.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name="test",havingValue="true", matchIfMissing = true )
@Slf4j
public class Populate implements CommandLineRunner {

    private final QuotesService quoteService;
    private QuoteDTOFactory quoteDTOFactory =  new QuoteDTOFactory();

    @Override
    public void run(String... args) throws Exception {
        int count = 20;
        log.warn("Populating {} quotes. ", count);
        quoteDTOFactory.listBuilder().make(count,count)
                        .getQuotes()
                        .stream().forEach(quote -> quoteService.createQuote(quote));
    }

    
    
}
