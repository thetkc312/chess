package ui;

import server.ServerFacade;
import ui.client.Client;
import ui.client.GameplayClient;
import ui.client.PostloginClient;
import ui.client.PreloginClient;
import ui.states.UiStates;

import java.util.Arrays;
import java.util.Scanner;

public class Repl {

    public static UiStates clientState;

    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;

    public Repl(String serverURL) {
        ServerFacade serverFacade = new ServerFacade(serverURL);

        preloginClient = new PreloginClient(serverFacade);
        postloginClient = new PostloginClient(serverFacade);
        gameplayClient = new GameplayClient(serverFacade);

        clientState = UiStates.PRELOGIN;
    }

    public void run() {
        System.out.println("Enjoy your time playing chess locally on this machine!");

        Scanner scanner = new Scanner(System.in);
        Client activeClient = preloginClient;
        String result = "";
        String input = "help";
        while (!result.equals("quit")) {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            activeClient = switch (clientState) {
                case UiStates.PRELOGIN -> preloginClient;
                case UiStates.POSTLOGIN -> postloginClient;
                case UiStates.GAMEPLAY -> gameplayClient;
            };

            activeClient.eval(cmd, params);
            clientState = activeClient.nextState;

            input = scanner.nextLine();
        }
        System.out.println();
    }
}
