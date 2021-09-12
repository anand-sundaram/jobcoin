import client.ClientException;
import client.JobcoinClient;
import model.AddressInfo;
import model.TransactionRequest;

import javax.ws.rs.client.ClientBuilder;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionManagerImpl implements TransactionManager {

    private final Logger logger = Logger.getLogger("TransactionManagerImpl");

    private final JobcoinClient jobcoinClient = new JobcoinClient(ClientBuilder.newClient());

    private TransactionNotificationDelegate transactionNotificationDelegate;

    public void setTransactionNotificationDelegate(TransactionNotificationDelegate transactionNotificationDelegate) {
        this.transactionNotificationDelegate = transactionNotificationDelegate;
    }

    @Override
    public void performTransaction(String fromAddress, String toAddress, BigDecimal amount) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(amount.toPlainString());
        transactionRequest.setFromAddress(fromAddress);
        transactionRequest.setToAddress(toAddress);
        try {
            jobcoinClient.createTransaction(transactionRequest);
        } catch (ClientException e) {
            logger.log(Level.SEVERE, "Failed to perform a transaction, this transaction will be skipped");
        }

    }

    @Override
    public void listenForAddress(String address) {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime < 30000)) {
            AddressInfo addressInfo = null;
            try {
                addressInfo = jobcoinClient.getAddressInfo(address);
                BigDecimal balance = new BigDecimal(addressInfo.getBalance());
                if (!balance.equals(BigDecimal.ZERO)) {
                    // TODO: null check on transactionNotificationDelegate
                    transactionNotificationDelegate.onTransactionEvent(balance, address);
                    break;
                }
            } catch (ClientException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
    }
}
