package pfc.pokergame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pfc.engine.PokerException;

/**
 * Created by fare on 11/09/14.
 */
public class Player {
    public enum State {DEFAULT, DEALER, SMALL_BLIND, BIG_BLIND, ALL_IN, FOLDED}

    private int funds;
    private int currentBet;
    private String name;
    private ArrayList<Card> holeCards;
    private boolean active;
    private State currentState;

    public Player(int initialFunds, int name) {
        this.funds = initialFunds;
        this.currentBet = 0;
        this.holeCards = new ArrayList<Card>();
        this.currentState = State.DEFAULT;
        this.active = true;
        this.name = new Integer(name).toString();
    }

    public Player(JSONObject p){
        try{
            this.funds = p.getInt("funds");
            this.currentBet = p.getInt("current_bet");
            this.currentState = State.values()[p.getInt("status")];
            this.name = new Integer(p.getInt("name")).toString();
            this.holeCards = new ArrayList<Card>();
            JSONArray aux = p.getJSONArray("hole_cards");
            for(int i=0;i<aux.length();i++)
                this.holeCards.add(new Card(aux.getJSONObject(i)));
        } catch (JSONException e){
            throw new PokerException("Error parsing JSON into Player", e);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public State getState() {
        return this.currentState;
    }

    public JSONObject getJSON(){
        JSONObject res = new JSONObject();
        try {
            res.put("funds", this.funds);
            res.put("current_bet", this.currentBet);
            res.put("status", this.currentState.ordinal());
            JSONArray cards = new JSONArray();
            for(Card c : this.holeCards)
                cards.put(c.getJSON());
            res.put("hole_cards", cards);
            res.put("name", this.name);
            return res;
        } catch (JSONException e){
            throw new PokerException("Error parsing Player into JSON", e);
        }

    }

    public void setState(State value) {
        this.currentState = value;
    }

    public void addNewHoleCard(Card c) {
        this.holeCards.add(c);
    }

    public int getFunds() {
        return this.funds;
    }

    public void addFunds(int amount) {
        this.funds += amount;
    }

    public void newHand() {
        this.holeCards = new ArrayList<Card>();
        this.currentBet = 0;
        this.currentState = State.DEFAULT;
        this.active = true;
    }

    public void newBet(int amount) {
        if (amount >= this.funds) {
            this.currentBet += this.funds;
            this.funds = 0;
            this.currentState = State.ALL_IN;
        } else {
            this.currentBet += amount;
            this.funds -= amount;
        }
    }

    public int getBet() {
        return this.currentBet;
    }

    public String toString() {
        return "-----\nPlayer " + this.name + ":\nFunds: " + this.funds + "\nHole Cards: " +
                this.holeCards + "\nBet: " + this.currentBet;
    }

    public void call(int biggestBet){
        if (this.currentBet < biggestBet)
            this.newBet(biggestBet - currentBet);
    }

    public void raise(int raiseAmount, int biggestBet){
        this.newBet(biggestBet - currentBet + raiseAmount);
    }

    public void fold(){
        this.currentState = State.FOLDED;
    }

    public int chooseAction(int biggestBet) {
        /*** MUST BE CHANGED ***/
        int betDiff = 0;

        if (this.funds > 0) {
            if (this.name.equals("0")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println(">>> YOUR TURN");
                System.out.println(">>> C(ALL)/R(AISE)/F(OLD)?");
                System.out.print("> ");
                try {
                    String line = br.readLine();
                    if (line.equalsIgnoreCase("c"))
                        this.call(biggestBet);
                    else if (line.equalsIgnoreCase("r")) { /*** MUST BE CHANGED ***/
                        this.raise(100,biggestBet);
                        betDiff = 100;
                    } else if (line.equalsIgnoreCase("f")) {
                        this.currentState = State.FOLDED;
                        betDiff = -1;
                    }
                } catch (Exception e) {
                }
            } else {
                if (this.currentBet < biggestBet)
                    this.newBet(biggestBet - currentBet);
            }
        }
        return betDiff;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Card> getHoleCards() {
        return this.holeCards;
    }

}