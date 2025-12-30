package com.mycompany.server_gui.network;

import com.mycompany.server_gui.network.PlayerHandler;
import com.mycompany.server_gui.utils.OnErrorListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer extends Thread {

    boolean keepRunning;
    OnErrorListener errorListener;
    ServerSocket serverSocket;
    PlayerHandler playerX;
    PlayerHandler playerO;

    public GameServer(int port, OnErrorListener errorListener) {
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
                playerX = new PlayerHandler(s1, "X", errorListener);
            } catch (IOException ex) {
                if (keepRunning) {
                    errorListener.onError(ex);
                }
            }

            try {
                Socket s2 = serverSocket.accept();
                playerO = new PlayerHandler(s2, "O", errorListener);
            } catch (IOException ex) {
                if (keepRunning) {
                    errorListener.onError(ex);
                }
            }

            playerX.start();
            playerO.start();

            playerX.setOpponent(playerO);
            playerO.setOpponent(playerX);
        }
    }

    public void closeServer() {
        keepRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                errorListener.onError(ex);
            }
        }
    }
}
