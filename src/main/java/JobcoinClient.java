import model.AddressInfo;
import model.SuccessResponse;
import model.TransactionRequest;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

public class JobcoinClient {

    public AddressInfo getAddressInfo(String address) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client
                .target("http://jobcoin.gemini.com/professed-exploit/api/addresses/{address}")
                .resolveTemplate("address", address);

        Invocation.Builder invocationBuilder
                = webTarget.request(MediaType.APPLICATION_JSON);

        AddressInfo addressInfo
                = invocationBuilder.get(AddressInfo.class);

        return addressInfo;
    }

    public void createTransaction(TransactionRequest transactionRequest) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client
                .target("http://jobcoin.gemini.com/professed-exploit/api/transactions");

        Invocation.Builder invocationBuilder
                = webTarget.request(MediaType.APPLICATION_JSON);

        SuccessResponse successResponse = invocationBuilder
                .post(Entity.entity(transactionRequest, MediaType.APPLICATION_JSON))
                .readEntity(SuccessResponse.class);
    }
}
