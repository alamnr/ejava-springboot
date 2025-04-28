package info.ejava.examples.svc.rpc.greeter;

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
import org.springframework.web.client.RestClient;

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
    
}
