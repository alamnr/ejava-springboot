package  info.ejava.examples.common.web;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.BDDAssertions.then;
import org.junit.jupiter.api.Test;


public class ServerConfigTest {

    @Test
    void can_format_default() throws URISyntaxException {

        // given 
        ServerConfig cfg = new ServerConfig();
        // when
        cfg = cfg.build();
        // then
        then(cfg.getBaseUrl()).isEqualTo(new URI("http://localhost:8080"));

    }

    @Test
    void can_format_port() throws URISyntaxException {

        // given
        ServerConfig cfg = new ServerConfig().withPort(1234);
        // when 
        cfg = cfg.build();
        // then 
        then(cfg.getPort()).isEqualTo(1234);
        then(cfg.getBaseUrl()).isEqualTo(new URI("http://localhost:1234"));
    }

    @Test
    void can_format_https_default() throws URISyntaxException {

        // given 
        ServerConfig cfg = new ServerConfig().withScheme("https").withHost("ahost");
        // when
        cfg = cfg.build();
        // then
        then(cfg.getBaseUrl()).isEqualTo(new URI("https://ahost:8443"));
    }
}
