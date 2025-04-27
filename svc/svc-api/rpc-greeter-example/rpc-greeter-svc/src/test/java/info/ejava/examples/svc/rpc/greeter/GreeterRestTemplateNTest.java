package info.ejava.examples.svc.rpc.greeter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URI;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

}
