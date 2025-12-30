package com.mycompany.server_gui.network;

import com.mycompany.server_gui.utils.OnErrorListener;
import com.mycompany.server_gui.utils.PlayerSymbol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class PlayerHandler extends Thread {

    private Socket socket;
    private BufferedReader br;
    private PrintStream ps;
    private PlayerHandler opponent;
    private GameServer server;
    private PlayerSymbol symbol;
    private OnErrorListener errorListener;

    public PlayerHandler(Socket socket, GameServer server, PlayerSymbol symbol, OnErrorListener errorListener) {
        this.socket = socket;
        this.symbol = symbol;
        this.server = server;
        this.errorListener = errorListener;

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
            errorListener.onError(ex);
        } finally {
            ps.println(symbol);
        }
    }

    public void setOpponent(PlayerHandler opponent) {
        this.opponent = opponent;
    }

    @Override
    public void run() {
        String msg;
        
    }
    
    public void setPlayerSymbol(PlayerSymbol sym){symbol = sym;}

    private void closeResources() {
        try {
            socket.close();
            ps.close();
            br.close();
        } catch (IOException e) {
            errorListener.onError(new IOException("Error in closing resource" + e.getLocalizedMessage()));
        }
    }
}
