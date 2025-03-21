package info.ejava.examples.app.config.propertysource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import info.ejava.examples.app.hello.Hello;

@Component
public class AppCommand implements CommandLineRunner  {

    private final Hello greeter;

    @Value("${app.audience}")
    private String audience;

    public AppCommand(Hello greeter) {
        this.greeter = greeter;
    }

    @Override
    public void run(String... args) throws Exception {
        greeter.sayHello(audience);    
    }

}
