package pfc.pokergame;

/**
 * Created by fare on 09/09/14.
 */
public class Card implements Comparable<Card>{

    public enum Suit {HEART, DIAMOND, SPADE, CLUB}
    public static final int HIGHEST_VALUE = 14;

    private int rank, suit;
    private static String[] suits = {"♥", "♦", "♠", "♣"};
    private static String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    public Card(int suit, int rank) {
        this.suit = suit;
        this.rank = rank;
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