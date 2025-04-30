package info.ejava.examples.svc.rpc.greeter;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Date;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.ejava.examples.svc.rpc.GreeterApplication;
import lombok.extern.slf4j.Slf4j;

/*
 * This class provides a simple example of using RestClient as a 
 * fluent API replacement for RestTemplate
 * 
 */
@SpringBootTest(classes = {GreeterApplication.class,ClientTestConfiguration.class},
                        webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("springboot") @Tag("greeter")
@Slf4j
public class GreeterRestClientNTest {

    @LocalServerPort 
    private int port; // injection option way1
    String baseUrl; // initialize in test setup
    private RestClient restClient; // initialize in setup

    @Autowired @Qualifier("baseUrl") // Qualifier makes bean selection more explicit
    private String injectedBaseUrl; // initialized in testConfig using way3
    @Autowired
    private RestClient injectedRestClient; // injected from Test config

    @BeforeEach
    void init(@LocalServerPort int port){
        baseUrl = String.format("http://localhost:%d/rpc/greeter", port);
        restClient= RestClient.builder().build();

    }

    @Test
    public void say_hi() {
        // given / arrange - a service available at a urland client access
        String url = String.format("http://localhost:%d/rpc/greeter/sayHai", port);
        RestClient restClient = RestClient.builder().build();

        // when / act - call the service
        String response = restClient.get().uri(url).retrieve().body(String.class);

        // then / assert - evaluate the result
        BDDAssertions.then(response).isEqualTo("hi");
    }

    @Test
    void say_greeting(){
        // given / arrange
            URI url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/say/{greeting}")
                                            .queryParam("name", "{name}")
                                            .build("hello", "jim");
                                            

        // when /act
        ResponseEntity<String> resp = injectedRestClient.get().uri(url).retrieve().toEntity(String.class);

        // then / assert-evaluate

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getBody()).contains("hello , jim");
    }

    @Test
    void no_boom() {
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl) // using injected base url
                        .path("/boom")
                        .queryParam("value", "whatever")
                        .build().toUri();
        // when / act
        ResponseEntity<String> resp = injectedRestClient.get().uri(url).retrieve().toEntity(String.class);

        // then / assert-evaluate

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH)).isEqualTo(9);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.DATE)).isEqualTo(new Date());
        BDDAssertions.then(resp.getBody()).isEqualTo("worked ? ");
        log.info("resp - {}", resp);
    }

    @Test
    void boom(){
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                    .path("/boom")
                                    .build().toUri();

        // when  / act
        RestClientResponseException ex = BDDAssertions.catchThrowableOfType(()->injectedRestClient.get().uri(url)
                                                            .retrieve().toEntity(String.class), HttpClientErrorException.class);

        // then / assert-evaluate
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        try {
            ErrorMessage err = new ObjectMapper().readValue(ex.getResponseBodyAsString(), ErrorMessage.class);
            log.info("Error message - {}",err);
        } catch (JsonMappingException e) {
            log.error("Error - ", e);
        } catch (JsonProcessingException e) {
            log.error("Error - ", e);
        }
    }

    @Test
    void boom_return_response_not_exception() {
        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                .replacePath("/rpc/greeter/boom")
                                .build().toUri();


        // when / act

        Assertions.assertDoesNotThrow(() -> {

            ResponseEntity<?> response = injectedRestClient.get().uri(url)
                                        .exchange((req,res) -> {
                                            return ResponseEntity.status(res.getStatusCode())
                                                                    .headers(res.getHeaders())
                                                                    .body(StreamUtils.copyToString(res.getBody(), Charset.defaultCharset()));
                                        });
            // then / assert - evaluate

            BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            BDDAssertions.then(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
            ErrorMessage err = new ObjectMapper().readValue(response.getBody().toString(), ErrorMessage.class);
            log.info("error msg - {}", err);
        });

        
    }

    @Test
    void  boy() {
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boys")
                                        .queryParam("name", "jim")
                                        .build().toUri();
        // when / act
        ResponseEntity<String> resp = injectedRestClient.get()
                                            .uri(url)
                                            .retrieve()
                                            .toEntity(String.class);

        // then / evaluate-assert

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getBody().contains("hello jim , "));
    }

    @Test
    void boy_blue(){
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boys")
                                        .queryParam("name", "blue")
                                        .build().toUri();

        // when / act

        RestClientResponseException ex = BDDAssertions.catchThrowableOfType(() -> 
                                        injectedRestClient.get().uri(url).retrieve().toEntity(String.class), 
                                        HttpClientErrorException.class);

        // then / evaluate-assert
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith(MediaType.TEXT_PLAIN_VALUE);
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
        BDDAssertions.then(ex.getResponseBodyAsString()).isEqualTo("boy named blue");
    }

    @Test
    void boy_blue_with_exception(){
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boys/throws")
                                        .queryParam("name", "blue")
                                        .build().toUri();
        // when / act
        RestClientResponseException ex = BDDAssertions.catchThrowableOfType(() -> 
                                        injectedRestClient.get().uri(url).retrieve().toEntity(String.class), 
                                        HttpClientErrorException.class)   ;

        // then / evaluate-assert

        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
        BDDAssertions.then(ex.getResponseBodyAsString()).isEqualTo("boy named blue");
    }
    
}
