package info.ejava.examples.app.config.configproperties.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

/**
 * This class provides a example of ConfigurationProperties class that uses
 * a constructor and final variable(s) to create a read-only property class.
 */
@ConfigurationProperties("app.config.boat")
@Validated
public class BoatProperties {
    @NotBlank
    private final String name;

    @ConstructorBinding //only required for multiple constructors
    public BoatProperties(String name) {
        this.name = name;
    }
    //not used for ConfigurationProperties initialization
    public BoatProperties() { this.name = "default"; }
    //no setter method(s) in read-only example
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BoatProperties{name='" + name + "\'}";
    }
}
