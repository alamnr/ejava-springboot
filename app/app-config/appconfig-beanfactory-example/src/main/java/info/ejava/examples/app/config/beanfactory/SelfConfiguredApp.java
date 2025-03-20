package info.ejava.examples.app.config.beanfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import info.ejava.examples.app.hello.Hello;
import info.ejava.examples.app.hello.stdout.StdOutHello;

@SpringBootApplication
public class SelfConfiguredApp {

    public static void main(String... args){
        SpringApplication.run(SelfConfiguredApp.class, args);
    }

    @Bean
    public Hello getHello(){
        return new StdOutHello("Application @Bean says hey - ");
    }
}
