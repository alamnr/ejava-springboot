package info.ejava.examples.app.config.propertysource.profiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PropertySourceProfileApp {
    public static final void main(String...args) {
        SpringApplication.run(PropertySourceProfileApp.class, args);
    }
}
