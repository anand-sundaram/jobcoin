import java.math.BigDecimal;

public interface TransactionManager {

    void performTransaction(String fromAddress, String toAddress, BigDecimal amount);

    void listenForAddress(String address);
}
