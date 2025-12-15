package info.ejava.examples.db.mongo.books;

import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.WebApplicationInitializer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@Configuration
public class BooksConfiguration {
    @Autowired
    private ConfigurableEnvironment env;

    @Bean
    public BookDTOFactory booksDtoFactory() {
        return new BookDTOFactory();
    }
}
