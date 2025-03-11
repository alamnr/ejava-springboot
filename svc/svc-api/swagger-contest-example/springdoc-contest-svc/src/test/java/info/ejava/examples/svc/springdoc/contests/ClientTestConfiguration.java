package info.ejava.examples.svc.springdoc.contests;

import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.svc.springdoc.contest.api.ContestApi;
import info.ejava.examples.svc.springdoc.contest.client.ContestsWebClientImpl;
import info.ejava.examples.svc.springdoc.contest.dto.ContestDTOFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@TestConfiguration
public class ClientTestConfiguration {
    @Bean
    @ConfigurationProperties("it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .build();
    }

    @Bean
    public ContestDTOFactory quotesDtoFactory() {
        return new ContestDTOFactory();
    }

    @Bean @Lazy
    public ServerConfig serverConfig(@LocalServerPort int port) {
         return new ServerConfig().withPort(port).build();
    }

    @Bean @Lazy
    public URI baseUrl(ServerConfig serverConfig) {
        return serverConfig.getBaseUrl();
    }

    @Bean @Lazy
    public ContestApi contestsClient(WebClient webClient, ServerConfig serverConfig) {
        return new ContestsWebClientImpl(webClient, serverConfig, MediaType.APPLICATION_JSON_VALUE);
    }
}
