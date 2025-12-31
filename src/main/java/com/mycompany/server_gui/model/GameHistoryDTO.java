package com.mycompany.server_gui.model;


public class GameHistoryDTO {
    private int gameId;
    private String opponentName;
    private String result; // "WIN", "LOSE", "DRAW"
    private String date;
    
    public static class Result{
        public final static String WIN = "WIN";
        public final static String LOSE = "LOSE";
        public final static String DRAW = "DRAW";
    }
    
    public GameHistoryDTO(int gameId, String opponentName, String result, String date) {
        this.gameId = gameId;
        this.opponentName = opponentName;
        this.result = result;
        this.date = date;
    }
    
    // Override toString to make it easy to send over network
    // Format: id,opponent,result,date
    @Override
    public String toString() {
        return gameId + "," + opponentName + "," + result + "," + date;
    }
}