# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Server Design Diagram

Using `https://sequencediagram.org/`, a representation of the server (and its interactions with the handler, services and database) was built out.

The following representation was completed 9 Oct 2025.

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YOlAowADWVdSUMNi3BhgIljA+08mGkBgt2AHCBKAAjqkcphhDUYKUKvBkOZ6gBWJxOYbjObqYD2eb1MYAUSg3jqMAAFBw1CAoMQCAUYBAAGYwShkgCUYyu6A412BjwAQiCYBlIWpmMzvDMYCBkaQQOw0BBmDpDMDQTpvgMajDqi8EZViiiYIEMVixjjVHiCcsSWT6npQbdxTluWZOHrnrBEWxONxyeNJlBpnNzgcxhtVNsoLtFss1mMjiczvH3X6uAbKrDRPVblkcpQKRkC2BKGKoWAOV6qKJ4ZVZPIlCp1PV8WAAKoDCnB0MoauNxTKNSqetGHS1ABiSE4MC7lEHMC1PwGff5d0e89gmyQYHiK6ms0MwAQGu+KAAHtkwBpB82R-DETmVPUt4Oa3XDU8avUFKe7hwljtA86BEheKjYAyhSwgaiLGhg9RmsEFpWjaCx2qS0D1L2R4wCeZ4wPI9zoO6vKYHew7qI+2bVLmMBoD4CAIB+KhjhRLaqLUtJ3GWW49gMg4Dto95UYixi1AoHCgm+2jrg8c5oKoPiUaozKMeOTLYJQeQFORwkqdR350cYMAtFpUA6WgMCTimKB8s+hhfjB5LsSOYEQVBNawUa8pgGi5qjJaI5oYS9pYTAzK2aCOovKoSDUhpEDmZZlwenyrmiU+tEvhpZnaVB5DKRxLGOQ2+kcVxwJlgoPh7hSwC1fEgl6U2BliROkmgjVe7vhlo5OdlKD1CZeUWQVZBFSOJWGRmAb1CW17lmojFYLN3nfpQ9RBquR5LISCbLA1e7tBAxFoHtyypc5hnwX5MDhAF22HmGMD7RGR3xCdZ0XfsPKep43h+P40DsPiMSTnARLSHACgwAAMhAWSFLdY7OfUzRtF0vQGOo+RoFiOFzGsUYxhwVw3BuG2wG8ajmF8Px-NwhEQGCIoupWXk+nBvn+ZigWofi6HEph5JUjSdKQXjTKsuy0Bcn9fIU-JwqghWEoRdKsq+bKSoqku6oAUu2r6lAnM3TzpqPUFuKC6FIuOmzEKVqRnrXb67CZoGEw7WGaaJiTOzhlca0+jRIg5QgiMzsWmSLVAas5NWDlseVI5tigYDdfE-HPf2LVDhxY7idOs5Z4uy6E2IPQ9DA7TxNkMBIDM2DcHMGApckEAgPco7sN8kdI3hYBls3N6N6y7B4eASA0HhjXmIQKUgBACWXteqguLX8SGMGMAzP0zAD0ytXS4R29ILAKrb7AONxYyarMtAhg5PQM6OMA9EoJsTJoCgm8nUYFme4G4AEk8JSTsjrXAOhTi7m+ICW4YAfAhjwpbYIEVoAwIgVZWWsBl7UhcHJTcAw8L-hBN8eI+g8JWQ+gvRUBV8FiD6oZByv5GrvmTgNb09Q+ruRQJLXSbtuYlDukhFCwVbYYQdAeWADd8IAX7ojfEoIZxsgQAYTY195gK3ziJfqWVw5DXooxZinCyqtQqgPGcfFgzNWYe1WocBt7dwwbAGS8giHwyjlZbi1IMBIBPKODgLNlTMGOGAEA+4P73GVJsKywZdFtQMbWHKDEmLTS4T+GQqd1B8IEdBE25sRGIScAARnETbW0wtpG-A+tAJAAAvOyLt0o5P0WHFJQ1gxgXXhk8xBc07cTkCgLO9V2HaCEhYh8DjOowDLrJJWm5FKTXUGpBAiVkqeXsck4yOhTKbKljZU4dkMnJM2tkqZuTwL8M8kInyxSYDoj5tiCRVSwrkkisc6KhS4oJRMklfKUsWmJMLpk3Z+zAWMgmipU5lQQ7zVjoWeOy0ECrQ9gGFhJstre1zj9A6YwPpfRIq9S67o7lIgeQ9Z5OKQy7RJfsRMhLTrErejogGvgAheBQOgGIcREhcp5QjewvgsAozOeSBo0giRwyJO0Ik3Qei3zxsMJlZ1PHXRph8MA9Nan-GZqzZ0TsJRmy-LdXmFTrSSOqeFMWqhaT0iliyNkIt5ZpRNXC9F5zxh1LpA0yyeL0yetDkZHK1JuBlhjqWJaroqx9IuQM1sc89w9JyKoHOtK5iTITf1SoxcZyglof4+hUtIBqsWTAEBaAqAnnikm7eRaUqlvQCC6ZOzUkmNhVTHhbS8m3MKaai2ZpymjGWALN59sGY+sac0mAOjtkdLoh9Dhg0U6XM4kYFA4aRmNXTX2LNeii4TmkJujOx554NoKk2wo5aOzLJUmsjZkLCjzpDUYkaBzGRHO4PZFdmTzm8Oufkk1wiTRPItSFKR4VPnfpgDFG+8VDD-I-VZYFL7WG5WQ4VGFnD3b+nOQtJFGQlKoswCHTF3CYAjCuOSs191HpsurnAZBtxSBNAgHFTyXgOX+FuKCfw2AZyPDhteGAABxI8GgxUdPOQ0UTsqFX2CPCqxqRLr0Cipq8d4dNvi6qZggw27NjU0YtmB-mryhbvPqLa+1+TT64NdWRN2Hq8NeynX6qCAbg5BtRoNeoyAcjiZxJGuOCdY1mPjXo+oH0U03l3UefdbVc0ThLgW89DBG3MrU5TSt1bkCpb3HQjLaq0O+eMeknD0mXI9sA3270RSTRDqxKO8zdsanTEavUppuxZ1uvC31dOYBAtqApAl0FSWEUWFQLPE86zFM4hbZlBdOUZtDY0BV1gQaEVRuRcRsA9UmKrerGRv9gZGhjDmygEB0gFgzjAGsFwD2YCdDWDAB7m9qP9pAwhOjzyzsXauzdjA93HvPZgK9x7bKuNAw4AAdjcE4FATgYhEmCHASGAA2eAVVDBDZgEUbW4r0atA6AppT7XjqZaxBdgAckecm6mNVac+DpxmAIWYajBDG4D9zQNWzHRZid1mJYFSdfZ4F5aVaiiNTkDWEAZRyhETrNAoT9YGqNrBk23OKUNb5y1yD5InSc+djopzG2XPYrc-6+l+Kad0rej147S2jFDLLEN4LhHpdhd-f0yLdaYtpsrqN6Z42Ut1sK5ezLnics1vy-W9LEfittPI50+oaTTG-sJxFlSva8Za9o41kd1tLXjra25mdc6k9guW+MjxfW2n1BdygN3lc1jL0jlAIPi3xy-ikmJo8njb1KXvepJDT6FvtNfcNPZo0UpfpOettGWeOI58EZ9nn33TMvMqQL6R0HIEmTg7BhDmkn2pTIiVwxU+IVjSltC4q634VS+20RlapHvMna2h9urA7KX0bdeyoGSwTdSOb+WIJABIMAIApiCAb+AAKQgFUVx38GSFAHuHx0V0z2aA7Cxh6Au2UwpzOixBbmACAKgDgAgHbzWH+2kHp0pkZ1pmZ0nT1X00di52MweU3yLwg2tVFmpDtWF0dRlhdXF3U0l1C1l3l21kVGVz1jVDV2XDgzz0HV1231a3CkN0MzdBN37Wc09gtw619Stzen9gQBIMoHIPbwDUTGoIDQd3fyd3qAACsEC0A3cCMyxkUY0k5vcl804-crxU04tM1x9D0px80w8L0S1I9y1o88sIj48ojE811k86I09O1F8AMPJc92Cddh0RhmtVD9dsJyd4hOty83UL8U861l1DFV1s16hmR803dqDW8KC5YQiHFQ8LtPFcdlQYBI4HAXhgBq1ThdBuAOi20jE0iF8sVfCrksjV9v8vtREnBkJC9+c1DyR5FyFCIQJzoetz9K9Ji2wjwajOkZpNsn8QsUU0UXNk8vUv84Qf8TQqUsRIdAZOULJvgwCICvASCkQ7hYBgBsAW5F4Cp0DzAfMKNJVpVZV5VehjB1VClNVtMmC9N2cDNPclCOCVDi8d8bU+CbMRchDORgVTdH8w1T1Bw9ADA387jjiN0t1qT9AUB3cPDQtvDajER+tGSqTtAaTWTO8c1u9EBASjB+SWSB870OIH1R8b9dJKjwUZ8Co58f0uTM9Miblsi19tcN9cTuDLMIoooNc6tflEM9kAV5SUMK9kiq831p9MM78poH9Lj3Clpds6TPZ7jAxHj1paNXjRgdEgA