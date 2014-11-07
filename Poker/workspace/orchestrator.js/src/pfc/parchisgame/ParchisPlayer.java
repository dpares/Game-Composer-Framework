package pfc.parchisgame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pfc.engine.PokerException;

/**
 * Created by fare on 07/11/14.
 */
public class ParchisPlayer {

    public enum Colour {YELLOW, RED, GREEN, BLUE, UNDEFINED};

    private String name;
    private String avatar;
    private boolean active;
    private Colour currentState; // Posiblemente sera cambiado

    private List<Pawn> pawns;

    public ParchisPlayer(String name, String avatar, boolean spectate) {
        this.active = !spectate;
        this.currentState = Colour.UNDEFINED;
        this.name = name;
        this.avatar = avatar;
        this.pawns = new ArrayList<Pawn>();
        for(int i=0;i<4;i++)
            pawns.add(new Pawn(Pawn.INITIAL_SQUARE));
    }

    public ParchisPlayer(JSONObject p){
        try {
            this.currentState = Colour.values()[p.getInt("status")];
            this.name = p.getString("name");
            this.avatar = p.getString("avatar");
            JSONArray aux = p.getJSONArray("pawns");
            this.pawns = new ArrayList<Pawn>();
            for (int i = 0; i < aux.length(); i++)
                this.pawns.add(new Pawn(aux.getJSONObject(i)));
        } catch (JSONException e) {
            throw new PokerException("Error parsing JSON into Player", e);
        }
    }

    public List<Pawn> getPawns(){
        return this.pawns;
    }

    public Colour getColour(){
        return this.currentState;
    }

    public void setColour(Colour colour){
        this.currentState = colour;
    }
}
