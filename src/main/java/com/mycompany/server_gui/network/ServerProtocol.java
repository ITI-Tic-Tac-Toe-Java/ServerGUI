package com.mycompany.server_gui.network;

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

    public static void processMessage(final String message, final PlayerHandler sender) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        final String[] parts = message.split(":");
        final String type = parts[0];

        switch (type) {
            case LOGIN: {
                try {
                    // Format: LOGIN:username:password
                    handleLogin(parts, sender);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    sender.sendMessage(ERROR + ":" + ex.getLocalizedMessage());
                }
                break;
            }
            break;

            case REGISTER: {
                try {
                    // Format: REGISTER:username:password
                    handleRegister(parts, sender);
                } catch (SQLException ex) {
                    ex.printStackTrace();
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
                    final String targetUsername = parts[1];
                    GameManager.getInstance().handleInvitation(sender.getUsername(), targetUsername);
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

    private static void handleLogin(final String[] parts, final PlayerHandler sender) throws SQLException {
        if (parts.length < 3) {

            return;
        }

        final String username = parts[1];
        final String password = parts[2];

        final Player p = playerDao.login(username, password);

        boolean isValid = p != null;

        if (isValid) {
            sender.setPlayer(p);
            String message = new StringBuilder("LOGIN_SUCCESS:").append(username).append(":").append(p.getScore()).toString();
            sender.sendMessage(message);
            GameManager.getInstance().addOnlinePlayer(sender);
        } else {
            sender.sendMessage("ERROR:" + "Incorrect Username or Password !");
        }
    }

    private static void handleRegister(final String[] parts, final PlayerHandler sender) throws SQLException {
        // Format: REGISTER:user:pass
        final String username = parts[1];
        final String password = parts[2];

        final Player p = new Player(username, password, 0, Player.PlayerStatus.IDLE);
      
        if (playerDao.register(p)) {
            sender.sendMessage("REGISTER_SUCCESS");
        } else {
            sender.sendMessage("ERROR:" + "There is Error in Creating your Account");
        }
    }

    private static void handleInviteResponse(final String[] parts, final PlayerHandler sender) {
        if (parts.length < 3) {
            return;
        }
        //INVITE_RESPONSE:Thaowpsta:ACCPETED

        final String requester = parts[1];
        final String response = parts[2];
        
        PlayerHandler reqHandler = GameManager.getInstance().getHandlerByName(requester);
        
    

        if ("ACCEPTED".equals(response)) {  
            GameManager.getInstance().startGame(requester, sender.getUsername());
            if (reqHandler != null) {
                reqHandler.sendMessage("INVITE_ACCEPTED:" + sender.getUsername());
            }
        } else {
            if (reqHandler != null) {
                reqHandler.sendMessage("INVITE_REJECTED:" + sender.getUsername());
            }
        }
    }

    private static void handleMove(final String[] parts, final PlayerHandler sender) {
        if (parts.length < 3) {
            return;
        }

        final GameRoom room = sender.getGameRoom();

        if (room != null) {
            try {
                final int row = Integer.parseInt(parts[1]);
                final int col = Integer.parseInt(parts[2]);
                room.handleMove(sender, row, col);
            } catch (NumberFormatException e) {
                sender.sendMessage("ERROR:Invalid move format");
            }
        }
    }

    private static void handleGetHistory(final PlayerHandler sender) throws SQLException {
        if (sender.getPlayer() == null) {
            return; // Guard clause
        }

        final int myId = sender.getPlayer().getId();

        final List<GameHistoryDTO> games = gameDao.getGameHistory(myId);

        System.out.println("History : " + gameDao.getGameHistory(myId));

        // Build a response string
        // Format: HISTORY_RESPONSE:id,opp,res,date;id,opp,res,date;...
        final StringBuilder sb = new StringBuilder("HISTORY_RESPONSE:");

        for (GameHistoryDTO game : games) {
            sb.append(game.toString()).append(";");
        }

        sender.sendMessage(sb.toString());
    }
}
