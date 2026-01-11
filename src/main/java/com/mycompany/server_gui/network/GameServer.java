package com.mycompany.server_gui.network;

import com.mycompany.server_gui.game.GameManager;
import com.mycompany.server_gui.utils.Functions;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class GameServer extends Thread {
    
    private PlayerHandler newPlayer;
    private volatile boolean keepRunning;
    
    private Consumer<Integer> onNewClientConnection;
    private ServerSocket serverSocket;
    
    public GameServer(int port, Consumer<Integer> onNewClientConnection) {
        this.onNewClientConnection = onNewClientConnection;
        this.keepRunning = true;
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Functions.showErrorAlert(ex);
        }
    }
    
    @Override
    public void run() {
        while (keepRunning) {
            try {
                Socket s = serverSocket.accept();
                
                newPlayer = new PlayerHandler(s);
                newPlayer.start();
                
                onNewClientConnection.accept(GameManager.getInstance().getPlayersCount());
                
            } catch (IOException ex) {
                if (keepRunning) {
                    Functions.showErrorAlert(ex);
                    if (newPlayer != null) {
                        newPlayer.sendMessage("ERROR:SERVER_DISCONNECT");
                    }
                }
            }
        }
    }
    
    public void closeServer() {
        keepRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                newPlayer.sendMessage("ERROR:SERVER_DISCONNECT");
            }
            
        } catch (IOException ex) {
            Functions.showErrorAlert(ex);
        }
    }
}
