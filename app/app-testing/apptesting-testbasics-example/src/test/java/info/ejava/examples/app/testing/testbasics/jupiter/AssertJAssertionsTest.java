package info.ejava.examples.app.testing.testbasics.jupiter;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import info.ejava.examples.app.testing.testbasics.PeopleFactory;
import info.ejava.examples.app.testing.testbasics.Person;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssertJAssertionsTest {

    Person beaver = PeopleFactory.beaver();
    Person wally = PeopleFactory.wally();
    Person eddie = PeopleFactory.eddie();

    // core
    @Test
    void basicTypes() {
        BDDAssertions.assertThat(beaver.getFirstName()).isEqualTo("Jerry");
        BDDAssertions.assertThat(beaver.getFirstName()).as("name").isEqualTo("Jerry");
    }

    // dates
    @Test
    void dateTypes() {
        BDDAssertions.assertThat(beaver.getDob()).isAfter(wally.getDob());
        BDDAssertions.assertThat(beaver.getDob())
                        .as("beaver not younger than wally")
                        .isAfter(wally.getDob());
    }

    // exceptions
    @Test
    void exceptions() {
        BDDAssertions.assertThatThrownBy(
         () -> {
                throw new IllegalArgumentException("example exception");
         }).hasMessage("example exception");
         BDDAssertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
            throw new IllegalArgumentException("example exception");
         }).withMessage("example exception");

        Throwable ex1 = Assertions.catchThrowable(
            () -> {  
                throw new IllegalArgumentException("example exception");
            });
        BDDAssertions.assertThat(ex1).hasMessage("example exception");
        
        RuntimeException ex2 = BDDAssertions.catchThrowableOfType(RuntimeException.class, 
          ()  -> {
            throw new IllegalArgumentException("example exception");
          });

        BDDAssertions.assertThat(ex2).hasMessage("example exception");
    }


    
    // BDD exceptions
    @Test
    void bdd_exceptions(){
        // Bdd syntax only has evaluations
        BDDAssertions.thenThrownBy(() -> {
            throw new IllegalArgumentException("example exception");
        }).hasMessage("example exception");

        BDDAssertions.thenExceptionOfType(RuntimeException.class).isThrownBy(() -> {
            throw new IllegalArgumentException("example exception");
        }).withMessage("example exception");
    }
    // all
    @Test
    void all() {
        Person p = beaver; // change to eddie to cause failures
        // Person p = eddie;
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(p.getFirstName()).isEqualTo("Jerry");
        softly.assertThat(p.getLastName()).isEqualTo("Mathers");
        softly.assertThat(p.getDob()).isAfter(wally.getDob());

        log.info("error count = {}", softly.errorsCollected().size());
        softly.assertAll();
    }

    // @Test
    // void extensions() {
    //     Assertions.assertThat(beaver).hasFirstName("Jerry").hasLastName("Mathers");
    //     BDDAssertions.then(beaver).hasFirstName("Jerry").hasLastName("Mathers");
    //     BDDAssertions.and.then(beaver).hasFirstName("Jerry").hasLastName("Jerry");
    // }

    
}
