package server.EndpointBodies;

import chess.ChessGame;

public record LoginBody(String username, String password) {
}