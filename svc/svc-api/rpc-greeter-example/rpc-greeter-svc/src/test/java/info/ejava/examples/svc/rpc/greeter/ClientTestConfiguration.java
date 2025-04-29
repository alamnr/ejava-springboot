package info.ejava.examples.svc.rpc.greeter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties.Restclient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.NoOpResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
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

    
    @Bean @Lazy @Qualifier("restTemplateHttpIface")
    // This could be done with RestTemplate, RestClient or WebClient
    public GreeterApi greeterApiRestTemplate(String baseUrl){
        /*
         RestTemplate is the only client option that allows one to bypass the exception rule and obtain an
         error ResponseEntity from the call without exception handling. The following example shows a
         NoOpResponseErrorHandler error handler being put in place and the caller is receiving the error
         ResponseEntity without using exception handling.
         */
        //configure RestTemplate to return error responses, not exceptions

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(baseUrl).build();
        restTemplate.setErrorHandler(new NoOpResponseErrorHandler());

        RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GreeterApi.class);

    }
    @Bean @Lazy @Qualifier("restClientHttpIface")
    // This could be done with RestTemplate, RestClient or WebClient
    public GreeterApi greeterApiRestClient(RestClient.Builder builder,String baseUrl){
        RestClient restclient = builder.baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(restclient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GreeterApi.class);

    }

    @Bean @Lazy @Qualifier("webClientHttpIface")
    // This could be done with RestTemplate, RestClient or WebClient
    public GreeterApi greeterApiWebClient(WebClient.Builder builder,String baseUrl){
        WebClient webclient = builder.baseUrl(baseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webclient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GreeterApi.class);

    }


    @Bean @Lazy
    public String baseUrl(@LocalServerPort int port){
        return  String.format("http://localhost:%d/rpc/greeter", port);
    }


}
