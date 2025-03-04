package info.ejava.examples.svc.content.quotes.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;
import info.ejava.examples.svc.content.quotes.services.QuotesService;
import lombok.extern.slf4j.Slf4j;
import static info.ejava.examples.svc.content.quotes.api.QuotesApi.*;

import java.net.URI;

@RestController
@Slf4j
public class QuotesController {
    
    private final QuotesService quoteService;

    public QuotesController(QuotesService quoteService){
        this.quoteService = quoteService;
    }

    /*
     * This method provides two example method signatures. Use the @RequestBody from when
     * headers are of no interest. Use RequestEntity<QuoteDTO> from when headers are of
     * interest
     */

    @RequestMapping(path = QUOTES_PATH,
                    method = RequestMethod.POST,
                    consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},
                    produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE} )
    
    public ResponseEntity<QuoteDTO> createQuote(@RequestBody QuoteDTO quote) {
        // public ResponseEntity<QuoteDTO> createQuote(RequestEntity<QuoteDTO> request) {
        // QuoteDTO quote = request.getBody();
        // log.info("CONTENT_TYPE = {}", request.getHeaders().get(HttpHeaders.CONTENT_TYPE))
        // log.info("ACCEPT = {} ", request.getHeaders().get(HttpHeaders.ACCEPT))
        QuoteDTO result = quoteService.createQuote(quote);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                                        .replacePath(QUOTE_PATH).build(result.getId());
        
        ResponseEntity<QuoteDTO> response = ResponseEntity.created(uri).body(result);
        
        return response;

    }


    @RequestMapping(path = QUOTES_PATH,
                    method = RequestMethod.GET,
                    produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<QuoteListDTO> getQuotes(@RequestParam(name="offset",defaultValue = "0") int offset, 
                                                  @RequestParam(name="limit", defaultValue = "0") int limit  ) {

    
        QuoteListDTO quotes = quoteService.getQuotes(offset, limit);
        URI url = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        ResponseEntity<QuoteListDTO> response = ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION, url.toString()).body(quotes);
        return response;
    
    }

    @RequestMapping(path = QUOTE_PATH,
    method = RequestMethod.PUT,
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateQuote(@PathVariable("id") int id,@RequestBody QuoteDTO quote) {
        // public ResponseEntity<Void> updateQuote(@PathVariable("id") int id, RequestEntity<QuoteDTO> request){
        // QuoteDTO quote = request.getBody();
        // log.info("Content-Type - {} ", request.getHeaders().get(HttpHeaders.CONTENT_TYPE));
        // log.info("Accept - {} ", request.getHeaders().get(HttpHeaders.ACCEPT));
        
        quoteService.updateQuote(id, quote);
        ResponseEntity<Void> response = ResponseEntity.ok().build();
        return response;
    }


    @RequestMapping(path = QUOTE_PATH, method = RequestMethod.HEAD)
    public ResponseEntity<Void> containsQuote(@PathVariable("id") int id){
        boolean exists = quoteService.containsQuote(id);
        ResponseEntity<Void> response = exists ? ResponseEntity.ok().build(): ResponseEntity.notFound().build();
        return response;

    }


    @RequestMapping(path = QUOTE_PATH, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<QuoteDTO> getQuote(@PathVariable("id") int id){
        QuoteDTO quote = quoteService.getQuote(id);
        ResponseEntity<QuoteDTO> response = ResponseEntity.ok(quote);
        return response;

    }

    @RequestMapping(path = RANDOM_QUOTE_PATH, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<QuoteDTO> randomQuote() {
        QuoteDTO quote = quoteService.randomQuote();

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().replacePath(QUOTE_PATH).build(quote.getId());
        ResponseEntity<QuoteDTO> response = ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION, uri.toString()).body(quote);
        return response;
    }

    @RequestMapping(path = QUOTE_PATH, method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteQuote(@PathVariable("id") int id){
        quoteService.deleteQuote(id);
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path = QUOTES_PATH, method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllQuotes() {
        quoteService.deleteAllQuotes();
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

}
