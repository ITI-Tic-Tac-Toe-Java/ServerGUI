/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.game;

/**
 *
 * @author DELL
 */
import com.mycompany.server_gui.model.Player.PlayerStatus;
import com.mycompany.server_gui.network.PlayerHandler;

import com.mycompany.server_gui.utils.PlayerSymbol;

public class GameRoom {

    private final PlayerHandler playerX;
    private final PlayerHandler playerO;
    private final XOGameLogic gameLogic;

    private PlayerSymbol currentTurn;

    public GameRoom(PlayerHandler p1, PlayerHandler p2) {
        this.playerX = p1;
        this.playerO = p2;
        this.gameLogic = new XOGameLogic();

        this.currentTurn = PlayerSymbol.X;

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
            if (gameLogic.hasPlayerWon(sender)) {
                sender.sendMessage("GAME_OVER:WIN");
                getOpponent(sender).sendMessage("GAME_OVER:LOSE");
                //save game
                closeRoom();
            } else if (gameLogic.isDraw()) {
                playerX.sendMessage("GAME_OVER:DRAW");
                playerO.sendMessage("GAME_OVER:DRAW");
                //save game
                closeRoom();
            } else {
                switchTurn();
            }
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

    private PlayerHandler getOpponent(PlayerHandler p) {
        return p == playerX ? playerO : playerX;
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
