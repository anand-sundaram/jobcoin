import client.ClientException;
import client.JobcoinClient;
import model.AddressInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

public class TransactionManagerImplTest {

    JobcoinClient jobcoinClient;
    TransactionNotificationDelegate transactionNotificationDelegate;

    TransactionManagerImpl transactionManager;

    @BeforeEach
    public void setup() {
        jobcoinClient = Mockito.mock(JobcoinClient.class);
        transactionNotificationDelegate = Mockito.mock(TransactionNotificationDelegate.class);
        transactionManager = new TransactionManagerImpl();
        transactionManager.setup(transactionNotificationDelegate, jobcoinClient);
    }

    @Test
    public void testPerformTransaction_success() {
        transactionManager.performTransaction("from", "to", BigDecimal.TEN);
    }

    @Test
    public void testPerformTransaction_handleClientException() throws ClientException {
        willThrow(new ClientException()).given(jobcoinClient).createTransaction(any());
        transactionManager.performTransaction("from", "to", BigDecimal.TEN);
    }

    @Test
    public void testListenForAddress_success() throws ClientException {
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setBalance("100");
        when(jobcoinClient.getAddressInfo(any())).thenReturn(addressInfo);
        transactionManager.listenForAddress("to");

        verify(transactionNotificationDelegate, times(1)).onTransactionEvent(any(), any());
    }

    @Test
    public void testListenForAddress_handleClientException() throws ClientException {
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setBalance("100");
        when(jobcoinClient.getAddressInfo(any())).thenThrow(new ClientException());

        // This test will run for the duration of the timeout coded in the while loop in TransactionManagerImpl.listenForAddress
        transactionManager.listenForAddress("to");

        verify(transactionNotificationDelegate, times(0)).onTransactionEvent(any(), any());
    }
}
