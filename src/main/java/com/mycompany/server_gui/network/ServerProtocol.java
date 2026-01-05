/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.network;

/**
 *
 * @author DELL
 */
import com.mycompany.server_gui.dao.GameDao;
import com.mycompany.server_gui.dao.PlayerDao;
import com.mycompany.server_gui.game.GameManager;
import com.mycompany.server_gui.game.GameRoom;
import com.mycompany.server_gui.model.GameHistoryDTO;
import com.mycompany.server_gui.model.Player;
import java.sql.SQLException;
import java.util.List;

public class ServerProtocol {

    private static final String LOGIN = "LOGIN";
    private static final String REGISTER = "REGISTER";
    private static final String LOGOUT = "LOGOUT";
    private static final String MOVE = "MOVE";
    private static final String SEND_INVITE = "SEND_INVITE";
    private static final String INVITE_RESPONSE = "INVITE_RESPONSE";
    private static final String GET_PLAYERS = "GET_ONLINE_PLAYERS";
    private static final String ERROR = "ERROR";
    private static final String GET_HISTORY = "GET_GAME_HISTORY";
    private static final GameDao gameDao = new GameDao();
    private static final PlayerDao playerDao = new PlayerDao();

    public static void processMessage(String message, PlayerHandler sender) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        System.out.println("entered process message");

        String[] parts = message.split(":");
        String type = parts[0];

        switch (type) {
            case LOGIN: {
                try {
                    // Format: LOGIN:username:password
                    System.out.println("Login case");
                    handleLogin(parts, sender);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    sender.sendMessage(ERROR + ":" + ex.getLocalizedMessage());
                }
            }
            break;

            case REGISTER: {
                try {
                    // Format: REGISTER:username:password
                    handleRegister(parts, sender);
                } catch (SQLException ex) {
                    sender.sendMessage(ERROR + ":" + ex.getLocalizedMessage());
                }
            }
            break;

            case GET_HISTORY: {
                try {
                    System.out.println("get history case");
                    handleGetHistory(sender);
                } catch (SQLException ex) {
                    sender.sendMessage(ERROR + ":" + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
            }
            break;

            case GET_PLAYERS:
                GameManager.getInstance().broadcastPlayerList();
                break;

            case SEND_INVITE:
                // Format: SEND_INVITE:targetUsername
                if (parts.length > 1) {
                    GameManager.getInstance().handleInvitation(sender.getUsername(), parts[1]);
                }
                break;

            case INVITE_RESPONSE:
                // Format: INVITE_RESPONSE:requesterUser:ACCEPTED (or REJECTED)
                handleInviteResponse(parts, sender);
                break;

            case MOVE:
                // Format: MOVE:row:col
                handleMove(parts, sender);
                break;

            case LOGOUT:
                sender.closeResources();
                break;

            default:
                System.out.println("Unknown command from " + sender.getUsername() + ": " + message);
        }
    }

    private static void handleLogin(String[] parts, PlayerHandler sender) throws SQLException {
        if (parts.length < 3) {

            return;
        }

        String username = parts[1];
        String password = parts[2];

        Player p = playerDao.login(username, password);

        System.out.println("Player : " + p);

        boolean isValid = p != null;

        if (isValid) {
            sender.setPlayer(p);
            sender.sendMessage("LOGIN_SUCCESS");
            System.out.println("LOGIN_SUCCESS Sent");
            GameManager.getInstance().addOnlinePlayer(sender);
        } else {
            System.out.println("LOGIN_FAILED Sent");
            sender.sendMessage("LOGIN_FAILED");
        }
    }

    private static void handleRegister(String[] parts, PlayerHandler sender) throws SQLException {
        // Format: REGISTER:user:pass
        String username = parts[1];
        String password = parts[2];

        Player p = new Player(username, password, 0, Player.PlayerStatus.IDLE);

        if (playerDao.register(p)) {
            sender.sendMessage("REGISTER_SUCCESS");
            sender.setPlayer(p);
        } else {
            sender.sendMessage("REGISTER_FAILED");
        }

    }

    private static void handleInviteResponse(String[] parts, PlayerHandler sender) {
        if (parts.length < 3) {
            return;
        }
        //INVITE_RESPONSE:Thaowpsta:ACCPETED

        String requester = parts[1];
        String response = parts[2];

        if ("ACCEPTED".equals(response)) {
            GameManager.getInstance().startGame(requester, sender.getUsername());
        } else {
            PlayerHandler reqHandler = GameManager.getInstance().getHandlerByName(requester);

            if (reqHandler != null) {
                reqHandler.sendMessage("INVITE_REJECTED:" + sender.getUsername());
            }
        }
    }

    private static void handleMove(String[] parts, PlayerHandler sender) {
        if (parts.length < 3) {
            return;
        }

        GameRoom room = sender.getGameRoom();

        if (room != null) {
            try {
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                room.handleMove(sender, row, col);
            } catch (NumberFormatException e) {
                System.out.println("Invalid move format");
            }
        }
    }

    private static void handleGetHistory(PlayerHandler sender) throws SQLException {
        if (sender.getPlayer() == null) {
            return; // Guard clause
        }

        int myId = sender.getPlayer().getId();

        List<GameHistoryDTO> games = gameDao.getGameHistory(myId);

        System.out.println("History : " + gameDao.getGameHistory(myId));

        // Build a response string
        // Format: HISTORY_RESPONSE:id,opp,res,date;id,opp,res,date;...
        StringBuilder sb = new StringBuilder("HISTORY_RESPONSE:");

        for (GameHistoryDTO game : games) {
            sb.append(game.toString()).append(";");
        }

        sender.sendMessage(sb.toString());
    }
}
