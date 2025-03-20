package info.ejava.examples.app.config.beanfactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import info.ejava.examples.app.hello.Hello;

@Component
public class AppCommand implements CommandLineRunner {

    private final Hello hello;
    public AppCommand(Hello hello){
        this.hello = hello;
    }

    @Override
    public void run(String... args) throws Exception {
        hello.sayHello(" Keramat");
    }

}
