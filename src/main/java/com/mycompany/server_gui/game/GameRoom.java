/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.game;

/**
 *
 * @author DELL
 */
import com.mycompany.server_gui.dao.PlayerDao;
import com.mycompany.server_gui.model.Player.PlayerStatus;
import com.mycompany.server_gui.network.PlayerHandler;

import com.mycompany.server_gui.utils.PlayerSymbol;
import java.sql.SQLException;
import java.util.List;
import javafx.util.Pair;

public class GameRoom {

    private final PlayerHandler playerX;
    private final PlayerHandler playerO;
    private final XOGameLogic gameLogic;

    private PlayerSymbol currentTurn;
    private final PlayerDao playerDao;

    public GameRoom(PlayerHandler p1, PlayerHandler p2) {
        this.playerX = p1;
        this.playerO = p2;
        this.gameLogic = new XOGameLogic();

        this.currentTurn = PlayerSymbol.X;
        this.playerDao = new PlayerDao();

        // Update Status to busy
        playerX.setStatus(PlayerStatus.PLAYING);
        playerO.setStatus(PlayerStatus.PLAYING);

        // Link players to this room
        playerX.setGameRoom(this);
        playerO.setGameRoom(this);

        // Refresh lobby for others (so these two show as Busy)
        GameManager.getInstance().broadcastPlayerList();
    }

    public void startGame() {
        // Notify clients to open Game Board
        playerX.setPlayerSymbol(PlayerSymbol.X);
        playerX.sendMessage("GAME_START:X:" + playerO.getUsername());

        playerO.setPlayerSymbol(PlayerSymbol.O);
        playerO.sendMessage("GAME_START:O:" + playerX.getUsername());
    }

    public void handleMove(PlayerHandler sender, int row, int col) {
        PlayerSymbol senderSymbol = sender.getPlayerSymbol();

        if (senderSymbol != currentTurn) {
            return;
        }

        // 1. Validate Move using your XOGameLogic
        if (gameLogic.makeMove(row, col, senderSymbol)) {

            // 2. Broadcast Valid Move to BOTH players
            // Format: MOVE_VALID:row:col:symbol
            String moveMsg = "MOVE_VALID:" + row + ":" + col + ":" + senderSymbol;
            playerX.sendMessage(moveMsg);
            playerO.sendMessage(moveMsg);

            // 3. Check Game State
            if (gameLogic.hasPlayerWon(sender.getPlayerSymbol())) {

                updateWinnerScore(sender);

                // 1. Get the coordinates String (e.g., "0,0;0,1;0,2;")
                String winCoords = getWinningCoordsAsString();

                // 2. Send to WINNER: GAME_OVER:WIN:Score:Coords
                sender.sendMessage("GAME_OVER:WIN:" + sender.getPlayer().getScore() + ":" + winCoords);

                // 3. Send to LOSER: GAME_OVER:LOSE:Coords
                getOpponent(sender).sendMessage("GAME_OVER:LOSE:" + winCoords);

               
                // 1. Get current steps
                String allSteps = gameLogic.getSteps();

                // 2. Append the winner symbol and WIN flag
                String stepsWithWinner = allSteps + sender.getPlayerSymbol() + ":WIN";

                // 3. Send the MODIFIED string
                sender.sendMessage("SAVE_REPLAY_DATA:" + stepsWithWinner);
                getOpponent(sender).sendMessage("SAVE_REPLAY_DATA:" + stepsWithWinner);
                
                closeRoom();
            } else if (gameLogic.isDraw()) {

                updateDrawScore();

                playerX.sendMessage("GAME_OVER:DRAW:" + playerX.getScore());
                playerO.sendMessage("GAME_OVER:DRAW:" + playerO.getScore());
                //save game
                String allSteps = gameLogic.getSteps();
                playerX.sendMessage("SAVE_REPLAY_DATA:" + allSteps);
                playerO.sendMessage("SAVE_REPLAY_DATA:" + allSteps);

                closeRoom();
            } else {
                switchTurn();
            }
        }
    }

    private void updateWinnerScore(PlayerHandler winner) {
        try {
            int newScore = winner.getScore() + 10;
            playerDao.updateScore(winner.getPlayer().getId(), newScore);
            winner.setScore(newScore);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateDrawScore() {
        try {
            int newScoreX = playerX.getScore() + 5;
            playerDao.updateScore(playerX.getPlayer().getId(), newScoreX);
            playerX.setScore(newScoreX);

            int newScoreO = playerO.getScore() + 5;
            playerDao.updateScore(playerO.getPlayer().getId(), newScoreO);
            playerO.setScore(newScoreO);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void switchTurn() {
        currentTurn = (currentTurn == PlayerSymbol.X) ? PlayerSymbol.O : PlayerSymbol.X;
    }

    public void handleDisconnect(PlayerHandler disconnectedPlayer) {
        PlayerHandler survivor = getOpponent(disconnectedPlayer);
        if (survivor != null) {
            survivor.sendMessage("GAME_OVER:OPPONENT_LEFT");
        }
        closeRoom();
    }

    public PlayerHandler getOpponent(PlayerHandler p) {
        return p == playerX ? playerO : playerX;
    }

    private String getWinningCoordsAsString() {
        List<Pair<Integer, Integer>> coords = gameLogic.getWinningCoords();
        if (coords == null || coords.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Pair<Integer, Integer> p : coords) {
            // Format: row,col;
            sb.append(p.getKey()).append(",").append(p.getValue()).append(";");
        }
        return sb.toString();
    }

    private void closeRoom() {
        // Reset players to IDLE
        playerX.setStatus(PlayerStatus.IDLE);
        playerO.setStatus(PlayerStatus.IDLE);

        playerX.setGameRoom(null);
        playerO.setGameRoom(null);

        // Remove from manager
        GameManager.getInstance().removeGame(this);
        GameManager.getInstance().broadcastPlayerList();
    }

}
