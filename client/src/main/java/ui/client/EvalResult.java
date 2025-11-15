package ui.client;

import ui.states.ClientStates;

public record EvalResult (String result, ClientStates nextState){
}
