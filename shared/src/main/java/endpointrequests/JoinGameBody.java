package endpointrequests;

import chess.ChessGame;

public record JoinGameBody(int gameID, ChessGame.TeamColor playerColor) {
}