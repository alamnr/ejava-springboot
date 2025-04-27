package info.ejava.examples.svc.rpc.greeter;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class ClientTestConfiguration {
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
        // or just following will work in the simple cases like this 
        // return new RestTemplate();
    }
    @Bean @Lazy
    public String baseUrl(@LocalServerPort int port){
        return  String.format("http://localhost:%d/rpc/greeter/sayHai", port);
    }
}
