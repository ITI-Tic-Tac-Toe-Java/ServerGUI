/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.model;

/**
 *
 * @author DELL
 */
public class Player {

    private int id;
    private String username;
    private String password;
    private int score;
    private PlayerStatus status;

    public enum PlayerStatus {OFFLINE,IDLE,PLAYING}

    public Player() {
    }

    public Player(String username, String password, int score, PlayerStatus status) {
        this.username = username;
        this.password = password;
        this.score = score;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }
}
