import java.math.BigDecimal;

public interface TransactionNotificationDelegate {

    void onTransactionEvent(BigDecimal amount, String depositAddress);
}
