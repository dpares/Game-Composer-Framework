package pfc.parchisgame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pfc.engine.PokerException;

/**
 * Created by fare on 07/11/14.
 */
public class Pawn {
    public static final int INITIAL_SQUARE = 0;
    private int square;

    public Pawn(int square){
        this.square = square;
    }

    public Pawn(JSONObject pawn){
        try{
            this.square = pawn.getInt("square");
        }catch(JSONException e){
            throw new PokerException("Error when parsing pawn",e);
        }
    }
}
