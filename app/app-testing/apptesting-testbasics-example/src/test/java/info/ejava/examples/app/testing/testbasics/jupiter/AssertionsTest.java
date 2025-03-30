package info.ejava.examples.app.testing.testbasics.jupiter;

import org.assertj.core.api.BDDAssertions;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssertionsTest {
    int lhs = 1;
    int rhs = 1;
    int expected = 2;

    @Test
    void one_and_one() {

        // JUnit4 vintage assertion
        Assert.assertEquals(expected, lhs+rhs);
        // Jupiter Assertions
        Assertions.assertEquals(expected, lhs+rhs);
        // Hamcrest assertion
        MatcherAssert.assertThat(lhs+rhs, Matchers.is(expected));
        // AssertJ assertion
        org.assertj.core.api.Assertions.assertThat(lhs+rhs).isEqualTo(expected);
        // AssertJ BDD
        BDDAssertions.then(lhs+rhs).isEqualTo(expected);
    }

    @Test
    void one_and_one_description() {
        // JUnit 4 vintage assertion
        Assert.assertEquals("math error",expected, lhs+rhs);
        // Jupiter Assertions
        Assertions.assertEquals(expected, lhs+rhs,"math error");
        Assertions.assertEquals(expected, lhs+rhs, () -> String.format("math error %d+%d != %d", lhs,rhs,expected));
        // Hamcrest assertion
        MatcherAssert.assertThat("Math Error", lhs+rhs,Matchers.is(expected));
        // AssertJ assertion
        org.assertj.core.api.Assertions.assertThat(lhs+rhs);
        org.assertj.core.api.Assertions.assertThat(lhs+rhs)
                        .as("math error %d+%d != %d", lhs, rhs, expected)
                        .isEqualTo(expected);
        // AssertJ BDD
        BDDAssertions.then(lhs+rhs)
                        .as("math error")
                        .isEqualTo(expected);

    }

    @Test
    void junit_all() {
        Assertions.assertAll("all assertions",
            // Jupiter assertions
            () -> Assertions.assertEquals(expected, lhs+rhs, "Jupiter assertion"),
            () -> Assertions.assertEquals(expected, lhs+rhs, () -> String.format("Jupiter format %d+%d != %d", lhs,rhs,expected))
        );
    }
}
