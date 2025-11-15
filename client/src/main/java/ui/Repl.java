package ui;

import server.ServerFacade;
import ui.client.EvalResult;
import ui.client.GameplayClient;
import ui.client.PostloginClient;
import ui.client.PreloginClient;
import ui.states.ClientStates;

import java.util.Arrays;
import java.util.Scanner;

public class Repl {

    private EvalResult evalResult;

    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;

    public Repl(String serverURL) {
        ServerFacade serverFacade = new ServerFacade(serverURL);

        preloginClient = new PreloginClient(serverFacade);
        postloginClient = new PostloginClient(serverFacade);
        gameplayClient = new GameplayClient(serverFacade);

        evalResult = new EvalResult("", ClientStates.PRELOGIN);
    }

    public void run() {
        System.out.println("Enjoy your time playing chess locally on this machine!");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        while (evalResult.nextState() != ClientStates.QUIT) {
            String input = scanner.nextLine();

            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            evalResult = switch (evalResult.nextState()) {
                case ClientStates.PRELOGIN -> preloginClient.eval(cmd, params);
                case ClientStates.POSTLOGIN -> postloginClient.eval(cmd, params);
                case ClientStates.GAMEPLAY -> gameplayClient.eval(cmd, params);
                case ClientStates.QUIT -> new EvalResult("", ClientStates.QUIT);
            };

            System.out.println("Thanks for playing chess!");
        }
        System.out.println();
    }
}
