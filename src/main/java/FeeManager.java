import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeeManager {

    private final static BigDecimal FEE_PERCENTAGE = BigDecimal.valueOf(0.01);

    /*
    Returns the amount after deducting fee
     */
    public static BigDecimal deductFee(BigDecimal amount, int numberOfAddresses) {
        // assuming that the fee amount remains in the house account so no transfer needed
        return amount.multiply(BigDecimal.ONE.subtract(FEE_PERCENTAGE)).setScale(2, RoundingMode.DOWN);
    }
}
