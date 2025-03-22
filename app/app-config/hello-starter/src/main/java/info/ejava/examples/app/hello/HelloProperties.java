package info.ejava.examples.app.hello;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("hello")
@Data
@Validated
public class HelloProperties {
    @NotBlank
    private String greeting = "HelloProperties default greeting says Hola!";
}
