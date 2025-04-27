package info.ejava.examples.svc.rpc;

import java.util.Arrays;
import java.util.List;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.Filter;

@SpringBootApplication
public class GreeterApplication {
    public static void main(String... args){
        SpringApplication.run(GreeterApplication.class,args);
    }

    @Bean
    public Filter logFilter() {
        final List<String> headers = Arrays.asList("accept,host,content-length,Content-Type,accept-encoding");
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(false);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(1000);
        filter.setBeforeMessagePrefix(System.lineSeparator());
        filter.setAfterMessagePrefix(System.lineSeparator());
        filter.setIncludeHeaders(true);
        filter.setHeaderPredicate(h -> h.contains(h));
        return filter;

    }
}
