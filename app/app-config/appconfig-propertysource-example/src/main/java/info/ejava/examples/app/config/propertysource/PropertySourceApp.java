package info.ejava.examples.app.config.propertysource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import info.ejava.examples.app.hello.Hello;
import info.ejava.examples.app.hello.stdout.StdOutHello;

@SpringBootApplication
public class PropertySourceApp {

    public static void main(String[] args) {
        SpringApplication.run(PropertySourceApp.class, args);
    }

    @Bean
    public Hello getHello(){
        return new StdOutHello("Application @Bean says hey -") ;
    }

}
