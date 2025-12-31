package com.mycompany.server_gui.network;

import com.mycompany.server_gui.game.GameManager;
import com.mycompany.server_gui.utils.OnErrorListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.application.Platform;

public class GameServer extends Thread {

    private volatile boolean keepRunning;
    private OnErrorListener errorListener;
    private Consumer<Integer> onNewClientConnection;
    private ServerSocket serverSocket;

    public GameServer(int port, OnErrorListener errorListener, Consumer<Integer> onNewClientConnection) {
        this.onNewClientConnection = onNewClientConnection;
        this.errorListener = errorListener;
        this.keepRunning = true; // IMPORTANT: Initialize true

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
          
                Socket s = serverSocket.accept();

                PlayerHandler newPlayer = new PlayerHandler(s, errorListener);
                newPlayer.start();

                
                onNewClientConnection.accept(GameManager.getInstance().getPlayersCount());

            } catch (IOException ex) {
                if (keepRunning) {
                    errorListener.onError(ex);
                }
            }
        }
    }

    public void closeServer() {
        keepRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
        } catch (IOException ex) {
            errorListener.onError(ex);
        }
    }
}
