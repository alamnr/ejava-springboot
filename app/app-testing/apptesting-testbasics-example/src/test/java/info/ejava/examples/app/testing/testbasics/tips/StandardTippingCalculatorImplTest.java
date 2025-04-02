package info.ejava.examples.app.testing.testbasics.tips;

import static org.mockito.Mockito.withSettings;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;

@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Standard Tipping Calculator")
public class StandardTippingCalculatorImplTest {
     
    // subject under test
    private TipCalculator tipCalculator;

    @BeforeEach
    public void setUp() { // simulating a complex initialization
        tipCalculator = new StandardTippingImpl();
    }

    @Test
    void given_fair_service() {
        // given - a 100$ bill with fair service
        BigDecimal billTotal = new BigDecimal(100);
        ServiceQuality serviceQuality = ServiceQuality.FAIR;

        // when - calculating tip
        BigDecimal resultTip = tipCalculator.calcTip(billTotal, serviceQuality);

        // then - expect a result that is 15% of the $100 total
        BigDecimal expectedTip =  billTotal.multiply(BigDecimal.valueOf(0.15));
        BDDAssertions.then(resultTip).isEqualTo(expectedTip);
    }

    // When we don’t provide a name for the @MethodSource, JUnit will search for a source method with the same name as the test method.
    @ParameterizedTest
    @MethodSource
    public void given_service_level(BigDecimal billTotal, ServiceQuality q, BigDecimal expectedTip) {
        // when - calculating tip
        BigDecimal resultTip = tipCalculator.calcTip(expectedTip, q);

        // then - expect result within range
        BDDAssertions.then(resultTip).isCloseTo(expectedTip, Assertions.withinPercentage(0.1));
    }

    static Stream<Arguments> given_service_level() {
        return Stream.of(
            Arguments.of(new BigDecimal(100),ServiceQuality.FAIR, new BigDecimal(15)),
            Arguments.of(new BigDecimal(100),ServiceQuality.GOOD, new BigDecimal(18)),
            Arguments.of(new BigDecimal(100), ServiceQuality.GREAT, new BigDecimal(20))
        );
    }


    
}
