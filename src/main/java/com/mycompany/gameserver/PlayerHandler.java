package com.mycompany.gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class PlayerHandler extends Thread {

    Socket socket;
    BufferedReader br;
    PrintStream ps;
    PlayerHandler opponent;
    String symbol;

    public PlayerHandler(Socket socket, String symbol) throws IOException {
        this.socket = socket;
        this.symbol = symbol;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ps = new PrintStream(socket.getOutputStream());
        ps.println(symbol);
    }

    void setOpponent(PlayerHandler opponent) {
        this.opponent = opponent;
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = br.readLine()) != null) {
                opponent.ps.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Player disconnected: " + symbol);
        } finally {
            close();
        }
    }

    void close() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}

