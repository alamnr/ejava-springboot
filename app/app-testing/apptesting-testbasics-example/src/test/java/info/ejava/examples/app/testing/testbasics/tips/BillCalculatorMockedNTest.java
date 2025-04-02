package info.ejava.examples.app.testing.testbasics.tips;

import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BillCalculatorImpl.class) // define custom spring context
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Bill CalculatorImpl Mocked Integration")
public class BillCalculatorMockedNTest {

    @Autowired // subject under test
    BillCalculator  billCalculator;

    @MockBean // will satisfy autowired injection point with in BillCalculatorImpl
    TipCalculator tipCalculatorMock;

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
