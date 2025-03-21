package info.ejava.examples.app.config.configproperties.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("user")
@Data
public class UserProperties {
    @NotNull
    private final String name;
    @NotNull
    private final String home;
    @NotNull
    private final String timezone;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public UserProperties(String name, String home, String timezone) {
        this.name = name;
        this.home = home;
        this.timezone = timezone;
    }
}
