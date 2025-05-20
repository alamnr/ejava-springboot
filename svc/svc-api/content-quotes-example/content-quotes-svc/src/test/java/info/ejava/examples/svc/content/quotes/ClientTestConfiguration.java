package info.ejava.examples.svc.content.quotes;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;

import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.svc.content.quotes.api.QuotesApi;
import info.ejava.examples.svc.content.quotes.client.QuotesApiWebClient;
import info.ejava.examples.svc.content.quotes.client.ServerConfig;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;

/*
 * A test configuration used by remote test client
 */
@TestConfiguration
public class ClientTestConfiguration {

    @Bean
    public WebClient webclient(WebClient.Builder builder) {
        return builder.filter(WebClientLoggingFilter.requestFilter())
                        .filter(WebClientLoggingFilter.responseFilter())
                        .build();
    }

    @Bean
    ClientHttpRequestFactory requestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory){
        RestTemplate restTemplate = builder.requestFactory(
                // used to read the Stream twice -- so we can use the logging filter below
                () -> new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(List.of(new RestTemplateLoggingFilter()))
                .build();
        return restTemplate;
    }

    @Bean
    public QuoteDTOFactory quoteDTOFactory() {
        return new QuoteDTOFactory();
    }

    @Bean @Lazy
    public ServerConfig serverConfig(@LocalServerPort int port){
        return new ServerConfig().withPort(port).build();
    }

    @Bean @Lazy
    public URI baseUrl(ServerConfig serverConfig) {
        return serverConfig.getBaseUrl();
    }

    @Bean @Lazy
    public URI quotesUrl(URI baseUri) {
        return UriComponentsBuilder.fromUri(baseUri).path(QuotesApi.QUOTES_PATH).build().toUri();
    }

    @Bean @Lazy
    @Qualifier("webClient")
    public QuotesApiWebClient quotesWebClient(WebClient webClient, ServerConfig cfg) {
        return new QuotesApiWebClient(webClient, cfg);
    }

    @Bean @Lazy
    public QuotesApiWebClient quotesClient(WebClient webClient, ServerConfig serverConfig) {
        return new QuotesApiWebClient(webClient, serverConfig, MediaType.APPLICATION_JSON_VALUE);
    }



}
