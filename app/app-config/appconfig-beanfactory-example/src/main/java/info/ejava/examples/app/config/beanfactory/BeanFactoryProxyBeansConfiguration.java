package info.ejava.examples.app.config.beanfactory;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

//@Configuration
//@Configuration(proxyBeanMethods = true)
@Configuration(proxyBeanMethods = false)
public class BeanFactoryProxyBeansConfiguration {
    public static class Example {
        private final int exampleValue;
        public Example(int value) { this.exampleValue = value; }
        @PostConstruct //will be called for POJOs made into Components
        void init() {
            System.out.println("@PostConstruct called for: " + exampleValue);
        }
        public String toString() { return Integer.toString(exampleValue); }
    }
    private int value = 0;

    @Bean
    //@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) //"singleton" - default
    //@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) //"prototype"
    Example bean() {
        return new Example(value++);
    }

    @Bean
    String calling1() {
        return "calling1=" + bean();
    }
    @Bean
    String calling2() {
        return "calling2=" + bean();
    }
    @Bean
    String injected1(Example bean) {
        return "injected1=" + bean;
    }
    @Bean
    String injected2(Example bean) {
        return "injected2=" + bean;
    }

    @Bean
    String beanFactories(List<String> results) {
        results.forEach(System.out::println);
        return results.toString();
    }
}
