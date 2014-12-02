package composer.parchisgame;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fare on 10/11/14.
 */
public class GameBoard {
    private SparseArray<List<Pawn>> squareStates;
    private Pawn conflictPawn;
    private List<Pawn> conflictPawnList;

    public GameBoard() {
        this.squareStates = new SparseArray<List<Pawn>>();
    }

    private int getSquareKey(int colour, int pos) {
        if (pos >= Pawn.REGULAR_SQUARES && pos < Pawn.LAST_SQUARE)
            pos += (1 + colour) * 100;
        return pos;
    }

    public List<Pawn> pawnsInSquare(int colour, int pos) {
        if (conflictPawn != null && colour == conflictPawn.getColour() &&
                pos == conflictPawn.getSquare())
            return conflictPawnList;
        else
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
        return (squareStates.indexOfKey(getSquareKey(colour, pos)) < 0 ||
                squareStates.get(getSquareKey(colour, pos)).size() < 2);
    }

    public void insertPawn(Pawn pawn) {
        List<Pawn> aux = pawnsInSquare(pawn.getColour(), pawn.getSquare());
        if (aux == null)
            aux = new ArrayList<Pawn>();
        if (aux.size() < 2) {
            aux.add(pawn);
            this.squareStates.put(getSquareKey(pawn.getColour(), pawn.getSquare()), aux);
        }
    }

    public void removePawn(Pawn pawn, boolean conflict) {
        if (conflictPawn == null || !conflictPawn.equals(pawn)) {
            List<Pawn> aux = squareStates.get(getSquareKey(pawn.getColour(), pawn.getSquare()));
            if (conflict) {
                this.conflictPawn = pawn;
                this.conflictPawnList = new ArrayList<Pawn>();
                for (Pawn p : aux)
                    this.conflictPawnList.add(p.clone());
            }
            if (aux != null) {
                aux.remove(pawn);
                this.squareStates.put(getSquareKey(pawn.getColour(), pawn.getSquare()), aux);
            }
        }
    }

    public void removePawn(Pawn pawn) {
        this.removePawn(pawn, false);
    }

    public boolean barrierBetween(Pawn pawn, int pos) {
        int colour = pawn.getColour();
        int dest = getSquareKey(colour, pos);
        int i = getSquareKey(colour, pawn.getSquare() + 1);
        boolean res = false;
        while (!res && i < dest) {
            if (!spaceInSquare(colour, i))
                res = true;
            else {
                i++;
                if (i == Pawn.CORRIDOR_START[colour] + 1)
                    i = getSquareKey(colour, Pawn.REGULAR_SQUARES);
            }
        }

        return res;
    }

    public void deleteConflictPawn() {
        this.conflictPawn = null;
        System.gc();
    }

}
