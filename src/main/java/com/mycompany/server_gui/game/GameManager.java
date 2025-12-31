/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.game;

/**
 *
 * @author DELL
 */

import java.util.Vector;

import java.util.concurrent.ConcurrentHashMap;

import com.mycompany.server_gui.network.PlayerHandler;

public class GameManager {

    private static GameManager instance;

    // Thread-safe map to store online players (Username -> Handler)
    private final ConcurrentHashMap<String, PlayerHandler> onlinePlayers = new ConcurrentHashMap<>();

    // Thread-safe list to track active games
    private final Vector<GameRoom> activeGames = new Vector<>();

    private GameManager() {
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // --- Player Management ---
    public void addOnlinePlayer(PlayerHandler player) {
        if (player.getUsername() != null) {
            onlinePlayers.put(player.getUsername(), player);
            broadcastPlayerList();
        }
    }

    public void removePlayer(PlayerHandler player) {
        if (player.getUsername() != null) {
            onlinePlayers.remove(player.getUsername());
            broadcastPlayerList();
        }
    }

    // THE MISSING METHOD
    public PlayerHandler getHandlerByName(String username) {
        return onlinePlayers.get(username);
    }

    public int getPlayersCount() {
        return onlinePlayers.size();
    }

    // --- Broadcasting ---
    public void broadcastPlayerList() {
        // Format: PLAYER_LIST:user1,score,status;user2,score,status;...
        StringBuilder sb = new StringBuilder("PLAYER_LIST:");
        for (PlayerHandler p : onlinePlayers.values()) {
            sb.append(p.getUsername()).append(",")
                    .append(p.getScore()).append(",")
                    .append(p.getStatus()).append(";");
        }

        for (PlayerHandler p : onlinePlayers.values()) {
            p.sendMessage(sb.toString());
        }
    }

    // --- Logic & Routing ---
    public void handleInvitation(String senderUsername, String targetUsername) {
        PlayerHandler target = onlinePlayers.get(targetUsername);
        if (target != null) {
            if (target.getStatus().equals("IDLE")) {
                target.sendMessage("RECEIVE_INVITE:" + senderUsername);
            } else {
                PlayerHandler sender = onlinePlayers.get(senderUsername);
                if (sender != null) {
                    sender.sendMessage("INVITE_FAIL:User is busy");
                }
            }
        }
    }

    public void startGame(String player1Name, String player2Name) {
        PlayerHandler p1 = onlinePlayers.get(player1Name);
        PlayerHandler p2 = onlinePlayers.get(player2Name);

        if (p1 != null && p2 != null) {
            GameRoom room = new GameRoom(p1, p2);
            activeGames.add(room);
            room.startGame();
        }
    }

    public void removeGame(GameRoom room) {
        activeGames.remove(room);
    }
}
