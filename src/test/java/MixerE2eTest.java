import client.JobcoinClient;
import model.AddressInfo;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MixerE2eTest {

    private final Logger logger = Logger.getLogger("MixerE2eTest");
    private final Client client = ClientBuilder.newClient();

    @Test
    public void simple_e2e_test() {
        /*
        This is not exactly a unit test, it runs the program by calling the Mixer methods,
        and waits before checking whether the amount has actually made it to the provided addresses
         */

        TransactionManagerImpl transactionManager = new TransactionManagerImpl();
        JobcoinClient jobcoinClient = new JobcoinClient(client);
        Mixer mixer = new Mixer(transactionManager);
        transactionManager.setup(mixer, jobcoinClient);

        List<String> addressList = new ArrayList<>();
        for (int x = 0; x < 5; x++) {
            addressList.add(UUID.randomUUID().toString());
        }
        logger.log(Level.INFO, "The randomly generated address list is: " + addressList);
        String depositAddress = mixer.createDepositAddress(addressList);
        logger.log(Level.INFO, "The deposit address is: " + depositAddress);
        create50newJobcoins(depositAddress);

        try {
            TimeUnit.SECONDS.sleep(5);
            BigDecimal sum = BigDecimal.ZERO;
            for (String address: addressList) {
                AddressInfo addressInfo = jobcoinClient.getAddressInfo(address);
                sum = sum.add(new BigDecimal(addressInfo.getBalance()));
            }
            assertEquals(new BigDecimal("49.5"), sum);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /*
    This method uses the form endpoint to create new jobcoins in the deposit address
     */
    public void create50newJobcoins(String depositAddress) {
        WebTarget webTarget = client
                .target("https://jobcoin.gemini.com/professed-exploit/create");

        Invocation.Builder invocationBuilder
                = webTarget.request(MediaType.APPLICATION_FORM_URLENCODED);

        Form form = new Form();
        form.param("address", depositAddress);
        Response response = invocationBuilder
                .post(Entity.form(form));

        if (!response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
            logger.log(Level.SEVERE, "Failed to add money with error: " + response.getStatus());
        }
    }
}
