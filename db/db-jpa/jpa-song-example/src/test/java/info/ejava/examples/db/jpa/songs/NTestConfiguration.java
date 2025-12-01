package info.ejava.examples.db.jpa.songs;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import reactor.netty.http.client.HttpClient;

@TestConfiguration
public class NTestConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public SongDTOFactory dtoFactory(){
        return new SongDTOFactory();
    }

     @Bean
    public WebClient webClient(WebClient.Builder builder) {
        //lengthy timeout used for stepping thru debugger
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofHours(1));
        return builder
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .exchangeStrategies(ExchangeStrategies.builder().codecs(conf->{
                    conf.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    conf.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                }).build())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public ClientHttpRequestFactory requestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
                ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new RestTemplateLoggingFilter())
                .build();
    }


    @Bean @Lazy
    public ServerConfig serverConfig(@LocalServerPort int port) {
        return new ServerConfig().withPort(port).build();
    }

    @Bean @Lazy
    public WebTestClient webTestClient(ServerConfig serverConfig) {
        return WebTestClient.bindToServer()
                .baseUrl(serverConfig.getBaseUrl().toString())
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .codecs(conf->{
                    conf.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    conf.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                })
                .responseTimeout(Duration.ofHours(1))
                .build();
    }


}
