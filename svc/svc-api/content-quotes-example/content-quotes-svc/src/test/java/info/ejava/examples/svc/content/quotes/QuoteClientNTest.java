package info.ejava.examples.svc.content.quotes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClientResponseException.UnprocessableEntity;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.MessageDTO;
import info.ejava.examples.svc.content.quotes.api.QuotesApi;
import info.ejava.examples.svc.content.quotes.client.QuotesApiClient;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = {ClientTestConfiguration.class},
                    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                    properties = "test=true")
@ActiveProfiles("test")
@Tag("springboot")
@Slf4j
public class QuoteClientNTest {

    @Autowired
    private QuoteDTOFactory quoteFactory;

    @Autowired
    private QuotesApiClient quotesClient;

    @Autowired
    private URI baseUrl;

    @BeforeEach
    public void setUp() {
        log.info("clearing all quotes");
        quotesClient.deleteAllQuotes().block();
    }

    @AfterEach
    public void cleanUp() {
        // cut down on logging noise
        // quotesClient.deleteAllQuotes()
        int i=0;
    }

    public MessageDTO getErrorResponse (WebClientResponseException ex) {
        final String contentTypeValue = ex.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        final MediaType contentType = MediaType.valueOf(contentTypeValue);
        final byte[] bytes = ex.getResponseBodyAsByteArray();
        if (MediaType.APPLICATION_JSON.equals(contentType)) {
            return JsonUtil.instance().unmarshal(bytes, MessageDTO.class);
        } else if (MediaType.APPLICATION_XML.equals(contentType)) {
            return JsonUtil.instance().unmarshal(bytes, MessageDTO.class);
        } else {
            throw new IllegalArgumentException("unknown contentType: " + contentTypeValue);
        }
    }

    @Test
    public void add_valid_quote() {
        // given - a valid quote
        QuoteDTO validQuote = quoteFactory.make();

        // when - added  to the service  API
        Mono<ResponseEntity<QuoteDTO>> request  = quotesClient.createQuote(validQuote);

        // then - a resource was created
        ResponseEntity<QuoteDTO> response = request.block();
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // that - equals what we sent plus an ID generated
        QuoteDTO createdQuote = response.getBody();
        BDDAssertions.then(createdQuote).isEqualTo(validQuote.withId(createdQuote.getId()));
        // with a location response header referencing the url for the created resource
        URI location = UriComponentsBuilder.fromUri(baseUrl).path(QuotesApi.QUOTE_PATH).build(createdQuote.getId());
        BDDAssertions.then(response.getHeaders().getLocation()).isEqualTo(location);

    }

