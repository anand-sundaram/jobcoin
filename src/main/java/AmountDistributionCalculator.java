import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class AmountDistributionCalculator {

    private static final AmountDistributionMode AMOUNT_DISTRIBUTION_MODE = AmountDistributionMode.RANDOM;

    public static Map<String, BigDecimal> getAmountDistributionMap(BigDecimal amount, List<String> addresses) {
        Map<String, BigDecimal> amountDistribution = new HashMap<>();
        if (AMOUNT_DISTRIBUTION_MODE.equals(AmountDistributionMode.RANDOM)) {
            Random random = new Random();
            for (int x = 0; x < addresses.size(); x++) {
                if (x == addresses.size() - 1) {
                    amountDistribution.put(addresses.get(x), amount);
                } else {
                    BigDecimal transferIncrement = BigDecimal.valueOf(random.nextDouble() * amount.doubleValue())
                            .setScale(2, RoundingMode.DOWN);
                    amountDistribution.put(addresses.get(x), transferIncrement);
                    amount = amount.subtract(transferIncrement);
                }
            }
        } else {
            BigDecimal transferIncrement = amount.divide(BigDecimal.valueOf(addresses.size()), 2, RoundingMode.DOWN);
            for (int x = 0; x < addresses.size(); x++) {
                if (x == addresses.size() - 1) {
                    amountDistribution.put(addresses.get(x), amount);
                } else {
                    amountDistribution.put(addresses.get(x), transferIncrement);
                    amount = amount.subtract(transferIncrement);
                }
            }
        }

        return amountDistribution;
    }
}

enum AmountDistributionMode {
    UNIFORM,
    RANDOM
}