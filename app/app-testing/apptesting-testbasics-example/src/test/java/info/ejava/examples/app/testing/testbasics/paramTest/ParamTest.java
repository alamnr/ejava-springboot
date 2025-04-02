package info.ejava.examples.app.testing.testbasics.paramTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Month;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import info.ejava.examples.app.testing.testbasics.parameterizedTest.Numbers;

public class ParamTest {
    
    @ParameterizedTest
    @ValueSource(ints = {1,3,5,-3,15,Integer.MAX_VALUE}) // six numbers
    void isOdd_shouldReturnTrueForOddNumbers(int number) {
        Assertions.assertTrue(Numbers.isOdd(number));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void isBlank_returnTrueForNullOrBlankString(String input) {
        Assertions.assertTrue(Numbers.isBlank(input));
    }

    @ParameterizedTest
    @NullSource
    void isBlank_ShouldReturnTrueForNullInputs(String input) {
        Assertions.assertTrue(Numbers.isBlank(input));
    }

    @ParameterizedTest
    @EmptySource
    void isBlank_ShouldReturnTrueForEmptyStrings(String input) {
        Assertions.assertTrue(Numbers.isBlank(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void isBlank_ShouldReturnTrueForNullAndEmptyStrings(String input) {
        assertTrue(Numbers.isBlank(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void isBlank_ShouldReturnTrueForAllTypesOfBlankStrings(String input) {
        assertTrue(Numbers.isBlank(input));
    }

    @ParameterizedTest
    @EnumSource(Month.class) // passing all 12 months
    void getValueForAMonth_IsAlwaysBetweenOneAndTwelve(Month month) {
        int monthNumber = month.getValue();
        assertTrue(monthNumber >= 1 && monthNumber <= 12);
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    void isBlank_ShouldReturnTrueForNullOrBlankStrings(String input, boolean expected) {
        assertEquals(expected, Numbers.isBlank(input));
    }

    private static Stream<Arguments> provideStringsForIsBlank() {
        return Stream.of(
            Arguments.of(null, true),
            Arguments.of("", true),
            Arguments.of("  ", true),
            Arguments.of("not blank", false)
        );
    }

    // When we don’t provide a name for the @MethodSource, JUnit will search for a source method with the same name as the test method.
    @ParameterizedTest
    @MethodSource // hmm, no method name ...
    void isBlank_ShouldReturnTrueForNullOrBlankStringsOneArgument(String input) {
        assertTrue(Numbers.isBlank(input));
    }

    private static Stream<String> isBlank_ShouldReturnTrueForNullOrBlankStringsOneArgument() {
        return Stream.of(null, "", "  ");
    }
}
