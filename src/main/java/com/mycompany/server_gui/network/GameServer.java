package com.mycompany.server_gui.network;

import com.mycompany.server_gui.network.PlayerHandler;
import com.mycompany.server_gui.utils.OnErrorListener;
import com.mycompany.server_gui.utils.PlayerSymbol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class GameServer extends Thread {

    private static final ArrayList<PlayerHandler> players = new ArrayList();
    boolean keepRunning = true;
    OnErrorListener errorListener;
    Consumer<Integer> onNewClientConnection;
    ServerSocket serverSocket;
    PlayerHandler player;

    public GameServer(int port, OnErrorListener errorListener, Consumer<Integer> onNewClientConnection) {
        this.onNewClientConnection = onNewClientConnection;
        this.errorListener = errorListener;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            errorListener.onError(ex);
        }
    }

    @Override
    public void run() {
        while (keepRunning) {
            try {
                Socket s1 = serverSocket.accept();
                player = new PlayerHandler(s1, this, PlayerSymbol.X, errorListener);
                player.start();
                players.add(player);
                onNewClientConnection.accept(getNumberOfPlayers());
            } catch (IOException ex) {
                if (keepRunning) {
                    errorListener.onError(new IOException("Internal Server Error :" + ex.getLocalizedMessage()));
                }
            }
        }
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void closeServer() {
        keepRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                errorListener.onError(new IOException("Error in closing socket" + ex.getLocalizedMessage()));
            }
        }
    }
}
