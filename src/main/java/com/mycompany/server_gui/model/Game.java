/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.model;

/**
 *
 * @author DELL
 */
public class Game {
    
    private int playerXId;
    private int playerOId;
    private int winnerId;
    private String movesData;

    public Game(int playerXId, int playerOId, int winnerId, String movesData) {
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.winnerId = winnerId;
        this.movesData = movesData;
    }

    public int getPlayerXId() {
        return playerXId;
    }

    public void setPlayerXId(int playerXId) {
        this.playerXId = playerXId;
    }

    public int getPlayerOId() {
        return playerOId;
    }

    public void setPlayerOId(int playerOId) {
        this.playerOId = playerOId;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public String getMovesData() {
        return movesData;
    }

    public void setMovesData(String movesData) {
        this.movesData = movesData;
    }

}