    private QuoteDTO given_an_existing_quote() {
        QuoteDTO existingQuote = quoteFactory.make();
        ResponseEntity<QuoteDTO> quoteResponse = quotesClient.createQuote(existingQuote).block();
        BDDAssertions.assertThat(quoteResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return quoteResponse.getBody();
    }

    @Test
    public void update_existing_quote() {
        // given - an existing/original quote
        QuoteDTO existingQuote = given_an_existing_quote();
        int requestId = existingQuote.getId();

        // and an update
        QuoteDTO updateQuote = existingQuote.withText(existingQuote.getText()+" Updated");

        // when - updating existing quote
        ResponseEntity<Void> response = quotesClient.updateQuote(requestId, updateQuote).block();

        // then no exception was thrown and ...
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<QuoteDTO> quoteResponse = quotesClient.getQuote(requestId).block();
        BDDAssertions.then(quoteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(quoteResponse.getBody()).isEqualTo(updateQuote);
        BDDAssertions.then(quoteResponse.getBody()).isNotEqualTo(existingQuote);
    }

    @Test
    public void get_quote() {
        // given - an existing/original quote
        QuoteDTO existingQuoteDTO = given_an_existing_quote(); // hold onto the client side object
        ResponseEntity<QuoteDTO> quoteResponse = quotesClient.createQuote(existingQuoteDTO).block();
        BDDAssertions.assertThat(quoteResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        int requestId = quoteResponse.getBody().getId();

        // when - requesting quote by id
        ResponseEntity<QuoteDTO> response = quotesClient.getQuote(requestId).block();

        // then 
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(response.getBody()).isEqualTo(existingQuoteDTO.withId(requestId));

    }

    protected List<QuoteDTO> given_many_quotes(int count) {
        List<QuoteDTO> quotes = new ArrayList<>(count);
        for(QuoteDTO quote: quoteFactory.listBuilder().quotes(count, count))
        {
            ResponseEntity<QuoteDTO> response = quotesClient.createQuote(quote).block();
            BDDAssertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            quotes.add(response.getBody());
        }

        //        List<QuoteDTO> quotes = Flux.fromIterable(quotesFactory.listBuilder()
        //            //create some quote POJOs
        //         .quotes(count, count))
        //             //define an async request and subscribe to returned Mono
        //         .flatMap(quote -> quotesClient.createQuote(quote) )
        //             //flatMap waited for createQuote Mono to complete
        //             //verify status code returned and return body/quote with ID
        //         .map(response -> { //i.e., do work on completed result
        //             log.info("CREATE COMPLETE for quote {}", response.getBody().getId());
        //             assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        //             return response.getBody();
        //         })
        //         .collect(Collectors.toList())
        //         .block();

        return quotes;
    }

    @Test
    public void get_random_quote() {
        // given - many quotes
        given_many_quotes(5);
        log.info("Now have quotes : {}");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(baseUrl).replacePath(QuotesApi.QUOTE_PATH);

        // when - get random quote
        ResponseEntity<QuoteDTO> response = quotesClient.randomeQuote().block();

        // then
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        QuoteDTO returnedQuote = response.getBody();
        String expectedLocation = uriBuilder.build(returnedQuote.getId()).toString();
        String contentLocation = response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION);
        BDDAssertions.then(contentLocation).isEqualTo(expectedLocation);
        log.info("random quote : {}", returnedQuote);

    }

    @Test
    public void remove_quote() {
        // given 
        List<QuoteDTO> quotes = given_many_quotes(5);
        int requestId = quotes.get(1).getId();
        BDDAssertions.assertThat(quotesClient.getQuote(requestId).block().getStatusCode()).isEqualTo(HttpStatus.OK);

        // when - remove selected quote
        ResponseEntity<Void> response = quotesClient.deleteQuote(requestId).block();

        // then
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
         WebClientResponseException ex = BDDAssertions.catchThrowableOfType(() -> quotesClient.getQuote(requestId).block(), 
                                                            WebClientResponseException.NotFound.class);
        BDDAssertions.assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void remove_all_quotes() {
        // given
        List<QuoteDTO> quotes = given_many_quotes(5);

        // when - requested to remove all quotes
        ResponseEntity<Void> response = quotesClient.deleteAllQuotes().block();
        // then map should be cleared
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        for (QuoteDTO quote: quotes) {
            WebClientResponseException ex = BDDAssertions.catchThrowableOfType(
                    ()->quotesClient.getQuote(quote.getId()).block(),
                    WebClientResponseException.NotFound.class);
            BDDAssertions.assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

    
    }

    @Test
    public void remove_unknown_quote() {
        //given
        int requestId = 13;

        //when - requested to remove, will not report that does not exist, because delete method is idempotent
        ResponseEntity<Void> response = quotesClient.deleteQuote(requestId).block();

        //then
        log.info("reponse : {}", response);
        BDDAssertions.then(response.getStatusCode().is2xxSuccessful()).isTrue();
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void get_random_quote_no_quotes() {
        //given - no quotes
        BDDAssertions.assertThat((quotesClient.deleteAllQuotes()).block().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        //then
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(
                ()->quotesClient.randomeQuote().block(),
                WebClientResponseException.NotFound.class);

        //then
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        MessageDTO errMsg = getErrorResponse(ex);
        BDDAssertions.then(errMsg.getMessage()).contains("no quotes");
        log.debug("random quote:{}", errMsg);
    }

    @Test
    public void get_unknown_quote() {
        //given - no quotes and an unknown quoteId
        int unknownId=13;

        //when - requesting quote by id
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(
                ()->quotesClient.getQuote(unknownId).block(),
                WebClientResponseException.NotFound.class);

        //then
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        MessageDTO errMsg = getErrorResponse(ex);
        BDDAssertions.then(errMsg.getMessage()).contains(String.format("quote[%s]", unknownId));
    }

    @Test
    public void update_unknown_quote() {
        //given - an existing quote
        int unknownId=13;
        QuoteDTO updatedQuote = quoteFactory.make();

        //verify - that updating existing quote
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(
                ()->quotesClient.updateQuote(unknownId, updatedQuote).block(),
                WebClientResponseException.NotFound.class);

        //then - exception was thrown and ...
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        MessageDTO errMsg = getErrorResponse(ex);
        BDDAssertions.then(errMsg.getMessage()).contains(String.format("quote[%d]", unknownId));
    }

    @Test
    public void update_known_quote_with_bad_quote() {
        //given - an existing quote
        int knownId = 22;
        QuoteDTO badQuoteMissingText = new QuoteDTO();

        //verify - that when updating quote
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(
                ()->quotesClient.updateQuote(knownId, badQuoteMissingText).block(),
                WebClientResponseException.UnprocessableEntity.class);

        //then - exection was thrown and ...
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        BDDAssertions.then(getErrorResponse(ex).getMessage()).contains(String.format("missing required text", knownId));
    }

    @Test
    public void add_bad_quote_rejected() {
        //given
        QuoteDTO badQuoteMissingText = new QuoteDTO();

        //verify
        UnprocessableEntity ex = BDDAssertions.catchThrowableOfType(
                ()->quotesClient.createQuote(badQuoteMissingText).block(),
                UnprocessableEntity.class);

        //then - a resource was created
        MessageDTO errMsg = getErrorResponse(ex);
        BDDAssertions.then(errMsg.getMessage()).contains("missing required text");
        log.info("{}", errMsg);
    }

    @ParameterizedTest
    @CsvSource({"-1,null", "null,-5"})
    public void get_invalid_offset_limit(
            @ConvertWith(IntegerConverter.class)Integer offset,
            @ConvertWith(IntegerConverter.class)Integer limit) {
        //when - requesting invalid offsets
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(
                ()->quotesClient.getQuotes(offset, limit).block(),
                WebClientResponseException.BadRequest.class);

        //then - error was reported
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    public static class IntegerConverter implements ArgumentConverter {
        @Override
        public Object convert(Object o, ParameterContext parameterContext) throws ArgumentConversionException {
            return o.equals("null") ? null : Integer.parseInt((String)o);
        }
    }


    @Test
    public void get_empty_quotes() {
        //given we have no quotes

        //when - asked for amounts we do not have
        ResponseEntity<QuoteListDTO> response = quotesClient.getQuotes(0, 100).block();
        log.debug("{}", response);

        //then - the response will be empty
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        QuoteListDTO returnedQuotes = response.getBody();
        BDDAssertions.then(returnedQuotes.getCount()).isEqualTo(0);
        //and descriptive attributes filed in
        BDDAssertions.then(returnedQuotes.getOffset()).isEqualTo(0);
        BDDAssertions.then(returnedQuotes.getLimit()).isEqualTo(100);
        BDDAssertions.then(returnedQuotes.getTotal()).isEqualTo(0);
    }

    @Test
    public void get_many_quotes() {
        //given many quotes
        given_many_quotes(100);

        //when asking for a page of quotes
        ResponseEntity<QuoteListDTO> response = quotesClient.getQuotes(10, 10).block();

        //then - page of results returned
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        QuoteListDTO returnedQuotes = response.getBody();
        log.debug("{}", returnedQuotes);
        BDDAssertions.then(returnedQuotes.getCount()).isEqualTo(10);
        QuoteDTO quote0 = returnedQuotes.getQuotes().get(0);
        BDDAssertions.then(quote0.getId()).isGreaterThan(1);
        BDDAssertions.then(returnedQuotes.getQuotes().get(9).getId()).isEqualTo(quote0.getId()+9);

        //and descriptive attributes filed in
        BDDAssertions.then(returnedQuotes.getOffset()).isEqualTo(10);
        BDDAssertions.then(returnedQuotes.getLimit()).isEqualTo(10);
        BDDAssertions.then(returnedQuotes.getTotal()).isEqualTo(100);
    }



    
}
