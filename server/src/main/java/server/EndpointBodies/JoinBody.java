package server.EndpointBodies;

import chess.ChessGame;

public record JoinBody(ChessGame.TeamColor playerColor, int gameID) {
}