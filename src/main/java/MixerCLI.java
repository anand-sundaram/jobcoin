import client.JobcoinClient;

import javax.ws.rs.client.ClientBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MixerCLI {

    private static String prompt = "Please enter a comma-separated list of new, unused Jobcoin addresses where your mixed Jobcoins will be sent.";

    private static String helpText = "Jobcoin Mixer\n" +
            "\n" +
            "Takes in at least one return address as parameters (where to send coins after mixing). Returns a deposit address to send coins to.\n" +
            "\n" +
            "Usage:\n" +
            "    run return_addresses...";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        TransactionManagerImpl transactionManager = new TransactionManagerImpl();
        JobcoinClient jobcoinClient = new JobcoinClient(ClientBuilder.newClient());
        Mixer mixer = new Mixer(transactionManager);
        transactionManager.setup(mixer, jobcoinClient);

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
                List<String> addresses = Arrays.asList(line.split(","));
                String depositAddress = mixer.createDepositAddress(addresses);
                System.out.printf("You may now send Jobcoins to address %s. They will be mixed and sent to your destination addresses.%n", depositAddress);
                // add validation for number of addresses
            }
        } catch (CompletedException completedException) {
            System.out.println("Quitting...");
        }
    }

    static class CompletedException extends Exception {

    }
}
