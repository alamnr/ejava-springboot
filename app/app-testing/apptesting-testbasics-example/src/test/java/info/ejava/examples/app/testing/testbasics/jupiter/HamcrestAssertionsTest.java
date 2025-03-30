package info.ejava.examples.app.testing.testbasics.jupiter;


import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;

import org.exparity.hamcrest.date.DateMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import info.ejava.examples.app.testing.testbasics.PeopleFactory;
import info.ejava.examples.app.testing.testbasics.Person;



public class HamcrestAssertionsTest {

    Person beaver = PeopleFactory.beaver();
    Person wally = PeopleFactory.wally();
    Person eddie = PeopleFactory.eddie();

    // core 
    @Test
    void basicTypes() {
        MatcherAssert.assertThat(beaver.getFirstName(),Matchers.equalTo("Jerry"));
        MatcherAssert.assertThat("name", beaver.getFirstName(), Matchers.equalTo("Jerry"));
    }

    // dates
    @Test
    public void dateTypes() {
        // support for date matchers requires additional org.exparity:hamcrest-date library
        MatcherAssert.assertThat(beaver.getDob(), DateMatchers.after(wally.getDob()));
        MatcherAssert.assertThat("beaver not younger than wally", beaver.getDob(),DateMatchers.after(wally.getDob()));
    }

    // exceptions
    @Test
    void exceptions() {
        // no support for exceptions - Assertions.assertThrows is a JUnit Jupiter call
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, 
                () -> {  throw new IllegalArgumentException("example exception");} );
        MatcherAssert.assertThat(ex.getMessage(),Matchers.equalTo("example exception"));
    }

    // all
    @Test
    void all() {
        Person p = beaver; // change to eddie to cause failures 
        // can only test multiple assertions against same subject
        MatcherAssert.assertThat(p.getFirstName(), Matchers.allOf(
                                    Matchers.startsWith("J"), 
                                    Matchers.endsWith("y"),
                                    Matchers.equalTo("Jerry"))); 
    }

}
