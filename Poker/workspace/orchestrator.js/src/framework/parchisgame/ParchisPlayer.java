package framework.parchisgame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import framework.engine.FrameworkGameException;
import framework.engine.FrameworkPlayer;

/**
 * Created by fare on 07/11/14.
 */
public class ParchisPlayer extends FrameworkPlayer {

    public enum ParchisState implements State {DEFAULT, FINISHED};

    public enum Colour {YELLOW, RED, GREEN, BLUE, UNDEFINED};

    private Colour colour;

    private List<Pawn> pawns;

    public ParchisPlayer(JSONObject initData, String name, String avatar, boolean spectate) {
        this.active = !spectate;
        this.name = name;
        this.avatar = avatar;
        this.newRound();
    }

    public ParchisPlayer(JSONObject p){
        try {
            this.currentState = ParchisState.values()[p.getInt("status")];
            this.name = p.getString("name");
            this.avatar = p.getString("avatar");
            this.colour = Colour.values()[p.getInt("colour")];
            JSONArray aux = p.getJSONArray("pawns");
            this.pawns = new ArrayList<Pawn>();
            for (int i = 0; i < aux.length(); i++)
                this.pawns.add(new Pawn(aux.getJSONObject(i)));
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing JSON into Player", e);
        }
    }


    @Override
    public JSONObject getJSON(){
        JSONObject res = new JSONObject();
        try {
            res.put("status", ((ParchisState)this.currentState).ordinal());
            JSONArray pawns = new JSONArray();
            for (Pawn p : this.pawns)
                pawns.put(p.getJSON());
            res.put("hole_cards", pawns);
            res.put("name", this.name);
            res.put("avatar", this.avatar);
            return res;
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing Player into JSON", e);
        }
    }

    @Override
    public void newRound() {
        this.colour = Colour.UNDEFINED;
        this.currentState = ParchisState.DEFAULT;
        this.pawns = new ArrayList<Pawn>();
        for(int i=0;i<4;i++)
            pawns.add(new Pawn(Pawn.INITIAL_SQUARE));
    }

    public List<Pawn> getPawns(){
        return this.pawns;
    }

    public Colour getColour(){
        return this.colour;
    }

    public void setColour(Colour colour){
        this.colour = colour;
    }
}
