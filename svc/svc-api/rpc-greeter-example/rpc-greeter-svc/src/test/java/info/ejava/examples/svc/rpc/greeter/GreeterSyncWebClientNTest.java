package info.ejava.examples.svc.rpc.greeter;

import java.net.URI;

import org.assertj.core.api.BDDAssertions;
import org.assertj.core.util.DateUtil;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.ejava.examples.svc.rpc.GreeterApplication;
import lombok.extern.slf4j.Slf4j;

/*
 * This class provides a simple example of using WebClient as a 
 * fluent API, synchronous replacement of RestTemplate 
 */

 @SpringBootTest(classes = {GreeterApplication.class,ClientTestConfiguration.class},
                    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("springboot") @Tag("greeter")
@Slf4j
public class GreeterSyncWebClientNTest {
    
    @LocalServerPort 
    private int port; // initialization of way1
    private String baseUrl; // initialize in test setup
    private WebClient webClient; // initialize in setup

    @Autowired @Qualifier("baseUrl") // @Qualifier makes bean selection more explicit
    private String injectedBaseUrl; // Initialize in test config using way3
    @Autowired
    private WebClient injectedWebClient; // Injected from test configuration

    @BeforeEach
    void init(@LocalServerPort int port){ // injection ortion way2
        baseUrl = String.format("http://localhost:%d/rpc/greeter", port);
        webClient = WebClient.builder().build();
    }

    @Test
    void say_hi(){
        // given / arrange - an endpoint url to access the service
        String endPointUrl = String.format("http://localhost:%d/rpc/greeter/sayHai", port);
        WebClient webClient = WebClient.builder().build();

        // when / act -  call the service
        String response = webClient.get().uri(endPointUrl).retrieve().bodyToMono(String.class).block();
        // block() causes the reactive flow definition to begin producing data

        // then / evaluate 
        BDDAssertions.then(response).isEqualTo("hi");
    }

    @Test
    void say_greeting(){
        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/say/{greeting}")
                                        .queryParam("name", "{name}")
                                        .build("hello", "jim");
        // when / act
        ResponseEntity<String> resp = injectedWebClient.get().uri(url).retrieve().toEntity(String.class).block();
        // then / evaluate-assert
        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH)).isEqualTo("11");
        BDDAssertions.then(resp.getBody()).isEqualTo("hello , jim");

    }

    @Test
    void no_boom(){
        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .replacePath("/rpc/greeter/boom")
                                        .queryParam("value", "whatever")
                                        .build().toUri();
        // when / act
        ResponseEntity<String> resp = injectedWebClient.get().uri(url).retrieve().toEntity(String.class).block();
        // then / evaluate-assert
        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getBody()).isEqualTo("worked ? ");
    }

    @Test
    void boom(){
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .replacePath("/rpc/greeter/boom")
                                        .build().toUri();
        // when / act
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(() -> 
                            injectedWebClient.get().uri(url).retrieve().toEntity(String.class).block(),WebClientResponseException.class);

        // then / evaluate-assert

        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        BDDAssertions.then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        try {
            ErrorMessage err = new ObjectMapper().readValue(ex.getResponseBodyAsString(), ErrorMessage.class);
            log.info("Parsed error message - {}", err);
        } catch (JsonProcessingException e) {
            log.error("Error message - ", e);
        }
    }

    @Test
    void boy() {
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boys").queryParam("name", "jim").build().toUri();

        // when / act

        ResponseEntity<String> resp = injectedWebClient.get().uri(url).retrieve().toEntity(String.class).block();


        // then // evaluate-assert
        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isEqualTo(url.toString());
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH)).isGreaterThan("0");
        BDDAssertions.then(resp.getBody()).isEqualTo("Hello jim , how do you do ?");
    }

    @Test
    void boy_blue(){

        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl).path("/boys").queryParam("name", "blue").build().toUri();

        // when / act 
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(()->
                                    injectedWebClient.get().uri(url).retrieve().toEntity(String.class).block(), WebClientResponseException.class);

        // then / evaluate-assert
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        BDDAssertions.then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
        BDDAssertions.then(ex.getResponseBodyAsString()).isEqualTo("boy named blue");
    }

    @Test
    void boy_blue_with_exception_handler() {
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl).path("/boys/throws").queryParam("name", "blue").build().toUri();
        // when / act
        WebClientResponseException ex = BDDAssertions.catchThrowableOfType(()->
                                            injectedWebClient.get().uri(url).retrieve().toEntity(String.class).block(),
                                            WebClientResponseException.class);

        // then / evaluate-assert
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        BDDAssertions.then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
        BDDAssertions.then(ex.getResponseBodyAsString()).isEqualTo("boy named blue");
    }

}
