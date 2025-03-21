package info.ejava.examples.svc.content.quotes.client;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {

    private String scheme;
    private String host;
    private int port;
    private URI baseUrl;

    @PostConstruct
    public ServerConfig build(){
        if(null == baseUrl){
            scheme = scheme == null ? "http" : scheme.toLowerCase();
            host = host == null ? "localhost" : host ;
            if(port == 0 ) {
                port = scheme.equals("http") ? 8080 : 8787;
            }
            baseUrl = UriComponentsBuilder.newInstance()
                        .scheme(scheme)
                        .host(host)
                        .port(port)
                        .build((Object)null);
        }
        scheme = baseUrl.getScheme();
        host = baseUrl.getHost();
        port = baseUrl.getPort();
        return this;
    }



}
