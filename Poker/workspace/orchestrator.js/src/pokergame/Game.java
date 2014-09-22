package pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by fare on 09/09/14.
 */
public class Game {
    private static final int INITIAL_FUNDS = 1000;
    private static final int INITIAL_SMALL_BLIND = 100;
    private static final int TURNS_PER_BLIND = 5;

    private static ArrayList<Player> players;
    private static Deck deck;
    private static ArrayList<Card> communityCards;

    private static int dealer;
    private static int currentPlayerIndex;
    private static int biggestBet;

    public static void showCurrentState() {
        for (Player p : players) /*** MUST BE CHANGED ***/
            System.out.println(p);
        System.out.println("-----\n-----\nCOMMUNITY CARDS: " + communityCards + "\n-----\n-----");
    }

    private static int nextPlayer(int index) {
        int res = index;
        do {
            res++;
            if (res == players.size())
                res = 0;
        }
        while (!players.get(res).isActive() || players.get(res).getState() == Player.State.NOT_PLAYING);
        return res;
    }

    private static boolean bettingConsensus(int biggestBet) {
        boolean res = true;
        int i = 0;
        while (i < players.size() && res) {
            Player p = players.get(i);
            if (!p.isActive() || p.getState() == Player.State.NOT_PLAYING || p.getBet() == biggestBet)
                i++;
            else
                res = false;
        }
        return res;
    }

    private static int countActivePlayers() {
        int res = 0;
        for (Player p : players) {
            if (p.isActive() || p.getState() != Player.State.NOT_PLAYING)
                res++;
        }
        return res;
    }

    public static void main(String[] args) {
        /*** INITIALISATION ***/
        players = new ArrayList<Player>();
        deck = new Deck();
        communityCards = new ArrayList<Card>();

        // STEP 1: Adding funds
        players.add(new Player(INITIAL_FUNDS));
        players.add(new Player(INITIAL_FUNDS));

        // STEP 2: Selecting the first dealer
        Random rnd = new Random();
        dealer = rnd.nextInt(players.size());
        players.get(dealer).setState(Player.State.DEALER);

        while (countActivePlayers() > 1) {

            // STEP 3: Showing initial state to players
            showCurrentState();

            // STEP 4: Assigning blinds (Note: Player 1 is the closest player to Player 0, clock-wise)
            if (players.size() > 2) { // If there are only 2 players, the dealer chip acts as the small blind
                players.get(nextPlayer(dealer)).setState(Player.State.SMALL_BLIND);
                players.get(nextPlayer(nextPlayer(dealer))).setState(Player.State.BIG_BLIND);
            } else
                players.get(nextPlayer(dealer)).setState(Player.State.BIG_BLIND);

            // STEP 5: Dealing the hole cards
            System.out.println("DEALING HOLE CARDS");
            deck.discard(); // Always discard before dealing cards
            for (Player p : players) { /*** MUST BE CHANGED ***/
                p.addNewHoleCard(deck.draw());
                p.addNewHoleCard(deck.draw());
            }
            showCurrentState();

            /*** HAND STARTS ***/

            // STEP 6: Announcing the start of the new hand
            System.out.println("NEW HAND"); /*** MUST BE CHANGED ***/

            // STEP 7: Paying the blinds
            for (Player p : players) {
                if (p.getState() == Player.State.SMALL_BLIND || (p.getState() ==
                        Player.State.DEALER && players.size() == 2))
                    p.newBet(INITIAL_SMALL_BLIND);
                else if (p.getState() == Player.State.BIG_BLIND)
                    p.newBet(INITIAL_SMALL_BLIND * 2);
            }
            showCurrentState();

            /*** PRE-FLOP BETTING ROUND ***/
            currentPlayerIndex = nextPlayer(dealer); /*** MUST BE CHANGED ***/
            biggestBet = INITIAL_SMALL_BLIND * 2;
            do {
                int numPlayers = countActivePlayers();
                for (int i = 0; i < numPlayers; i++) {
                    Player currentPlayer = players.get(currentPlayerIndex);
                    if(currentPlayer.getFunds()==0)
                        ;
                    // STEP 8: Announce the start of the betting turn
                    System.out.println(currentPlayer.getName() + "'S TURN");

                    // STEP 9: Call/Check/Raise/Fold
                    int betDiff = currentPlayer.chooseAction(biggestBet);
                    if (betDiff > 0)
                        biggestBet += betDiff;

                    // STEP 10: Show current state and move index to next player
                    showCurrentState();
                    currentPlayerIndex = nextPlayer(currentPlayerIndex);
                }
            } while (!bettingConsensus(biggestBet));

            // STEP 11: Flop
            deck.discard();
            for (int i = 0; i < 3; i++)
                communityCards.add(deck.draw());
            showCurrentState();

            for(int round=0;round<3;round++) {
                do {
                    int numPlayers = countActivePlayers();
                    for (int i = 0; i < numPlayers; i++) {
                        Player currentPlayer = players.get(currentPlayerIndex);
                        // STEP 12: Announce the start of the betting turn
                        System.out.println(currentPlayer.getName() + "'S TURN");

                        // STEP 13: Call/Check/Raise/Fold
                        int betDiff = currentPlayer.chooseAction(biggestBet);
                        if (betDiff > 0)
                            biggestBet += betDiff;

                        // STEP 14: Show current state and move index to next player
                        showCurrentState();
                        currentPlayerIndex = nextPlayer(currentPlayerIndex);
                    }
                } while (!bettingConsensus(biggestBet));

                // STEP 15: New Community Card
                if(round<3) {
                    deck.discard();
                    communityCards.add(deck.draw());
                    showCurrentState();
                }
            }

            // STEP 16: Obtaining players' best hands and calculating this hand's pot
            List<BestHand> hands = new ArrayList<BestHand>();
            int pot = 0;
            for (Player p : players)
                if (p.isActive()) {
                    hands.add(new BestHand(p.getHoleCards(), communityCards, p));
                    pot += p.getBet();
                }

            // STEP 17: Showing the results and updating balances
            System.out.println(hands);
            Collections.sort(hands, Collections.reverseOrder());
            int numWinners = 1;
            while (numWinners < hands.size() && hands.get(numWinners - 1).equals(hands.get(numWinners)))
                numWinners++;
            if (numWinners > 1)
                System.out.println("TIE");
            else
                System.out.println(hands.get(0).getOwner().getName() + " WINS");
            for (int i = 0; i < numWinners; i++)
                hands.get(i).getOwner().addFunds(pot / numWinners);
            for (Player p : players) {
                if (p.isActive() && p.getFunds() == 0) {
                    System.out.println(p.getName() + " HAS LOST THE GAME");
                    p.setActive(false);
                }
            }

            if (countActivePlayers() > 1) {
                // STEP 18. Rotating the dealer chip
                players.get(dealer).setState(Player.State.DEFAULT);
                dealer = nextPlayer(dealer);
                players.get(dealer).setState(Player.State.DEALER);

                // STEP 19. Cleaning up before a new hand starts
                deck.newHand();
                communityCards = new ArrayList<Card>();
                for (Player p : players)
                    if (p.isActive())
                        p.newHand();
            }
        }
        // STEP 20. Endgame
        for (Player p : players)
            if (p.isActive())
                System.out.println("PLAYER " + p.getName() + " WINS THE GAME");

        /*** ADD LOOP TO RESTART THE GAME ***/
    }
}
