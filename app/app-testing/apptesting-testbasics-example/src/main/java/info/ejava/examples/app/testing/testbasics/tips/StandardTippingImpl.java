package info.ejava.examples.app.testing.testbasics.tips;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class StandardTippingImpl implements TipCalculator {

    private BigDecimal[] rates = new BigDecimal[]{
        BigDecimal.valueOf(0.15),
        BigDecimal.valueOf(0.18),
        BigDecimal.valueOf(0.20)
    };

    @Override
    public BigDecimal calcTip(BigDecimal amount, ServiceQuality serviceQuality) {
        BigDecimal tipRate = rates[serviceQuality.ordinal()];
        return amount.multiply(tipRate);
    }
    
}
