package info.ejava.examples.svc.rpc.greeter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URI;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.NoOpResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.ejava.examples.svc.rpc.GreeterApplication;
import lombok.extern.slf4j.Slf4j;
/*
 * This class provides a simple example of synchronous RestTemplate client
 */

@SpringBootTest(classes = {GreeterApplication.class,ClientTestConfiguration.class},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("springboot") @Tag("greeter")
@Slf4j
public class GreeterRestTemplateNTest {

    @LocalServerPort // @Value("${local.server.port}")
    /*
     * @Value("${local.server.port}")
     * public @interfac LocalServerPort{}
     */
    private int port;
    // We use this instance / attribute variable  of the test class since other @Test method can use the port value when required
    // anothe way of injection is to inject it into Test class life cycle method so that @Test method do not use the raw port value 
    // but may use something that is  built from the value

    private String baseUrl; // initialize in test setup 
    private RestTemplate restTemplate; // initialize in test setup
    @BeforeEach
    void init(@LocalServerPort int port){
        baseUrl = String.format("http://localhost:%d/rpc/greeter/sayHai",port);
        restTemplate = new RestTemplate();
    }


    @Autowired @Qualifier("baseUrl") // Qualifier makes bean selection more explicit
    private String injectedBaseUrl;

    @Autowired
    private RestTemplate injectedRestTemplate; // injected from TestConfig



    @Test
    public void say_hi(){

            // given / arrange - a service available at a url and client access
            String url = String.format("http://localhost:%d/rpc/greeter/sayHai",port);
            // more type safe, purpose designed way to build the url
            URI url_1 =  UriComponentsBuilder.fromUriString("http://localhost")
                            .port(port)
                            .path("rpc/greeter/sayHai")
                            .build().toUri();

            RestTemplate restTemplate = new RestTemplate(); // simple, manual instantiation
            // when / act 
            String greeting = restTemplate.getForObject(url, String.class);

            // then / evaluate
            log.info("Response - {}",greeting);      
            assertEquals("hi", greeting);
            BDDAssertions.then(greeting).isEqualTo("hi");
            
    }

    @Test
    void say_greeting(){
        // given / arrange
        URI url =  UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/say/{greeting}")
                                        .queryParam("name", "{name}")
                                        .build("hello", "jim");

        // when / act
        ResponseEntity<String> resp = injectedRestTemplate.getForEntity(url, String.class);

        // then / evaluate-assert
        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getBody()).isEqualTo("hello , jim");
    }

    @Test
    void no_boom(){
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boom")
                                        .queryParam("value", "whatever")
                                        .build().toUri();

        // when / act

        ResponseEntity<String>  resp = injectedRestTemplate.getForEntity(url, String.class);

        // then / evaluate-assert

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getBody()).isEqualTo("worked ? ");
    }

    @Test
    void boom() {
        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boom")
                                        .build().toUri();
        // when / act
        HttpClientErrorException ex = BDDAssertions.catchThrowableOfType(() -> injectedRestTemplate.getForEntity(url, String.class),
                                                                HttpClientErrorException.class);
        
        // then / evaluate - assert
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        try {
            ErrorMessage err = new ObjectMapper().readValue(ex.getResponseBodyAsString(), ErrorMessage.class);
        } catch (JsonMappingException e) {
            log.error("Error - ", e);
        } catch (JsonProcessingException e) {
            log.error("Error - ", e);
        }
    }

    @Test
    void boom_return_response_not_exception(){
        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .replacePath("rpc/greeter/boom")
                                        .build().toUri();

        // configure Resttemplate to return error response , not exception
         /*
         RestTemplate is the only client option that allows one to bypass the exception rule and obtain an
         error ResponseEntity from the call without exception handling. The following example shows a
         NoOpResponseErrorHandler error handler being put in place and the caller is receiving the error
         ResponseEntity without using exception handling.
         */
        RestTemplate noExceptionRestTemplate = new RestTemplate();
        noExceptionRestTemplate.setErrorHandler(new NoOpResponseErrorHandler());
        // when / act 
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<String> response = noExceptionRestTemplate.getForEntity(url, String.class);
        // then / evaluate - assert
        BDDAssertions.then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        BDDAssertions.then(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        ErrorMessage err = new ObjectMapper().readValue(response.getBody(), ErrorMessage.class);
        }, "return response not exception");


    }

    @Test
    void boy(){
        // given / arrange

        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boys")
                                        .queryParam("name", "jim")
                                         .build().toUri();
                        

        // when / act
        ResponseEntity<String> resp = injectedRestTemplate.getForEntity(url,String.class);
        // then / evaluate-assert

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isEqualTo(url.toString());
        BDDAssertions.then(resp.getBody()).isEqualTo("Hello jim , how do you do ?");

    }

    @Test
    void boy_blue(){
        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boys")
                                        .queryParam("name", "blue")
                                        .build().toUri();

        // when / act

        HttpClientErrorException ex = BDDAssertions.catchThrowableOfType(() -> 
                injectedRestTemplate.getForEntity(url, String.class), HttpClientErrorException.class);

        // then / evaluate - assert

        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
        BDDAssertions.then(ex.getResponseBodyAsString()).isEqualTo("boy named blue");
    }

    @Test
    void boy_blue_with_exception_handler(){
        // given / arrange
        URI url = UriComponentsBuilder.fromHttpUrl(injectedBaseUrl)
                                        .path("/boys")
                                        .queryParam("name", "blue").build().toUri();
        // when / act
        HttpClientErrorException ex = BDDAssertions.catchThrowableOfType(()-> injectedRestTemplate.getForEntity(url, String.class),
                                            HttpClientErrorException.UnprocessableEntity.class);
        // then / evaluate-assert
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        BDDAssertions.then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
        BDDAssertions.then(ex.getResponseBodyAsString()).isEqualTo("boy named blue");

    }

}
