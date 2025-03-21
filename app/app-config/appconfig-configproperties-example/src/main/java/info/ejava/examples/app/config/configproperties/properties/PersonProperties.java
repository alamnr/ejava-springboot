package info.ejava.examples.app.config.configproperties.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

//@ConfigurationProperties("???") multiple prefixes mapped
@Data
@Validated
public class PersonProperties {
    @NotNull
    private String name;
    @NestedConfigurationProperty
    @NotNull
    private AddressProperties address;
}
