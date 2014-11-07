package framework.pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by fare on 16/09/14.
 */
public class BestHand implements Comparable<BestHand> {

    public enum Type {
        HIGH_CARD("HIGH CARD"), ONE_PAIR("PAIR"), TWO_PAIR("TWO PAIR"),
        THREE_OF_A_KIND("THREE OF A KIND"), STRAIGHT("STRAIGHT"), FLUSH("FLUSH"),
        FULL_HOUSE("FULL HOUSE"), FOUR_OF_A_KIND("FOUR OF A KIND"), STRAIGHT_FLUSH("STRAIGHT FLUSH");

        private String name;

        Type(String name){
            this.name = name;
        }

        public String toString(){
            return this.name;
        }
    }

    private Type type;
    private int[] highValues;
    private PokerPlayer owner;

    private static int[] reverseArray(int[] array) {
        int temp;
        for (int i = 0; i < array.length / 2; i++) {
            temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
        return array;
    }

    private BestHand bestCombination(List<Card> cards) {
        BestHand res, best = null;
        if (cards.size() > 5) {
            Card aux;
            for (int i = 0; i < cards.size(); i++) {
                aux = cards.remove(i);
                res = bestCombination(cards);
                if (best == null || best.compareTo(res) < 0)
                    best = res;
                cards.add(i, aux);
            }
            return best;
        } else {
            int rankHistogram[] = new int[14]; // For the sake of simplicity, the initial slot won't be used
            int suitHistogram[] = new int[4];
            boolean isStraight = true;
            Card previousCard = null;
            int highValues[];
            Type type;

            for (Card c : cards) {
                rankHistogram[c.getRank() - 1]++;
                suitHistogram[c.getSuit()]++;
                if (isStraight) {
                    if (previousCard == null || previousCard.getRank() + 1 == c.getRank() ||
                            previousCard.getRank() == 5 && c.getRank() == Card.HIGHEST_VALUE) // A2345 Straight
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
                    type = Type.STRAIGHT_FLUSH;
                else
                    type = Type.STRAIGHT;
                highValues[0] = cards.get(cards.size() - 1).getRank();
            } else if (suitHistogram[0] == 5) {
                type = Type.FLUSH;
                highValues[0] = cards.get(cards.size() - 1).getRank();
            } else if (rankHistogram[0] == 4) {
                type = Type.FOUR_OF_A_KIND;
                for (int i = 0; i < originalRankHistogram.length; i++) {
                    if (originalRankHistogram[i] == 4)
                        highValues[0] = i;
                    else if (originalRankHistogram[i] == 1)
                        highValues[1] = i;
                }
            } else if (rankHistogram[0] == 3 && rankHistogram[1] == 2) {
                type = Type.FULL_HOUSE;
                for (int i = 0; i < originalRankHistogram.length; i++) {
                    if (originalRankHistogram[i] == 3)
                        highValues[0] = i;
                    else if (originalRankHistogram[i] == 2)
                        highValues[1] = i;
                }
            } else if (rankHistogram[0] == 3) {
                type = Type.THREE_OF_A_KIND;
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
                type = Type.TWO_PAIR;
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
                type = Type.ONE_PAIR;
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
                highValues[highValues.length - 1] = -1;
            } else {
                type = Type.HIGH_CARD;
                for (int i = 0; i < 5; i++)
                    highValues[i] = cards.get(cards.size() - i - 1).getRank();
            }
            return new BestHand(type, highValues);
        }
    }

    private BestHand(Type type, int[] highValues) {
        this.type = type;
        this.highValues = highValues;
    }

    public BestHand(List<Card> holeCards, List<Card> communityCards, PokerPlayer owner) {
        ArrayList<Card> cards = new ArrayList<Card>(holeCards);
        cards.addAll(communityCards);
        Collections.sort(cards);

        BestHand aux = bestCombination(cards);
        this.type = aux.type;
        this.highValues = aux.highValues;
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

    public boolean equals(Object o) {
        return o instanceof BestHand && ((BestHand) o).type.equals(this.type) &&
                Arrays.equals(((BestHand) o).highValues, this.highValues);
    }

    public Type getType() {
        return this.type;
    }

    public PokerPlayer getOwner() {
        return this.owner;
    }

    public String toString() {
        return this.owner.getName() + ": " + this.type.toString() + " " + Arrays.toString(this.highValues);
    }

}
