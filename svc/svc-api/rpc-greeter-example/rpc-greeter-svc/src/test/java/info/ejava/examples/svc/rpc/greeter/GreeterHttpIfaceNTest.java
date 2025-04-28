package info.ejava.examples.svc.rpc.greeter;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties.Restclient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import info.ejava.examples.svc.rpc.GreeterApplication;
import lombok.extern.slf4j.Slf4j;

/*
 * This class provides a simple example using RestClient as a 
 * fluent Api replacement for RestTemplate
 */

@SpringBootTest(classes = {GreeterApplication.class,ClientTestConfiguration.class},
                    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("springboot") @Tag("greeter")
@Slf4j
public class GreeterHttpIfaceNTest {

    @LocalServerPort
    private int port; // injetcion of port way1
    private String baseUrl; // initialized in test setup
    private GreeterApi greeterApi; // initialized in test setup

    @Autowired @Qualifier("baseUrl") // @Qualifier makes bean selection mor explicit
    private String injectedBaseUrl; // injected from test configuration
    @Autowired
    private GreeterApi injectedGreeterApi;  // injected from test configuration

    @BeforeEach
    void init(@LocalServerPort int port) { // injection of port in way2
        baseUrl = String.format("http://localhost:%d/rpc/greeter/", port);
        RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        greeterApi = factory.createClient(GreeterApi.class);
    }

    @Test
    public void say_hi(){
        // given / arrange - 
        String url = String.format("http://localhost:%d/rpc/greeter/", port);
        //RestTemplate restTemplate = new RestTemplateBuilder().rootUri(url).build();
        //RestClient restClient = RestClient.builder().baseUrl(url).build();
        WebClient webClient = WebClient.builder().baseUrl(url).build();


        interface MyInnerGreeterApi {
            @GetExchange("sayHai")
            String sayHai();
        }

        //RestClientAdapter adapter = RestClientAdapter.create(restClient);
        //RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        MyInnerGreeterApi greeterApi = factory.createClient(MyInnerGreeterApi.class);

        // when / act 
        String resp = greeterApi.sayHai();
        
        // then / evaluate / assert
        BDDAssertions.then(resp).isEqualTo("hi");

    }   

    @Test
    public void say_greeting(){
        // given / arrange - 



        //  when / act
        ResponseEntity<String> resp = greeterApi.sayGreeting("Hello", "Jim");
        
        // then / assert

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        BDDAssertions.then(resp.getBody()).isEqualTo("Hello , Jim");
    }

    @Test
    void say_greeting_with_default_param(){
        // given / arrange

        // when / act

            ResponseEntity<String> resp = greeterApi.sayGreeting("Hello", null);
        // then / evaluate / assert

        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).contains("text/plain");
        BDDAssertions.then(resp.getBody()).isEqualTo("Hello , you");
    }

    @Test
    void no_boom(){
        // given // arrange 

        // when / act - calling the service with injected Http Interface
        ResponseEntity<String> resp = greeterApi.boom("whatever");

        // then / assert
        BDDAssertions.then(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        BDDAssertions.then(resp.getBody()).isEqualTo("worked ? ");
    }

    @Test
    void boom(){

        // given / arrange

        // when / act 
        RestClientResponseException ex = BDDAssertions.catchThrowableOfType(() -> greeterApi.boom(), HttpClientErrorException.class);

        // then  /  evaluate / assert
        BDDAssertions.then(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}