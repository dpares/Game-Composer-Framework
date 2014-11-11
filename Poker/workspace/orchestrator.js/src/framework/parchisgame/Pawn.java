package framework.parchisgame;

import org.json.JSONException;
import org.json.JSONObject;

import framework.engine.FrameworkGameException;

/**
 * Created by fare on 07/11/14.
 */
public class Pawn {
    public static final int INITIAL_SQUARE = 0;
    public static final int REGULAR_SQUARES = 68;
    public static final int LAST_SQUARE = 75;
    public static final int STARTING_POSITIONS[] = {4, 21, 38, 55};

    private int square;
    private int colour;

    public Pawn(int colour, int square) {
        this.colour = colour;
        this.square = square;
    }

    public Pawn(JSONObject pawn) {
        try {
            this.square = pawn.getInt("square");
            this.colour = pawn.getInt("colour");
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing JSON into Pawn", e);
        }
    }

    public int getSquare() {
        return this.square;
    }

    public int getColour() {
        return this.colour;
    }

    public JSONObject getJSON() {
        try {
            JSONObject res = new JSONObject();
            res.put("square", this.square);
            res.put("colour", this.colour);
            return res;
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing Pawn into JSON", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Pawn && ((Pawn) o).square == this.square && ((Pawn) o).colour == this.colour;
    }
}
