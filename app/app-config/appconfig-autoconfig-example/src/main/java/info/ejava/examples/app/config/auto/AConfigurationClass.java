package info.ejava.examples.app.config.auto;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import info.ejava.examples.app.hello.Hello;
import info.ejava.examples.app.hello.stdout.StdOutHello;

@Configuration(proxyBeanMethods = false)
public class AConfigurationClass {

    
    @Bean
    @ConditionalOnProperty(prefix = "hello", name = "quiet", havingValue = "true")
    @Primary
    
    public Hello quiteHello() {
        return new StdOutHello("hello.quiet property condition set, Application @Bean says hi");
    }

    /*
    @Bean
    @ConditionalOnMissingBean 
    public Hello hello(){
        return new StdOutHello("... ");
    }
     */
    
    
}
