package calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmountDistributionCalculatorTest {

    @Test
    public void testGetAmountDistributionMap_uniform() {
        Map<String, BigDecimal> amountMap = AmountDistributionCalculator
                .getAmountDistributionMap(new BigDecimal("75.56"), Arrays.asList("a1", "a2", "a3"));
        assertEquals(3, amountMap.size());
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount: amountMap.values()) {
            sum = sum.add(amount);
        }
        assertEquals(sum, (new BigDecimal("75.56")));
    }

    @Test
    public void testGetAmountDistributionMap_random() {
        Map<String, BigDecimal> amountMap = AmountDistributionCalculator
                .getAmountDistributionMap(new BigDecimal("75.56"), Arrays.asList("a1", "a2", "a3"), AmountDistributionMode.RANDOM);
        assertEquals(3, amountMap.size());
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount: amountMap.values()) {
            sum = sum.add(amount);
        }
        assertEquals(sum, (new BigDecimal("75.56")));
    }
}
