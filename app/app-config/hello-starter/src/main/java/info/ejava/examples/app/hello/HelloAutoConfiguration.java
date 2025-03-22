package info.ejava.examples.app.hello;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import info.ejava.examples.app.hello.stdout.StdOutHello;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(StdOutHello.class)
@EnableConfigurationProperties(HelloProperties.class)
public class HelloAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean // added later in example to resolve conflict
    public Hello hello(HelloProperties helloProperties) {
        return new StdOutHello(helloProperties.getGreeting());
    }

}
