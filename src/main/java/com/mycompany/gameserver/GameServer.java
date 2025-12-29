package com.mycompany.gameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    ServerSocket serverSocket;
    PlayerHandler playerX;
    PlayerHandler playerO;

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    void init() throws IOException {
        Socket s1 = serverSocket.accept();
        playerX = new PlayerHandler(s1, "X");

        Socket s2 = serverSocket.accept();
        playerO = new PlayerHandler(s2, "O");
        
        playerX.start();
        playerO.start();
        
        playerX.setOpponent(playerO);
        playerO.setOpponent(playerX);
    }

    public static void main(String[] args) throws IOException {
        GameServer gs = new GameServer(5004);
        gs.init();
    }
}
