import calculator.AmountDistributionCalculator;
import calculator.FeeManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Mixer implements TransactionNotificationDelegate {

    private final static String houseAddress = "House";

    private final TransactionManager transactionManager;

    private final Map<String, List<String>> depositAddressToAddressMap;

    public Mixer(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.depositAddressToAddressMap = new HashMap<>();
    }

    public String createDepositAddress(List<String> addresses) {
        String depositAddress = UUID.randomUUID().toString();
        depositAddressToAddressMap.put(depositAddress, addresses);
        CompletableFuture.runAsync(() -> transactionManager.listenForAddress(depositAddress));
        return depositAddress;
    }

    @Override
    public void onTransactionEvent(BigDecimal amount, String depositAddress) {
        // TODO: add validation for scenario where address does not exist in map
        transactionManager.performTransaction(depositAddress, houseAddress, amount);
        List<String> addresses = depositAddressToAddressMap.get(depositAddress);
        amount = FeeManager.deductFee(amount, addresses.size());
        Map<String, BigDecimal> amountDistribution = AmountDistributionCalculator.getAmountDistributionMap(amount, addresses);
        distributeToAddresses(amountDistribution);
    }

    private void distributeToAddresses(Map<String, BigDecimal> amountDistribution) {
        amountDistribution.forEach((address, amount) -> {
            transactionManager.performTransaction(houseAddress, address, amount);
        });
    }
}
