package pokergame;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by fare on 09/09/14.
 */
public class Deck {
    private ArrayList<Card> deck;
    private Random rnd;

    public Deck() {
        deck = new ArrayList<Card>();
        rnd = new Random();
        Card aux;

        this.newHand();
    }

    public void newHand() {
        for (int i = 0; i < 4; i++) // Filling the deck with cards
            for (int j = 0; j < 13; j++)
                deck.add(new Card(i, j));

        int i, j; // Shuffling the deck
        Card aux;
        for (int cont = 0; cont < 2 * deck.size(); cont++) {
            i = rnd.nextInt(deck.size() - 1);
            j = rnd.nextInt(deck.size() - 1);
            aux = deck.get(j);
            deck.set(j, deck.get(i));
            deck.set(i, aux);
        }
    }

    public Card draw() {
        return deck.remove(0);
    }

    public void discard() {
        deck.remove(0);
    }

}
