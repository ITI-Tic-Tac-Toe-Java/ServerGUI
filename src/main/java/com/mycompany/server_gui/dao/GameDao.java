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
import com.mycompany.server_gui.model.Game; // Assuming you have a Game POJO
import com.mycompany.server_gui.model.GameHistoryDTO;
import com.mycompany.server_gui.model.GameHistoryDTO.Result;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameDao {

    private static final String GAMES_DIR = "server_games_log";

    public GameDao() {
        // Create directory for game files if it doesn't exist
        new File(GAMES_DIR).mkdirs();
    }

    // 1. Insert Game into DB
    // 2. Write moves to a file named "game_{id}.txt"
    public void saveGame(Game game) throws SQLException, IOException {
        String query = "INSERT INTO game (player_x_id, player_o_id, winner_id, played_at) VALUES (?, ?, ?, ?)";

        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, game.getPlayerXId());
        ps.setInt(2, game.getPlayerOId());

        if (game.getWinnerId() == -1) {
            ps.setNull(3, java.sql.Types.INTEGER);
        } else {
            ps.setInt(3, game.getWinnerId());
        }

        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

        int rows = ps.executeUpdate();

        if (rows > 0) {
            // Get the generated Game ID to name the file
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int gameId = rs.getInt(1);
                    saveMovesToFile(gameId, game.getMovesData());
                }
            }
        }

    }

    private void saveMovesToFile(int gameId, String moves) throws IOException {
        File file = new File(GAMES_DIR, "game_" + gameId + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(moves);
        }
    }

    public List<GameHistoryDTO> getGameHistory(int playerId) throws SQLException {
        List<GameHistoryDTO> history = new ArrayList<>();

        // Select games where the user played as X or O
        // We Join twice to get usernames for both X and O
        String query = "SELECT g.game_id, g.played_at, g.winner_id, "
                + "p1.username AS x_name, p1.player_id AS x_id, "
                + "p2.username AS o_name, p2.player_id AS o_id "
                + "FROM game g "
                + "JOIN player p1 ON g.player_x_id = p1.player_id "
                + "JOIN player p2 ON g.player_o_id = p2.player_id "
                + "WHERE g.player_x_id = ? OR g.player_o_id = ?";
        
        Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, playerId);
        ps.setInt(2, playerId);
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int gameId = rs.getInt("game_id");
            int winnerId = rs.getInt("winner_id");
            int xId = rs.getInt("x_id");
            String xName = rs.getString("x_name");
            String oName = rs.getString("o_name");
            String date = rs.getTimestamp("played_at").toString();

            String opponentName;
            String result;

            // Determine who the opponent was and if we won
            if (playerId == xId) {
                opponentName = oName; // I am X, so opponent is O
            } else {
                opponentName = xName; // I am O, so opponent is X
            }

            
            if (winnerId == -1) {
                result = Result.DRAW;
            } else if (winnerId == playerId) {
                result = Result.WIN;
            } else {
                result = Result.LOSE;
            }

            history.add(new GameHistoryDTO(gameId, opponentName, result, date));
        }

        return history;
    }
}
