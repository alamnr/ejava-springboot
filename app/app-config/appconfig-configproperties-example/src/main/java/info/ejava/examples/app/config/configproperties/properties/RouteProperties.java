package info.ejava.examples.app.config.configproperties.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties("app.config.route")
@Data
@Validated
public class RouteProperties {
    @NotNull
    private final String name;
    @NestedConfigurationProperty
    @NotNull
    @Size(min = 1)
    private final List<AddressProperties> stops;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public RouteProperties(String name, List<AddressProperties> stops) {
        this.name = name;
        this.stops = stops;
    }
}
