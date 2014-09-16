package pokergame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by fare on 11/09/14.
 */
public class Player {
    public static final int PLAYER_DEFAULT = 0;
    public static final int PLAYER_DEALER = 1;
    public static final int PLAYER_SMALL_BLIND = 2;
    public static final int PLAYER_BIG_BLIND = 3;
    public static final int PLAYER_NOT_PLAYING = 4;

    private int funds;
    private int currentBet;
    private String name;
    private ArrayList<Card> holeCards;
    private boolean active;
    private int status;

    private static int id = 0;

    public Player(int initialFunds) {
        this.funds = initialFunds;
        this.currentBet = 0;
        this.holeCards = new ArrayList<Card>();
        this.status = PLAYER_DEFAULT;
        this.active = true;
        name = new Integer(id).toString();
        id++;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int value) {
        this.status = value;
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
        this.status = PLAYER_DEFAULT;
        this.active = true;
    }

    public void newBet(int amount) {
        if (amount >= this.funds) {
            this.currentBet = this.funds;
            this.funds = 0;
            this.status = PLAYER_NOT_PLAYING;
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

    public int chooseAction(int biggestBet) {
        /*** MUST BE CHANGED ***/
        int betDiff = 0;

        if(this.funds > 0) {
            if (this.name.equals("0")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println(">>> YOUR TURN");
                System.out.println(">>> C(ALL)/R(AISE)/F(OLD)?");
                System.out.print("> ");
                try {
                    String line = br.readLine();
                    if (line.equalsIgnoreCase("c")) {
                        if (this.currentBet < biggestBet)
                            this.newBet(biggestBet - currentBet);
                    } else if (line.equalsIgnoreCase("r")) { /*** MUST BE CHANGED ***/
                        this.newBet(biggestBet - currentBet + 100);
                        betDiff = 100;
                    } else if (line.equalsIgnoreCase("f")) {
                        this.status = PLAYER_NOT_PLAYING;
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

    public ArrayList<Card> getHoleCards(){
        return this.holeCards;
    }

}