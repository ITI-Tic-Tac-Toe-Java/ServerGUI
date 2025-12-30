package com.mycompany.server_gui.network;

import com.mycompany.server_gui.utils.OnErrorListener;
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

    public PlayerHandler(Socket socket, String symbol,OnErrorListener errorListener) {
        this.socket = socket;
        this.symbol = symbol;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
           errorListener.onError(ex);
        }finally{
            ps.println(symbol);
        }
    }

    public void setOpponent(PlayerHandler opponent) {
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
            closeResources();
        }
    }

    private void closeResources() {
        try {
            socket.close();
            ps.close();
            br.close();
        } catch (IOException e) {
        }
    }
}
