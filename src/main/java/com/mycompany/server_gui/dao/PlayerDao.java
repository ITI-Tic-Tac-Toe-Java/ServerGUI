package com.mycompany.server_gui.dao;

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
                p.setStatus(Player.PlayerStatus.IDLE);
                return p;
            }
        }

        return null;
    }

    public boolean register(Player player) throws SQLException {
        String query = "INSERT INTO player (username, password, score, status) VALUES (?, ?, 0, ?)";
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, player.getUsername());
        ps.setString(2, player.getPassword());
        ps.setString(3, Player.PlayerStatus.OFFLINE.name());

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

    public void updatStatus(int playerId, String status) throws SQLException {
        String query = "UPDATE player SET status = ? WHERE player_id = ?";
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, status);
        ps.setInt(2, playerId);
        ps.executeUpdate();

    }

    public int getAllPlayersCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM player";
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
