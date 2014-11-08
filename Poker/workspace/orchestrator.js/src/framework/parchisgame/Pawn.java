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
    public static final int LAST_SQUARE = 100;
    private int square;

    public Pawn(int square){
        this.square = square;
    }

    public Pawn(JSONObject pawn){
        try{
            this.square = pawn.getInt("square");
        }catch(JSONException e){
            throw new FrameworkGameException("Error parsing JSON into Pawn", e);
        }
    }

    public int getSquare(){
        return this.square;
    }

    public JSONObject getJSON(){
        try{
            JSONObject res = new JSONObject();
            res.put("square",this.square);
            return res;
        } catch(JSONException e){
            throw new FrameworkGameException("Error parsing Pawn into JSON", e);
        }
    }
}
