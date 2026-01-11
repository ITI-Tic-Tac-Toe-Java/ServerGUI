package com.mycompany.server_gui.game;

import com.mycompany.server_gui.utils.PlayerSymbol;
import static com.mycompany.server_gui.utils.PlayerSymbol.EMPTY;
import static com.mycompany.server_gui.utils.PlayerSymbol.O;
import static com.mycompany.server_gui.utils.PlayerSymbol.X;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.util.Pair;

public class XOGameLogic {

    private int[][] board = new int[3][3];
    private int stepCount = 0;
    private StringBuffer steps = new StringBuffer();

    public boolean makeMove(int r, int c, PlayerSymbol symbol) {
        if (board[r][c] != EMPTY.ordinal()) {
            return false;
        }
        board[r][c] = symbol.ordinal();
        stepCount++;
        steps.append(r).append(",").append(c).append(",").append(symbol).append(";");
        return true;
    }

    private List<Pair<Integer, Integer>> winningCoords = new ArrayList<>();

    public List<Pair<Integer, Integer>> getWinningCoords() {
        return winningCoords;
    }

    public boolean hasPlayerWon(PlayerSymbol s) {
        int symbol = s.ordinal();
        winningCoords.clear();
        int[][] winConditions = {
            {0, 0, 0, 1, 0, 2}, {1, 0, 1, 1, 1, 2}, {2, 0, 2, 1, 2, 2},
            {0, 0, 1, 0, 2, 0}, {0, 1, 1, 1, 2, 1}, {0, 2, 1, 2, 2, 2},
            {0, 0, 1, 1, 2, 2}, {0, 2, 1, 1, 2, 0}
        };

        for (int[] c : winConditions) {
            if (board[c[0]][c[1]] == symbol && board[c[2]][c[3]] == symbol && board[c[4]][c[5]] == symbol) {
                winningCoords.add(new Pair<>(c[0], c[1]));
                winningCoords.add(new Pair<>(c[2], c[3]));
                winningCoords.add(new Pair<>(c[4], c[5]));
                return true;
            }
        }
        return false;
    }

    public boolean isDraw() {
        return stepCount == 9 && (!hasPlayerWon(X) && !hasPlayerWon(O));
    }

    public int getSymbol(String sym) {
        return sym.equals("X") ? X.ordinal() : O.ordinal();
    }

    public void reset() {
        board = new int[3][3];
        stepCount = 0;
    }

    public int[] getRandomEmptyPosition() {
        while (!isDraw()) {
            Random r = new Random();
            int random = r.nextInt(9);
            int row = random / 3;
            int col = random % 3;

            if (board[row][col] == EMPTY.ordinal()) {
                return new int[]{row, col};
            }
        }

        return new int[]{-1, -1};
    }

    public List<Pair<Integer, Integer>> getAvailableMoves() {
        List<Pair<Integer, Integer>> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY.ordinal()) {
                    moves.add(new Pair<>(i, j));
                }
            }
        }
        return moves;
    }

    public void undoMove(int r, int c) {
        if (board[r][c] != EMPTY.ordinal()) {
            board[r][c] = EMPTY.ordinal();
            stepCount--;
        }
    }

    public String getSteps() {
        return steps.toString();
    }
}
