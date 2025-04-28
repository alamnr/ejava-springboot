package info.ejava.examples.svc.rpc.greeter;

import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties.Restclient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@TestConfiguration
public class ClientTestConfiguration {
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
        // or just following will work in the simple cases like this 
        // return new RestTemplate();
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder, RestTemplate restTemplate){
        return builder.build();
         //return RestClient.create(restTemplate);
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
        // or just following will work in the simple cases like this
        // return WebClient.builder().build();
    }
    @Bean @Lazy
    // This could be done with RestTemplate, RestClient or WebClient
    public GreeterApi greeterApi(RestClient.Builder builder,String baseUrl){
        RestClient restclient = builder.baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(restclient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GreeterApi.class);

    }

    @Bean @Lazy
    public String baseUrl(@LocalServerPort int port){
        return  String.format("http://localhost:%d/rpc/greeter/sayHai", port);
    }


}
