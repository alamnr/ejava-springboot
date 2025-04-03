package info.ejava.examples.app.testing.testbasics.testconfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import info.ejava.examples.app.testing.testbasics.tips.BillCalculator;
import info.ejava.examples.app.testing.testbasics.tips.ServiceQuality;
import info.ejava.examples.app.testing.testbasics.tips.TipCalculator;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(
        //classes = MyTestConfiguration.class,
        properties = "spring.main.allow-bean-definition-overriding=true"
    )
//@ContextConfiguration(classes=MyTestConfiguration.class)
//@Import(MyTestConfiguration.class)
@ActiveProfiles("test")
@Tag("springboot")@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("bill calculator")
@Slf4j
public class TestConfigurationNTest {

    @Autowired
    BillCalculator billCalculator;
    
    // a single TipCalculator registered in the list because each considered have the same name and overriding is enabled
    // the TipCalculator used is one of the @TestConfiguration-supplied components
    @Autowired 
    List<TipCalculator> tipCalculators;

    @TestConfiguration //adds/overrides bean definitions in context
    static class MyEmbeddedTestConfiguration {
        @Bean
        public TipCalculator standardTippingImpl() {
            return new TipCalculator() {
                @Override
                public BigDecimal calcTip(BigDecimal amount, ServiceQuality serviceQuality) {
                    return BigDecimal.ZERO;
                }
            };
        }
    }




    @Test
    void calc_has_been_replaced() {
        //given
        log.info("calcs={}", tipCalculators.stream().map(tc->tc.getClass()).collect(Collectors.toList()));
        //then
        BDDAssertions.then(tipCalculators).as("too many topCalculators").hasSize(1);
        BDDAssertions.then(tipCalculators.get(0).getClass().getName())
                .as("unexpected tipCalc implementation class")
                .matches(".*My.*TestConfiguration.*");
    }

    @Test
    void calc_shares_for_bill_total() {
        //given
        BigDecimal billTotal = BigDecimal.valueOf(100.0);
        ServiceQuality service = ServiceQuality.GOOD;
        BigDecimal tip = BigDecimal.ZERO;
        int numPeople = 4;

        //when - call method under test
        BigDecimal shareResult = billCalculator.calcShares(billTotal, service, numPeople);

        //then - verify correct result
        BigDecimal expectedShare = billTotal.add(tip).divide(BigDecimal.valueOf(4));
        BDDAssertions.then(shareResult).isEqualTo(expectedShare);
    }

    
}
