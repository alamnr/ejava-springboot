package info.ejava.examples.db.mongo.books.bo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Document(collection = "books")
@Builder
@With
@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@CompoundIndex(def = "{'author':1, 'published':-1}", unique = true, background = false, sparse = false)
public class Book {

    @Id @Setter(AccessLevel.NONE)
    private String id;
    @Field(name = "title")
    @Indexed(unique = true, sparse = true, direction = IndexDirection.ASCENDING, background = false)
    private String title;
    private String author;
    private LocalDate published;
    
}
