package info.ejava.examples.svc.content.quotes;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.svc.content.quotes.api.QuotesApi;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;
import lombok.extern.slf4j.Slf4j;

/*
 * This test was put in place with RestTemplate so that we could leverage the 
 * ability for restTemplate filters to log pay load bodies in debug mode
 */

 @SpringBootTest(classes = {ClientTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "test=true")
@ActiveProfiles("test")
@Tag("springboot")
@Slf4j
public class QuotesRestTemplateNTest {
    @Autowired
    private QuoteDTOFactory quotesFactory;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private URI baseUrl;
    @Autowired
    private URI quotesUrl;

    private static final MediaType[] MEDIA_TYPES = new MediaType[] {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML
    };

    @BeforeEach
    public void setUp() {
        log.info("clearing all gestures");
        restTemplate.delete(quotesUrl);
    }

    @AfterEach
    public void cleanUp() {
        //cut down on noise
        //restTemplate.delete(quotesUrl);
    }

    public static Stream<Arguments> mediaTypes() {
        List<Arguments> params = new ArrayList<>();
        for (MediaType contentType : MEDIA_TYPES) {
            for (MediaType acceptType : MEDIA_TYPES) {
                params.add(Arguments.of(contentType, acceptType));
            }
        }
        return params.stream();
    }
   
    @ParameterizedTest
    @MethodSource("mediaTypes")
    public void add_valid_quote_for_type(MediaType contentType, MediaType acceptType) {
        
        // given -a valid quote
        QuoteDTO validQuote = quotesFactory.make();
        log.info("Content-Type- {} , dto - {}, Accept - {}",contentType, validQuote, acceptType);
        
        // when - makin a request with different content and accept payload types 
        RequestEntity<QuoteDTO> request = RequestEntity.post(quotesUrl)
                                                        .contentType(contentType)
                                                        .accept(acceptType)
                                                        .body(validQuote);
        ResponseEntity<QuoteDTO> response = restTemplate.exchange(request, QuoteDTO.class);

        // then - the service will accept the format we supplied
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // and the content will be returned to us in the requested format
        BDDAssertions.then(response.getHeaders().getContentType()).isEqualTo(acceptType);

        // that equals what we sent plus an ID generated
        QuoteDTO createdQuote = response.getBody();
        BDDAssertions.then(createdQuote).isEqualTo(validQuote.withId(createdQuote.getId()));
        // with a Location response header referencing the URI of the created quote
        URI location = UriComponentsBuilder.fromUri(baseUrl).path(QuotesApi.QUOTE_PATH).build(createdQuote.getId());
        BDDAssertions.then(response.getHeaders().getLocation()).isEqualTo(location);
        //BDDAssertions.then(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isEqualTo(location);
    }
}
