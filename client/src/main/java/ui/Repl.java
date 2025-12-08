package ui;

import server.ServerFacade;
import server.websocket.ActiveGameTracker;
import ui.client.EvalResult;
import ui.client.GameplayClient;
import ui.client.PostloginClient;
import ui.client.PreloginClient;
import ui.states.ClientStates;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.Scanner;

public class Repl {

    private EvalResult evalResult;

    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;

    public Repl(String serverURL) {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        ActiveGameTracker activeGameTracker = new ActiveGameTracker(0, null);

        preloginClient = new PreloginClient(serverFacade);
        postloginClient = new PostloginClient(serverFacade, activeGameTracker);
        gameplayClient = new GameplayClient(serverFacade, activeGameTracker);

        evalResult = new EvalResult("", ClientStates.PRELOGIN);
    }

    public void run() throws ConnectException {
        ConsolePrinter.safePrint("Enjoy your time playing chess locally on this machine!\n");

        Scanner scanner = new Scanner(System.in);
        ClientStates lastState = null;
        while (evalResult.nextState() != ClientStates.QUIT) {
            // When there is a state transition, give the help command
            String input;
            if (lastState != evalResult.nextState()) {
                input = "help";
            } else {
                input = scanner.nextLine();
            }

            lastState = evalResult.nextState();

            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            evalResult = switch (evalResult.nextState()) {
                case ClientStates.PRELOGIN -> preloginClient.eval(cmd, params);
                case ClientStates.POSTLOGIN -> postloginClient.eval(cmd, params);
                case ClientStates.GAMEPLAY -> gameplayClient.eval(cmd, params);
                case ClientStates.QUIT -> new EvalResult("Press enter again to confirm you would like to quit", ClientStates.QUIT);
            };

            ConsolePrinter.safePrint(evalResult.result());
        }
        ConsolePrinter.safePrint("Thanks for playing chess!\n");
    }
}
