package info.ejava.examples.common.web;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestTemplateConfig {
    public RestTemplate restTemplateDebug(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory,
                                          ClientHttpRequestInterceptor...  otherInterceptors) {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new RestTemplateLoggingFilter());
        if (null!=otherInterceptors) {
            interceptors.addAll(Arrays.asList(otherInterceptors));
        }

        RestTemplate restTemplate = builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(interceptors)
                .build();
        return restTemplate;
    }

    public RestTemplate restTemplateDebug(RestTemplateBuilder builder,
                                          ClientHttpRequestInterceptor...  otherInterceptors) {
        return restTemplateDebug(builder, new SimpleClientHttpRequestFactory(), otherInterceptors);
    }
}
