package info.ejava.examples.app.testing.testbasics.tips;

import java.math.BigDecimal;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import info.ejava.examples.app.testing.testbasics.TestBasicsApp;

@SpringBootTest
 //(classes = TestBasicsApp.class)
 (classes = {BillCalculatorImpl.class, StandardTippingImpl.class})
@ActiveProfiles("test")
@Tag("springboot") @Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("bill calculator")
public class BillCalculatorNTest {

    @Autowired
    BillCalculator billCalculator;
    @Test
    public void calc_shares_for_bill_total() {
        //given
        BigDecimal billTotal = BigDecimal.valueOf(100.0);
        ServiceQuality service = ServiceQuality.GOOD;
        BigDecimal tip = billTotal.multiply(BigDecimal.valueOf(0.18));
        int numPeople = 4;

        //when - call method under test
        BigDecimal shareResult = billCalculator.calcShares(billTotal, service, numPeople);

        //then - verify correct result
        BigDecimal expectedShare = billTotal.add(tip).divide(BigDecimal.valueOf(4));
        BDDAssertions.then(shareResult).isEqualTo(expectedShare);
    }

}
