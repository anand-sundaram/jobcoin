import model.AddressInfo;
import model.TransactionRequest;

import javax.ws.rs.client.ClientBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class JobcoinMixer {

    private static String prompt = "Please enter a comma-separated list of new, unused Jobcoin addresses where your mixed Jobcoins will be sent.";

    private static String helpText = "Jobcoin Mixer\n" +
            "\n" +
            "Takes in at least one return address as parameters (where to send coins after mixing). Returns a deposit address to send coins to.\n" +
            "\n" +
            "Usage:\n" +
            "    run return_addresses...";

    private static String houseAddress = "House";

    private static JobcoinClient jobcoinClient = new JobcoinClient(ClientBuilder.newClient());

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            while (true) {
                System.out.println(prompt);
                String line = sc.nextLine();
                if (line.equalsIgnoreCase("quit")) {
                    throw new CompletedException();
                }
                if (line.trim().isEmpty()) {
                    System.out.println("You must specify empty addresses to mix into!\n" + helpText);
                    continue;
                }
                String depositAddress = UUID.randomUUID().toString();
                System.out.printf("You may now send Jobcoins to address %s. They will be mixed and sent to your destination addresses.%n", depositAddress);
                List<String> addresses = Arrays.asList(line.split(","));
                // add validation for number of addresses
                mix(depositAddress, addresses);
            }
        } catch (CompletedException completedException) {
            System.out.println("Quitting...");
        } catch (ClientException clientException) {
            System.out.println("There was an error with an API call, terminating the program");
        }
    }

    private static void mix(String depositAddress, List<String> addresses) throws ClientException {
        boolean isTransferredToDepositAddress = false;
        long startTime = System.currentTimeMillis();
        while (!isTransferredToDepositAddress && (System.currentTimeMillis() - startTime < 30000)) {
            AddressInfo addressInfo = jobcoinClient.getAddressInfo(depositAddress);
            BigDecimal balance = new BigDecimal(addressInfo.getBalance());
            if (balance.equals(BigDecimal.ZERO)) {
                continue;
            }
            isTransferredToDepositAddress = true;
            transfer(depositAddress, houseAddress, balance.toString());
            distributeToAddresses(balance, addresses);
        }
    }

    private static void distributeToAddresses(BigDecimal amount, List<String> addresses) {
        BigDecimal transferIncrement = amount.divide(BigDecimal.valueOf(addresses.size()), RoundingMode.DOWN);
        for (int x = 0; x < addresses.size(); x++) {
            if (x == addresses.size() - 1) {
                transfer(houseAddress, addresses.get(x), amount.toString());
            } else {
                transfer(houseAddress, addresses.get(x), transferIncrement.toString());
                amount = amount.subtract(transferIncrement);
            }
        }
    }

    private static void transfer(String fromAddress, String toAddress, String amount) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(amount);
        transactionRequest.setFromAddress(fromAddress);
        transactionRequest.setToAddress(toAddress);
        jobcoinClient.createTransaction(transactionRequest);
    }

    static class CompletedException extends Exception {

    }
}
