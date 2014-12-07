package composer.parchisgame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import composer.engine.ComposerGameException;
import composer.engine.ComposerPlayer;

/**
 * Created by fare on 07/11/14.
 */
public class ParchisPlayer extends ComposerPlayer {

    public enum ParchisState implements State {DEFAULT, FINISHED}

    public enum Colour {YELLOW, BLUE, RED, GREEN, UNDEFINED}

    private Colour colour; // Player's colour. Set by the server depending on connection order.
    private List<Pawn> pawns; // List of four pawns

    public ParchisPlayer(JSONObject initData, String name, String avatar, boolean spectate) {
        super(initData,name,avatar,spectate);
    }

    public ParchisPlayer(JSONObject p) {
        super(p);
        try {
            this.currentState = ParchisState.values()[p.getInt("status")];
            JSONArray aux = p.getJSONArray("pawns");
            this.pawns = new ArrayList<Pawn>();
            this.colour = Colour.values()[p.getInt("colour")];
            for (int i = 0; i < aux.length(); i++)
                this.pawns.add(new Pawn(aux.getJSONObject(i).getInt("colour"),
                        aux.getJSONObject(i).getInt("square"), aux.getJSONObject(i).getInt("number")));
        } catch (JSONException e) {
            throw new ComposerGameException("Error parsing JSON into Player", e);
        }
    }


    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("status", ((ParchisState) this.currentState).ordinal());
            JSONArray pawns = new JSONArray();
            for (Pawn p : this.pawns)
                pawns.put(p.getJSON());
            res.put("pawns", pawns);
            res.put("name", this.name);
            res.put("avatar", this.avatar);
            res.put("colour", this.colour.ordinal());
            return res;
        } catch (JSONException e) {
            throw new ComposerGameException("Error parsing Player into JSON", e);
        }
    }

    @Override
    public void newRound() {
        this.colour = Colour.UNDEFINED;
        this.currentState = ParchisState.DEFAULT;
        this.pawns = new ArrayList<Pawn>();
        for (int i = 0; i < 4; i++)
            pawns.add(new Pawn(Colour.UNDEFINED.ordinal(), Pawn.INITIAL_SQUARE, i));
    }

    public List<Pawn> getPawns() {
        return this.pawns;
    }

    public Pawn firstHomePawn() {
        Pawn res = null;
        int i = this.pawns.size() - 1;
        while (res == null && i >= 0) {
            Pawn p = this.pawns.get(i);
            if (p.getSquare() == Pawn.INITIAL_SQUARE)
                res = p;
            else
                i--;
        }
        return res;
    }

    public Colour getColour() {
        return this.colour;
    }

    public int getHomePawns() {
        int res = 0;
        for (Pawn p : pawns)
            if (p.getSquare() == Pawn.INITIAL_SQUARE)
                res++;

        return res;
    }

    public int getFinishedPawns() {
        int res = 0;
        for (Pawn p : pawns)
            if (p.getSquare() == Pawn.LAST_SQUARE)
                res++;

        return res;
    }

    @Override
    public ParchisPlayer clone(){
        return new ParchisPlayer(this.getJSON());
    }
}
