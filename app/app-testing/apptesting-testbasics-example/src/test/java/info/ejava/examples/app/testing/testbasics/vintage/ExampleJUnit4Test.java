package info.ejava.examples.app.testing.testbasics.vintage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleJUnit4Test {
    
    @BeforeClass
    public static void setUpClass(){
        log.info("setUpClass");
    }

    @Before
    public void setUp(){
        log.info("setup");
    }

    @After
    public void tearDown() {
        log.info("tearDown");
    }

    @AfterClass
    public static void tearDownClass(){
        log.info("tearDownClass");
    }

    // in Juint4 there is no way to assert and inspect exception at any point inside method
    @Test(expected = IllegalArgumentException.class)
    public void two_plus_two() {
        log.info("2+2=4");
        assertEquals(4, 2+2);
        throw new IllegalArgumentException("just demonstrating an expected exception");
    }

    @Test
    // in Junit 4 the assertion description is placed first and  it has to pay the price of building String 
    // whether assertion passes or fails
    public void one_and_one(){
        log.info("1+1=2");
        assertTrue("problem with (true) 1+1", 1+1 == 3);
        assertEquals(String.format("problem with (equal) %d +%d",   1,1), 2 , 1+1);
    }

}
