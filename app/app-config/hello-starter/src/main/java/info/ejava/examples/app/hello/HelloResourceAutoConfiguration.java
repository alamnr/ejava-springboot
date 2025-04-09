package info.ejava.examples.app.hello;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import info.ejava.examples.app.hello.stdout.StdOutHello;

@AutoConfiguration(before = HelloAutoConfiguration.class) //since boot 2.7.0
//==> wraps @Configuration + @AutoConfigurationBefore/After
//@Configuration(proxyBeanMethods = false)
//@AutoConfigureBefore(HelloAutoConfiguration.class)
//@ConditionalOnClass(StdOutHello.class)
@ConditionalOnResource(resources = "file:./hello.properties")
public class HelloResourceAutoConfiguration {
    @Bean
    //@Primary
    public Hello resourceHello() {
        return new StdOutHello("hello.properties exists says hello");
    }

}
