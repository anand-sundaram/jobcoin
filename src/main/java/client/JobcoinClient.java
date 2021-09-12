package client;

import model.AddressInfo;
import model.SuccessResponse;
import model.TransactionRequest;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

public class JobcoinClient {

    private final Logger logger = Logger.getLogger("client.JobcoinClient");
    Client client;

    public JobcoinClient(Client client) {
        this.client = client;
    }

    public AddressInfo getAddressInfo(String address) throws ClientException {
        AddressInfo addressInfo;
        try {
            WebTarget webTarget = client
                    .target("http://jobcoin.gemini.com/professed-exploit/api/addresses/{address}")
                    .resolveTemplate("address", address);

            Invocation.Builder invocationBuilder
                    = webTarget.request(MediaType.APPLICATION_JSON);

            Response response = invocationBuilder.get();
            response.bufferEntity();
            if (!response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
                logger.log(Level.INFO, "API call returned unsuccessful status: " + response.getStatus());
                throw new ClientException();
            }
            addressInfo = invocationBuilder.get(AddressInfo.class);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ClientException();
        }

        return addressInfo;
    }

    public void createTransaction(TransactionRequest transactionRequest) throws ClientException {
        try {
            WebTarget webTarget = client
                    .target("http://jobcoin.gemini.com/professed-exploit/api/transactions");

            Invocation.Builder invocationBuilder
                    = webTarget.request(MediaType.APPLICATION_JSON);

            Response response = invocationBuilder
                    .post(Entity.entity(transactionRequest, MediaType.APPLICATION_JSON));
            if (!response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
                logger.log(Level.INFO, "API call returned unsuccessful status: " + response.getStatus());
                throw new ClientException();
            }
            SuccessResponse successResponse = response.readEntity(SuccessResponse.class);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ClientException();
        }
    }
}
