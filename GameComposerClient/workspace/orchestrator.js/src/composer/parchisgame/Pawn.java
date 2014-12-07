package composer.parchisgame;

import org.json.JSONException;
import org.json.JSONObject;

import composer.engine.ComposerGameException;

/**
 * Created by fare on 07/11/14.
 */
public class Pawn {
    public static final int INITIAL_SQUARE = 0;
    public static final int REGULAR_SQUARES = 68;
    public static final int LAST_SQUARE = 75;
    public static final int STARTING_POSITIONS[] =  {4, 21, 38, 55};
    public static final int CORRIDOR_START[] = {67, 16, 33, 50};
    private static final int SAFE_SQUARES[] = {4, 11, 17, 21, 28, 33, 38, 45, 50, 55, 62, 67};

    private int square;
    private int colour;
    private int number;

    public Pawn(int colour, int square, int number) {
        this.colour = colour;
        this.square = square;
        this.number = number;
    }

    public Pawn(JSONObject pawn) {
        try {
            this.square = pawn.getInt("square");
            this.colour = pawn.getInt("colour");
            this.number = pawn.getInt("number");
        } catch (JSONException e) {
            throw new ComposerGameException("Error parsing JSON into Pawn", e);
        }
    }

    public int getSquare() {
        return this.square;
    }

    public int getColour() {
        return this.colour;
    }

    public int getNumber() {
        return this.number;
    }

    public JSONObject getJSON() {
        try {
            JSONObject res = new JSONObject();
            res.put("square", this.square);
            res.put("colour", this.colour);
            res.put("number", this.number);
            return res;
        } catch (JSONException e) {
            throw new ComposerGameException("Error parsing Pawn into JSON", e);
        }
    }

    public static boolean isSafeSquare(int pos) {
        boolean res = false;
        int i = 0;
        while (!res && i < SAFE_SQUARES.length) {
            if (SAFE_SQUARES[i] == pos)
                res = true;
            else
                i++;
        }

        return res;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Pawn && ((Pawn) o).square == this.square &&
                ((Pawn) o).colour == this.colour && ((Pawn) o).number == this.number;
    }

    @Override
    public Pawn clone(){
        return new Pawn(this.colour,this.square,this.number);
    }
}
