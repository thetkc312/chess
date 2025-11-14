package server.EndpointBodies;

import chess.ChessGame;

public record RegisterBody(String username, String password, String email) {
}