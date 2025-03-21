package info.ejava.examples.app.config.propertysource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource( name = "propertySource",value = "classpath:info/examples/app/config/propertysource/packaged_propertySource.properties")
public class PropertySourceConfiguration {
    
}
