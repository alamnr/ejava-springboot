package info.ejava.examples.app.testing.testbasics.tips;

import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This test case provides an example of implementing a unit test with
 * Mocks and without a Spring Context and without using Mockito
 * to instantiate the subjects with Mocks. We do this by hand.
 */

 @ExtendWith(MockitoExtension.class)  // Add Mockito extension to JUnit
 @Tag("tips")
 @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
 @DisplayName("Bill Calculator Mocked Unit Test")
public class BillCalculatorMockedTest {
    // subject under test
    private BillCalculator billCalculator;
    
    @Mock
    private TipCalculator tipCalculatorMock; // Identify which interfaces to Mock

    @BeforeEach
    void init() {
        // we are manually wiring up the subject under test
        billCalculator = new BillCalculatorImpl(tipCalculatorMock); // tipCalculatorMock is manually injected into BillCalculatorImpl
    }

    @Test
    public void calc_shares_for_people_including_tip() {
        //given - we have a bill for 4 people and tip calculator that returns tip amount
        BigDecimal billTotal = new BigDecimal(100.0);
        ServiceQuality service = ServiceQuality.GOOD;
        BigDecimal tip = billTotal.multiply(new BigDecimal(0.18));
        int numPeople = 4;
        //configure mock
        BDDMockito.given(tipCalculatorMock.calcTip(billTotal, service)).willReturn(tip);

        //when - call method under test
        BigDecimal shareResult = billCalculator.calcShares(billTotal, service, numPeople);

        //then - tip calculator should be called once to get result
        BDDMockito.then(tipCalculatorMock).should(times(1)).calcTip(billTotal,service);

        //verify correct result
        BigDecimal expectedShare = billTotal.add(tip).divide(new BigDecimal(numPeople));
        BDDAssertions.and.then(shareResult).isEqualTo(expectedShare);
    }



}
