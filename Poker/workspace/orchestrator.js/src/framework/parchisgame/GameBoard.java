package framework.parchisgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fare on 10/11/14.
 */
public class GameBoard {
    private Map<Integer, List<Pawn>> squareStates;

    public GameBoard() {
        this.squareStates = new HashMap<Integer, List<Pawn>>();
    }

    private int getSquareKey(int colour, int pos) {
        if (pos >= Pawn.REGULAR_SQUARES && pos < Pawn.LAST_SQUARE)
            pos += (1 + colour) * 100;
        return pos;
    }

    public List<Pawn> pawnsInSquare(int colour, int pos) {
        return squareStates.get(getSquareKey(colour, pos));
    }

    public int nextSpaceInSquare(int colour, int pos) {
        List<Pawn> aux = pawnsInSquare(colour, pos);
        int res = 0;
        if (aux != null) {
            res = aux.size();
            if (res == 2)
                res = -1;
        }

        return res;
    }

    public boolean spaceInSquare(int colour, int pos) {
        return (!squareStates.containsKey(getSquareKey(colour, pos)) ||
                squareStates.get(getSquareKey(colour, pos)).size() < 2);
    }

    public void insertPawn(Pawn pawn) {
        List<Pawn> aux = pawnsInSquare(pawn.getColour(), pawn.getSquare());
        if (aux == null)
            aux = new ArrayList<Pawn>();
        aux.add(pawn);
        this.squareStates.put(getSquareKey(pawn.getColour(), pawn.getSquare()), aux);
    }

    public void removePawn(Pawn pawn) {
        List<Pawn> aux = pawnsInSquare(pawn.getColour(), pawn.getSquare());
        if (aux != null)
            aux.remove(pawn);
        this.squareStates.put(getSquareKey(pawn.getColour(), pawn.getSquare()), aux);
    }

}
