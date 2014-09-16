package pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by fare on 16/09/14.
 */
public class BestHand implements Comparable<BestHand> {

    public enum Type {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT, FLUSH, FULL_HOUSE,
        FOUR_OF_A_KIND, STRAIGHT_FLUSH
    }

    ;

    private Type type;
    private int[] highValues;
    private Player owner;

    private static int[] reverseArray(int[] array) {
        int temp;
        for (int i = 0; i < array.length / 2; i++) {
            temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
        return array;
    }

    public BestHand(ArrayList<Card> holeCards, ArrayList<Card> communityCards, Player owner) {
        int rankHistogram[] = new int[14]; // For the sake of simplicity, the initial slot won't be used
        int suitHistogram[] = new int[4];
        String res = "";

        ArrayList<Card> cards = new ArrayList<Card>(holeCards);
        cards.addAll(communityCards);
        Collections.sort(cards);
        boolean isStraight = true;
        Card previousCard = null;
        /*** SUPPORT +5 CARD HANDS ***/
        for (Card c : cards) {
            rankHistogram[c.getRank()-1]++;
            suitHistogram[c.getSuit()]++;
            if (isStraight) {
                if (previousCard == null || previousCard.getRank() + 1 == c.getRank() ||
                        previousCard.getRank() == 4 && c.getRank() == 14) // A2345 Straight
                    previousCard = c;
                else
                    isStraight = false;
            }
        }
        int originalRankHistogram[] = rankHistogram.clone();
        Arrays.sort(rankHistogram);
        rankHistogram = reverseArray(rankHistogram);
        Arrays.sort(suitHistogram);
        suitHistogram = reverseArray(suitHistogram);

        // Printing out what kind of hand it is
        highValues = new int[5];
        for (int i = 0; i < 5; i++) {
            highValues[i] = -1;
        }
        if (isStraight) {
            if (suitHistogram[0] == 5)
                this.type = Type.STRAIGHT_FLUSH;
            else
                this.type = Type.STRAIGHT;
            highValues[0] = cards.get(cards.size() - 1).getRank();
        } else if (suitHistogram[0] == 5) {
            this.type = Type.FLUSH;
            highValues[0] = cards.get(cards.size() - 1).getRank();
        } else if (rankHistogram[0] == 4) {
            this.type = Type.FOUR_OF_A_KIND;
            for (int i = 0; i < originalRankHistogram.length; i++) {
                if (originalRankHistogram[i] == 4)
                    highValues[0] = i;
                else if (originalRankHistogram[i] == 1)
                    highValues[1] = i;
            }
        } else if (rankHistogram[0] == 3 && rankHistogram[1] == 2) {
            this.type = Type.FULL_HOUSE;
            for (int i = 0; i < originalRankHistogram.length; i++) {
                if (originalRankHistogram[i] == 3)
                    highValues[0] = i;
                else if (originalRankHistogram[i] == 2)
                    highValues[1] = i;
            }
        } else if (rankHistogram[0] == 3) {
            this.type = Type.THREE_OF_A_KIND;
            for (int i = 0; i < originalRankHistogram.length; i++) {
                if (originalRankHistogram[i] == 3)
                    highValues[0] = i;
                else if (originalRankHistogram[i] == 1) {
                    if (highValues[1] == -1)
                        highValues[1] = i;
                    else if (i > highValues[1]) {
                        highValues[2] = highValues[1];
                        highValues[1] = i;
                    } else
                        highValues[2] = i;
                }
            }
        } else if (rankHistogram[0] == 2 && rankHistogram[1] == 2) {
            this.type = Type.TWO_PAIR;
            for (int i = 0; i < originalRankHistogram.length; i++) {
                if (originalRankHistogram[i] == 1)
                    highValues[2] = i;
                else if (originalRankHistogram[i] == 2) {
                    if (highValues[0] == -1)
                        highValues[0] = i;
                    else if (i > highValues[0]) {
                        highValues[1] = highValues[0];
                        highValues[0] = i;
                    } else
                        highValues[1] = i;
                }
            }
        } else if (rankHistogram[0] == 2) {
            this.type = Type.ONE_PAIR;
            int i = 0;
            boolean foundPair = false;
            while (i < 5) {
                highValues[(foundPair ? i - 1 : i)] = cards.get(cards.size() - i - 1).getRank();
                if (i > 0 && highValues[i] == highValues[i - 1]) {
                    for (int j = i; j > 0; j--)
                        highValues[j] = highValues[j - 1];
                    highValues[0] = cards.get(cards.size() - i - 1).getRank();
                    foundPair = true;
                }
                i++;
            }
            highValues[highValues.length-1] = -1;
        } else {
            this.type = Type.HIGH_CARD;
            for (int i = 0; i < 5; i++)
                highValues[i] = cards.get(cards.size() - i - 1).getRank();
        }

        this.owner = owner;
    }

    public int compareTo(BestHand that) {
        int res = this.type.compareTo(that.type);
        int i = 0;
        while (res == 0 && i < this.highValues.length) {
            res = new Integer(this.highValues[i]).compareTo(new Integer(that.highValues[i]));
            i++;
        }
        return res;
    }

    public boolean equals(Object o){
        return o instanceof BestHand && ((BestHand)o).type.equals(this.type) &&
                Arrays.equals(((BestHand)o).highValues,this.highValues);
    }

    public Type getType() {
        return this.type;
    }

    public Player getOwner() {
        return this.owner;
    }

    public String toString() {
        return this.owner.getName() + ": " + this.type.toString() + " " + Arrays.toString(this.highValues);
    }

}
