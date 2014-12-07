package composer.pokergame;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.ojs.capabilities.composerCapability.ComposerCapability;

import org.json.JSONException;
import org.json.JSONObject;

import composer.engine.ComposerGameException;

/**
 * Created by fare on 09/09/14.
 */
public class Card implements Comparable<Card>{

    public enum Suit {HEART, DIAMOND, SPADE, CLUB}
    public static final int HIGHEST_VALUE = 14;

    private int rank, suit;
    private static String[] suits = {"h", "d", "s", "c"}; //{"♥", "♦", "♠", "♣"}
    private static String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    public Card(int suit, int rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Card(JSONObject card){
        try {
            this.suit = card.getInt("suit");
            this.rank = card.getInt("rank");
        }catch(JSONException e){
            throw new ComposerGameException("Error parsing JSON into Card", e);
        }
    }

    public int getRank() {
        int res = this.rank + 1; // ranks[0] = "A"
        if(res == 1)
            res = HIGHEST_VALUE; // Aces have the biggest value
        return res;
    }

    public int getSuit() {
        return this.suit;
    }

    public JSONObject getJSON(){
        try{
            JSONObject res = new JSONObject();
            res.put("suit",this.suit);
            res.put("rank",this.rank);
            return res;
        } catch(JSONException e){
            throw new ComposerGameException("Error parsing Card into JSON", e);
        }
    }

    public Drawable getDrawable(){
        Context ctx = ComposerCapability.getContext();
        String drawableName = suits[this.suit] +
                (this.getRank() < 10 ? "0"+this.getRank():this.getRank());
        int resourceId = ctx.getResources().
                getIdentifier(drawableName ,"drawable",ctx.getPackageName());
        return ctx.getResources().getDrawable(resourceId);
    }

    public String toString() {
        return ranks[this.rank] + suits[this.suit];
    }

    public int compareTo(Card c){
        int a = this.getRank();
        int b = c.getRank();
        int res = new Integer(a).compareTo(b);
        if(res == 0)
            res = new Integer(this.suit).compareTo(c.suit);
        return res;
    }
}