/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.dao;

/**
 *
 * @author DELL
 */
import com.mycompany.server_gui.db.DBUtil;
import com.mycompany.server_gui.model.Player;
import java.sql.*;

public class PlayerDao {

    
    public Player login(String username, String password) throws SQLException {
        String query = "SELECT * FROM player WHERE username = ? AND password = ?";

        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, username);
        ps.setString(2, password);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("player_id"));
                p.setUsername(rs.getString("username"));
                p.setPassword(rs.getString("password"));
                p.setScore(rs.getInt("score"));
                return p;
            }
        }

        return null;
    }

    public boolean register(Player player) throws SQLException {
        String query = "INSERT INTO player (username, password, score, status) VALUES (?, ?, 0, 'IDLE')";
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, player.getUsername());
        ps.setString(2, player.getPassword());

        return ps.executeUpdate() > 0;

    }

    public void updateScore(int playerId, int newScore) throws SQLException {
        String query = "UPDATE player SET score = ? WHERE player_id = ?";
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query);
        
        ps.setInt(1, newScore);
        ps.setInt(2, playerId);
        ps.executeUpdate();

    }
}
