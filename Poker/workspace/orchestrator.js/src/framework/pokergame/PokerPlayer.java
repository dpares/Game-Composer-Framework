package framework.pokergame;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.ojs.capabilities.frameworkCapability.FrameworkCapability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import framework.engine.FrameworkGameException;
import framework.engine.FrameworkPlayer;

/**
 * Created by fare on 11/09/14.
 */
public class PokerPlayer extends FrameworkPlayer {
    public enum PokerState implements State {DEFAULT, DEALER,
        SMALL_BLIND, BIG_BLIND, ALL_IN, FOLDED}

    private int funds;
    private int currentBet;
    private ArrayList<Card> holeCards;

    public PokerPlayer(JSONObject initData, String name, String avatar, boolean spectate) {
        this.active = !spectate;
        this.currentBet = 0;
        this.holeCards = new ArrayList<Card>();
        this.currentState = PokerState.DEFAULT;
        this.name = name;
        this.avatar = avatar;
        try{
            int initialFunds = initData.getInt("initial_funds");
            this.funds = this.active ? initialFunds : 0;
        } catch(JSONException e){
            throw new FrameworkGameException("Error parsing initial_funds", e);
        }
    }

    public PokerPlayer(JSONObject p) {
        try {
            this.funds = p.getInt("funds");
            this.currentBet = p.getInt("current_bet");
            this.currentState = PokerState.values()[p.getInt("status")];
            this.name = p.getString("name");
            this.avatar = p.getString("avatar");
            this.holeCards = new ArrayList<Card>();
            JSONArray aux = p.getJSONArray("hole_cards");
            for (int i = 0; i < aux.length(); i++)
                this.holeCards.add(new Card(aux.getJSONObject(i)));
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing JSON into Player", e);
        }
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("funds", this.funds);
            res.put("current_bet", this.currentBet);
            res.put("status", ((PokerState)this.currentState).ordinal());
            JSONArray cards = new JSONArray();
            for (Card c : this.holeCards)
                cards.put(c.getJSON());
            res.put("hole_cards", cards);
            res.put("name", this.name);
            res.put("avatar", this.avatar);
            return res;
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing Player into JSON", e);
        }

    }

    @Override
    public void newGame(JSONObject initData){
        this.newHand();
        try{
            this.funds = initData.getInt("initial_funds");
        } catch (JSONException e){
            throw new FrameworkGameException("Error parsing initial funds",e);
        }
    }

    public void addNewHoleCard(Card c) {
        this.holeCards.add(c);
    }

    public ArrayList<Card> getHoleCards() {
        return this.holeCards;
    }

    public void addFunds(int amount) {
        this.funds += amount;
    }

    public int getFunds() {
        return this.funds;
    }

    public String toString() {
        return "-----\nPlayer " + this.name + ":\nFunds: " + this.funds + "\nHole Cards: " +
                this.holeCards + "\nBet: " + this.currentBet;
    }

    public void newHand() {
        this.holeCards = new ArrayList<Card>();
        this.currentBet = 0;
        this.currentState = PokerState.DEFAULT;
    }

    public void newBet(int amount) {
        if (amount >= this.funds) {
            this.currentBet += this.funds;
            this.funds = 0;
            this.currentState = PokerState.ALL_IN;
        } else {
            this.currentBet += amount;
            this.funds -= amount;
        }
    }

    public int getBet() {
        return this.currentBet;
    }

    public void call(int biggestBet) {
        if (this.currentBet < biggestBet)
            this.newBet(biggestBet - currentBet);
    }

    public void raise(int raiseAmount, int biggestBet) {
        this.newBet(biggestBet - currentBet + raiseAmount);
    }

    public void fold() {
        this.currentState = PokerState.FOLDED;
    }

}