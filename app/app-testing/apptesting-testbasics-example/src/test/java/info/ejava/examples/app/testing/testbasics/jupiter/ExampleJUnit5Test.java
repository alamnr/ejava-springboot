package info.ejava.examples.app.testing.testbasics.jupiter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import lombok.extern.slf4j.Slf4j;

@TestMethodOrder(
//        MethodOrderer.OrderAnnotation.class
//        MethodOrderer.MethodName.class
//        MethodOrderer.DisplayName.class
          MethodOrderer.Random.class
)
@Slf4j
public class ExampleJUnit5Test {

    @BeforeAll
    static void setUpClass() {
        log.info("setUpClass");
    }

    @BeforeEach
    void setUp(){
        log.info("setUp");
    }

    @AfterEach
    void tearDown() {
        log.info("tearDown");
    }

    @AfterAll
    static void tearDownClass() {
        log.info("tearDownClass");
    }

   
    @Test
    @Order(1)
    void two_plus_two() {
        log.info("2+2=4");
        assertEquals(4, 2+2);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("just demonstrating an expected exception");
        });
        assertTrue(ex.getMessage().startsWith("just demo"));
    }


    // in Junit5 assertion description is given last and it hgas not pay the price of creating String 
    // cause the description is generated when assertion fails
    @Test
    @Order(2)
    void one_and_one(){
        log.info("1+1 = 2");
        assertTrue(1+1 == 2, "Problem with (tr) one plus one");
        assertEquals(2, 1+1 , () -> String.format("problem with (eq) %d+%d", 1,1));
    }

    // in JUnit5 Exception assertion and can be inspected with in method at any point 
    @Test
    @Order(3)
    public void exception() {
        log.info("exception test");
        RuntimeException ex1 = Assertions.assertThrows(RuntimeException.class, () -> {
            throw new IllegalArgumentException("example exception");
        });
        assertTrue(ex1.getMessage().contains("example"));
    }
}

