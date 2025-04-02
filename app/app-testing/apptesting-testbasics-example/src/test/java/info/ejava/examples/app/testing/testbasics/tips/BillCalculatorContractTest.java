package info.ejava.examples.app.testing.testbasics.tips;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.math.BigDecimal;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Bill Calculator Contract")
@ExtendWith(MockitoExtension.class)
public class BillCalculatorContractTest {

    @Mock 
    private BillCalculator  billCalculatorMock;  
    
    @Test
    public void  calc_shares_for_people_including_tip(){
        // given - billCalculator will return the value for a share
        BDDMockito.given(billCalculatorMock.calcShares(any(), any(), anyInt())).willReturn(BigDecimal.valueOf(1));
        // when
        BigDecimal share = billCalculatorMock.calcShares(BigDecimal.valueOf(100), ServiceQuality.FAIR, 2);
        // then
        BDDAssertions.then(share).isNotNull();
    }
}
