import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MixerTest {

    private TransactionManager transactionManager;

    private Mixer mixer;

    @BeforeEach
    public void setup() {
        transactionManager = Mockito.mock(TransactionManager.class);
        mixer = new Mixer(transactionManager);
    }

    @Test
    public void testCreateDepositAddress() {
        String depositAddress = mixer.createDepositAddress(Arrays.asList("a1", "a2", "a3"));
        assertNotNull(depositAddress);
    }

    @Test
    public void testOnTransactionEvent() {
        String depositAddress = mixer.createDepositAddress(Arrays.asList("a1", "a2", "a3"));
        mixer.onTransactionEvent(new BigDecimal("75.56"), depositAddress);
        verify(transactionManager, times(4)).performTransaction(any(), any(), any());
    }
}
