/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server_gui.game;

import com.mycompany.server_gui.network.PlayerHandler;
import com.mycompany.server_gui.utils.PlayerSymbol;

/**
 *
 * @author DELL
 */
public class XOGameLogic {

    public static final int EMPTY = 0;
    public static final int X = 1;
    public static final int O = 2;

    private int[][] board = new int[3][3];
    private int stepCount = 0;
    private StringBuffer steps = new StringBuffer();

    public boolean makeMove(int r, int c, PlayerSymbol symbol) {
        if (board[r][c] != EMPTY) {
            return false;
        }

        board[r][c] = symbol.ordinal() + 1;
        stepCount++;
        steps.append(stepCount).append(":").append(r).append(",").append(c).append(",").append(symbol).append(";");
        return true;
    }

    public boolean hasPlayerWon(PlayerHandler player) {
        int symbol = player.getPlayerSymbol().ordinal() + 1;

        return (board[0][0] == symbol && board[0][1] == symbol && board[0][2] == symbol)
                || (board[1][0] == symbol && board[1][1] == symbol && board[1][2] == symbol)
                || (board[2][0] == symbol && board[2][1] == symbol && board[2][2] == symbol)
                || (board[0][0] == symbol && board[1][0] == symbol && board[2][0] == symbol)
                || (board[0][1] == symbol && board[1][1] == symbol && board[2][1] == symbol)
                || (board[0][2] == symbol && board[1][2] == symbol && board[2][2] == symbol)
                || (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
                || (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol);
    }

    public boolean isDraw() {
        return stepCount == 9;
    }

    public void reset() {
        board = new int[3][3];
        stepCount = 0;
    }

    public String getSteps() {
        return steps.toString();
    }
}
