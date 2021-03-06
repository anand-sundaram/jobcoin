package client;

import model.AddressInfo;
import model.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class JobcoinClientTest {

    Client client;

    WebTarget webTarget;

    Invocation.Builder invocationBuilder;

    Response.StatusType statusType;

    Response response;

    JobcoinClient jobcoinClient;

    @BeforeEach
    public void setup() {
        client = Mockito.mock(Client.class);
        webTarget = Mockito.mock(WebTarget.class);
        invocationBuilder = Mockito.mock(Invocation.Builder.class);
        statusType = Mockito.mock(Response.StatusType.class);
        response = Mockito.mock(Response.class);
        jobcoinClient = new JobcoinClient(client);
    }

    @Test
    public void testGetAddressInfo_successfulResponse() throws ClientException {
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.resolveTemplate(anyString(), any())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(invocationBuilder);
        when(invocationBuilder.get()).thenReturn(response);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setBalance("10");
        when(response.readEntity(AddressInfo.class)).thenReturn(addressInfo);

        AddressInfo addressInfoResponse = jobcoinClient.getAddressInfo("address1");
        assertNotNull(addressInfoResponse);
        assertEquals("10", addressInfoResponse.getBalance());
    }

    @Test
    public void testGetAddressInfo_unsuccessfulResponse(){
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.resolveTemplate(anyString(), any())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(invocationBuilder);
        when(invocationBuilder.get()).thenReturn(response);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getFamily()).thenReturn(Response.Status.Family.SERVER_ERROR);

        assertThrows(ClientException.class, () -> jobcoinClient.getAddressInfo("address1"));
    }

    @Test
    public void testGetAddressInfo_exceptionOnParsingResponse() {
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.resolveTemplate(anyString(), any())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(invocationBuilder);
        when(invocationBuilder.get()).thenReturn(response);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
        when(response.readEntity(AddressInfo.class)).thenThrow(new RuntimeException());

        assertThrows(ClientException.class, () -> jobcoinClient.getAddressInfo("address1"));
    }

    @Test
    public void testCreateTransaction_successfulResponse() throws ClientException {
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.resolveTemplate(anyString(), any())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(invocationBuilder);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setBalance("10");
        when(invocationBuilder.post(any())).thenReturn(response);

        jobcoinClient.createTransaction(new TransactionRequest());
    }

    @Test
    public void testCreateTransaction_unsuccessfulResponse() {
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.resolveTemplate(anyString(), any())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(invocationBuilder);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getFamily()).thenReturn(Response.Status.Family.CLIENT_ERROR);
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setBalance("10");
        when(invocationBuilder.post(any())).thenReturn(response);

        assertThrows(ClientException.class, () -> jobcoinClient.createTransaction(new TransactionRequest()));
    }

    @Test
    public void testCreateTransaction_runtimeException() {
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.resolveTemplate(anyString(), any())).thenReturn(webTarget);
        when(webTarget.request(anyString())).thenReturn(invocationBuilder);
        when(response.getStatusInfo()).thenReturn(statusType);
        when(statusType.getFamily()).thenReturn(Response.Status.Family.CLIENT_ERROR);
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setBalance("10");
        when(invocationBuilder.post(any())).thenThrow(new RuntimeException());

        assertThrows(ClientException.class, () -> jobcoinClient.createTransaction(new TransactionRequest()));
    }
}
