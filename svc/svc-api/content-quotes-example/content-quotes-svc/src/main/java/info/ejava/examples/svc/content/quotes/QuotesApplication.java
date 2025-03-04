package info.ejava.examples.svc.content.quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.databind.SerializationFeature;

import info.ejava.examples.svc.content.quotes.dto.ISODateFormat;

@SpringBootApplication
public class QuotesApplication {

    public static void main(String...  args){
        SpringApplication.run(QuotesApplication.class, args);
    }

    /*
     * Execute these customizations first (Highest Precedence) and then the 
     * properties second so that properties can overrride Java configuration
     */

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Jackson2ObjectMapperBuilderCustomizer jacksonMapper(){
        return builder -> builder
                                //spring.jackson.serialization.indent-output=true
                                .featuresToEnable(SerializationFeature.INDENT_OUTPUT) 
                                //spring.jackson.serialization.write-dates-as-timestamps=false
                                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                //spring.jackson.date-format=info.ejava.examples.svc.content.quotes.dto.ISODateFormat
                                .dateFormat(new ISODateFormat());

    }
}
