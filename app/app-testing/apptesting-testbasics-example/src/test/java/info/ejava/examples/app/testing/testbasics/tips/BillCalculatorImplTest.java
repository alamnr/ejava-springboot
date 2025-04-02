package info.ejava.examples.app.testing.testbasics.tips;

import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Bill CalculatorImpl")
@ExtendWith(MockitoExtension.class)
public class BillCalculatorImplTest {
    
    @Mock
    TipCalculator tipCalculatorMock;

    // Mockito is instantiating this implementation class for us an injecting mocks
    @InjectMocks
    BillCalculatorImpl billCalculator; // instantiates and injects out subject under test
    

    @Test
    void calc_share_for_people_including_tip() {
        //given - we have a bill for 4 people and tip calculator that returns amount for tip
         BigDecimal billTotal = new BigDecimal(100.0);
        ServiceQuality service = ServiceQuality.GOOD;
        BigDecimal tip = new BigDecimal(50.00); //50% tip!!!
        int numPeople = 4;

        // configure mock

        BDDMockito.given(tipCalculatorMock.calcTip(billTotal, service)).willReturn(tip);

        // when - call method under test
        BigDecimal result = billCalculator.calcShares(billTotal, service, numPeople);

        // then - verify tipCalculator called once to get the result
        BDDMockito.then(tipCalculatorMock).should(times(1)).calcTip(billTotal, service);

        // and then verify correct result
        BigDecimal expectedShare = billTotal.add(tip).divide(new BigDecimal(numPeople));
        BDDAssertions.and.then(result).isEqualTo(expectedShare);

    }

}
