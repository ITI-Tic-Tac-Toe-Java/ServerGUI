package com.mycompany.server_gui.network;

import com.mycompany.server_gui.game.GameManager;
import com.mycompany.server_gui.game.GameRoom;
import com.mycompany.server_gui.model.Player;
import com.mycompany.server_gui.model.Player.PlayerStatus;
import com.mycompany.server_gui.utils.OnErrorListener;
import com.mycompany.server_gui.utils.PlayerSymbol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class PlayerHandler extends Thread {
    private final Socket socket;
    private BufferedReader br;
    private PrintStream ps;
    private final OnErrorListener errorListener;

    // Player State
    private GameRoom currentRoom;
    private PlayerSymbol symbol;
    private Player player;

    public PlayerHandler(Socket socket, OnErrorListener errorListener) {
        this.socket = socket;
        this.errorListener = errorListener;

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
            errorListener.onError(ex);
        }
    }

    @Override
    public void run() {
        String msg;
        try {
            while ((msg = br.readLine()) != null) {
                ServerProtocol.processMessage(msg, this);
            }
        } catch (IOException ex) {
            errorListener.onError(new IOException("Problem in receving message : " + ex.getLocalizedMessage()));
        } finally {
            cleanup();
        }
    }

    public void sendMessage(String msg) {
        if (ps != null) {
            ps.println(msg);
            ps.flush();
        }
    }

    private void cleanup() {
        GameManager.getInstance().removePlayer(this);
        if (currentRoom != null) {
            currentRoom.handleDisconnect(this);
        }
        closeResources();
    }

    public void closeResources() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            errorListener.onError(new IOException("Error closing resources: " + e.getMessage()));
        }
    }

    // --- Getters & Setters ---
    public String getUsername() {
        return player.getUsername();
    }

    public void setUsername(String username) {
        player.setUsername(username);
    }

    public int getScore() {
        return player.getScore();
    }

    public void setScore(int score) {
        player.setScore(score);
    }

    public PlayerStatus getStatus() {
        return player.getStatus();
    }

    public void setStatus(PlayerStatus status) {
        player.setStatus(status);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }

    public GameRoom getGameRoom() {
        return currentRoom;
    }

    public void setGameRoom(GameRoom room) {
        this.currentRoom = room;
    }

    public PlayerSymbol getPlayerSymbol() {
        return symbol;
    }

    public void setPlayerSymbol(PlayerSymbol symbol) {
        this.symbol = symbol;
    }
}
