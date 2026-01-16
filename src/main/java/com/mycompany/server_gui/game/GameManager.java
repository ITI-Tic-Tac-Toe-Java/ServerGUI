package com.mycompany.server_gui.game;

import com.mycompany.server_gui.model.Player.PlayerStatus;
import com.mycompany.server_gui.network.PlayerHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;

public class GameManager {

    private static GameManager instance;

    private final ConcurrentHashMap<String, PlayerHandler> onlinePlayers = new ConcurrentHashMap<>();

    private final Vector<GameRoom> activeGames = new Vector<>();
    private Runnable uiUpdateCallback;

    private GameManager() {
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public ConcurrentHashMap<String, PlayerHandler> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setUiUpdateCallback(Runnable callback) {
        this.uiUpdateCallback = callback;
    }

    private void notifyUI() {
        if (uiUpdateCallback != null) {
            Platform.runLater(uiUpdateCallback);
        }
    }

    public void addOnlinePlayer(PlayerHandler player) {
        if (player != null && player.getUsername() != null) {
            onlinePlayers.put(player.getUsername(), player);
            broadcastPlayerList();
            notifyUI();
        }
    }

    public void removePlayer(PlayerHandler player) {
        if (player != null && player.getUsername() != null) {
            onlinePlayers.remove(player.getUsername());
            broadcastPlayerList();
            notifyUI();
        }
    }

    public PlayerHandler getHandlerByName(String username) {
        return onlinePlayers.get(username);
    }

    public int getPlayersCount() {
        return onlinePlayers.size();
    }

    public void broadcastPlayerList() {
        List<PlayerHandler> values = new ArrayList<>(onlinePlayers.values());
        values.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        StringBuilder sb = new StringBuilder("PLAYER_LIST:");
        for (PlayerHandler p : values) {
            sb.append(p.getUsername()).append(",")
                    .append(p.getScore()).append(",")
                    .append(p.getStatus()).append(";");
        }

        for (PlayerHandler p : onlinePlayers.values()) {
            p.sendMessage(sb.toString());
        }
    }

    public void handleInvitation(String senderUsername, String targetUsername) {
        PlayerHandler target = onlinePlayers.get(targetUsername);

        if (target != null) {
            if (target.getStatus().equals(PlayerStatus.IDLE)) {
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
            notifyUI();
        }
    }

    public void removeGame(GameRoom room) {
        activeGames.remove(room);
        notifyUI();
    }

    public int getOnlineCount() {
        return onlinePlayers.size();
    }

    public int getInGameCount() {
        int count = 0;
        for (PlayerHandler handler : onlinePlayers.values()) {
            if (handler.getGameRoom() != null) {
                count++;
            }
        }
        return count;
    }
}
