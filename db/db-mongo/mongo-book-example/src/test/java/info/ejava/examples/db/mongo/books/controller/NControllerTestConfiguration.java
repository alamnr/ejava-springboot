package info.ejava.examples.db.mongo.books.controller;

import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

@TestConfiguration
public class NControllerTestConfiguration {

    @Bean
    @Lazy
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
                }).build();
    }
}
