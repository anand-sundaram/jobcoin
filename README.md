#### Requirements

- Apache Maven 3.6.3
- Java 8

#### Run

To run the command line interface for the mixer, run the following command from the terminal:
```shell
mvn compile exec:java -Dexec.mainClass="MixerCLI"
```

To run the unit tests (might take 10-15s):
```shell
mvn test
```

#### Project Structure

```
├── pom.xml                                             - dependencies
├── src
│   ├── main
│   │   ├── java
│   │   │   └── MixerCLI.java                           - main class, exposing the command line interface
│   │   │   ├── Mixer.java                              - creates deposit address and performs mixing when the transaction comes in
│   │   │   ├── TransactionManager.java                 - interface for performing a transaction and listening to an address for transaction
│   │   │   │── TransactionManagerImpl.java             - implements TransactionManager interface
│   │   │   ├── TransactionNotificationDelegate.java    - interface for notifying when a transaction comes in, implemented by Mixer
│   │   │   └── calculator                              - handles the business logic for amount/fee calculation
│   │   │   │   ├── AmountDistributionCalculator.java   - determines how to split the amount between different addresses
│   │   │   │   ├── FeeManager.java                     - deducts fee from the transaction amount
│   │   │   └── client
│   │   │       └── JobcoinClient.java                  - client to wrap around the jobcoin API endpoints
│   │   │   └── model                                   - contains the entity POJOs for the API request/response
│   └── test
│       └── java                                        - contains the unit tests and one end-to-end test

```


#### Explanation for Mixer and TransactionManager

The `Mixer` class is the primary class orchestrating the mixing of jobcoins. It generates a deposit address for each request, and maintains the mapping to the user's addresses to which the coins need to be forwarded.
When it is notified of a transaction to the deposit address, it deducts a fee and transfers the amount to the different addresses associated with the deposit address.

The `TransactionManager` interface exposes methods for performing a transaction and listening for a transaction event on a particular address.
This interface is implemented by `TransactionManagerImpl`. The goal was to decouple the mixing orchestration from logic for dealing with transactions.
In my implementation, the `TransactionManager` listens for updates by continuously polling the `GET /addresses/{address_id}` endpoint.
But this could be replaced with a message subscription model in the future.

When a transaction for the deposit address does come in, the `TransactionManagerImpl` would need to notify the `Mixer` about the transaction, so that the `Mixer` can distribute the amount between the user provided addresses.
However, this creates a circular dependency. In order to overcome the circular dependency, I decided to create a `TransactionNotificationDelegate` interface that could be invoked by the `TransactionManagerImpl` to notify whenever it finds out about a transaction on a relevant address.

For the purpose of this assignment, I made the `Mixer` class directly implement `TransactionNotificationDelegate`, but in the future, we might want to separate TransactionManagement and Mixing in two separate components, in which case the `TransactionNotificationDelegate` could make an API call or publish a message to notify the Mixing Component.

The `Mixer` and `TransactionManagerImpl` don't directly depend on each other, they instead rely on the invoking class (`MixerCLI` in this case) to supply them with the dependencies in a crude form of dependency injection.


#### Assumptions:
- Any non-zero balance in the deposit address indicates the new in-flow of funds
- Any fees charged can remain in the house address
- Limiting precision to two decimal places just for ease of implementation and testing, although
  that may not be realistic for some cryptocurrencies
  
- There is no need for persistence, it is enough to store the address mappings in-memory
- We can stop listening for transaction into the deposit address after a reasonable timeout (30 seconds after user requests deposit address, in my current implementation)


#### Quick Fixes:
- Use Lombok for getters/setters in POJOs so that the model code is less verbose
- Move the end-to-end test (`MixerE2eTest`) to a separate module instead of clubbing it with unit tests
- Move constants such as api endpoints, house address, fee percentage, amount distribution type and transaction timeout to config
- Better input validation (for example when the `Mixer` is notified of a transaction event, it should verify the transaction details instead of blindly trusting the client)


#### Long-term Improvements:
- Move the mixing logic to a separate service, and the CLI can call endpoints on that service to perform the mixing
- For transfer errors and error fetching transaction updates, introduce a retry queue
- To further obfuscate the mixing, the transactions should be performed after random intervals of time
  instead of sequentially
- Introduce parallelization after observing the performance of the code under load
- Add instrumentation so we can measure metrics such as failure rates for endpoint calls, waiting time to receive a transaction updates


