package framework.pokergame;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ojs.R;
import com.ojs.capabilities.frameworkCapability.FrameworkCapability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import framework.engine.FrameworkGameActivity;
import framework.engine.FrameworkGameException;

/**
 * Created by fare on 29/09/14.
 */
public class PokerActivity extends FrameworkGameActivity {
    private static final int MAX_PLAYERS = 4;

    private TextView winnersLabel;
    private List<ImageView> communityCardImages;
    private List<List<View>> playerLayouts;
    private TextView betLabel;
    private TextView potLabel;
    private LinearLayout buttonLayout;
    private LinearLayout spinningWheel;

    private List<Card> communityCards;
    private int biggestBet;
    private int currentBet;
    private int numPlayers;
    private int playerIndex;
    private int currentPot;

    private boolean showingResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poker_layout_new);

        instance = this;
        showingResults = false;

        winnersLabel = (TextView) findViewById(R.id.winnersLabel);
        winnersLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/badaboom.TTF"));
        communityCardImages = new ArrayList<ImageView>();
        communityCardImages.add((ImageView) findViewById(R.id.communityCard1));
        communityCardImages.add((ImageView) findViewById(R.id.communityCard2));
        communityCardImages.add((ImageView) findViewById(R.id.communityCard3));
        communityCardImages.add((ImageView) findViewById(R.id.communityCard4));
        communityCardImages.add((ImageView) findViewById(R.id.communityCard5));
        playerLayouts = new ArrayList<List<View>>();
        Context ctx = FrameworkCapability.getContext();
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            List<View> playerElements = new ArrayList<View>();
            playerElements.add(findViewById(ctx.getResources().
                    getIdentifier("player" + i + "Layout", "id", ctx.getPackageName())));
            playerElements.add(findViewById(ctx.getResources().
                    getIdentifier("player" + i + "_name", "id", ctx.getPackageName())));
            playerElements.add(findViewById(ctx.getResources().
                    getIdentifier("player" + i + "_avatar", "id", ctx.getPackageName())));
            playerElements.add(findViewById(ctx.getResources().
                    getIdentifier("player" + i + "_funds", "id", ctx.getPackageName())));
            playerElements.add(findViewById(ctx.getResources().
                    getIdentifier("player" + i + "_holeCardsLayout", "id", ctx.getPackageName())));
            playerElements.add(findViewById(ctx.getResources().
                    getIdentifier("player" + i + "_handType", "id", ctx.getPackageName())));
            playerElements.add(findViewById(ctx.getResources().
                    getIdentifier("player" + i + "FrameLayout", "id", ctx.getPackageName())));
            playerLayouts.add(playerElements);
        }
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        betLabel = (TextView) findViewById(R.id.currentBet);
        potLabel = (TextView) findViewById(R.id.potLabel);
        spinningWheel = (LinearLayout) findViewById(R.id.loadingPanel);

        this.newRound();
    }

    @Override
    public JSONObject getCommonDataJSON() {
        try {
            return new JSONObject().put("biggest_bet", this.biggestBet).put("current_pot",
                    this.currentPot);
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing commonData into JSON", e);
        }
    }

    @Override
    public void update(JSONArray players, JSONObject commonData) {
        communityCards = new ArrayList<Card>();
        boolean skippingTurns = this.player.isActive(); /* Turns are skipped when every player is ALL_IN or FOLDED
        Only taken into account if the player is active */
        boolean showToast = false; // Message shown after a player disconnects or loses
        try {
            this.biggestBet = commonData.getInt("biggest_bet");
            this.currentPot = commonData.getInt("current_pot");

            /* Updating the pot */
            this.potLabel.setText(new Integer(this.currentPot).toString());

            /* Community cards treatment */
            JSONArray cards = commonData.getJSONArray("community_cards");
            for (int i = 0; i < cards.length(); i++)
                communityCards.add(new Card(cards.getJSONObject(i)));
            int i = 0;
            while (i < communityCards.size()) {
                if (communityCardImages.get(i).getVisibility() == View.INVISIBLE) {
                    communityCardImages.get(i).setVisibility(View.VISIBLE);
                    communityCardImages.get(i).setImageDrawable(communityCards.get(i).getDrawable());
                }
                i++;

            }

            /* Player treatment */
            if (this.numPlayers != -1 && this.numPlayers != players.length()) { // A player left the game
                int numPlayersLeft = this.numPlayers - players.length();
                for (int j = 1; j <= numPlayersLeft; j++) {
                    playerLayouts.get(this.numPlayers - j).get(0).setVisibility(View.GONE);
                    playerLayouts.get(this.numPlayers - j).get(6).setVisibility(View.GONE);
                }
                this.numPlayers = -1;
                this.playerIndex = -1;
                showToast = true;
            }
            for (int j = 0; j < players.length(); j++) {
                PokerPlayer p = new PokerPlayer(players.getJSONObject(j));
                if (spinningWheel.getVisibility() == View.VISIBLE && skippingTurns &&
                        p.getState() != PokerPlayer.PokerState.FOLDED &&
                        p.getState() != PokerPlayer.PokerState.ALL_IN) {
                    skippingTurns = false;
                }
                if (numPlayers == -1) {
                    playerLayouts.get(j).get(0).setVisibility(View.VISIBLE);
                    playerLayouts.get(j).get(6).setVisibility(View.VISIBLE);
                }
                ((TextView) playerLayouts.get(j).get(1)).setText(p.getName());
                ((ImageView) playerLayouts.get(j).get(2)).setImageDrawable(p.getAvatarDrawable());
                if (this.numPlayers == -1 && p.getName().equals(this.player.getName()))
                    this.playerIndex = j; // Locate current player in JSON
                // Change name and avatar once profiles are implemented
                if (p.getState() == PokerPlayer.PokerState.FOLDED)
                    playerLayouts.get(j).get(0).setAlpha(0.5F);
                else {
                    TextView funds = (TextView) playerLayouts.get(j).get(3);
                    funds.setText(new Integer(p.getFunds()).toString());
                }
                if (players.getJSONObject(j).has("best_hand")) {
                    JSONObject bestHand = players.getJSONObject(j).getJSONObject("best_hand");
                    View v = playerLayouts.get(j).get(4);
                    ((TextView) playerLayouts.get(j).get(5)).setText(
                            BestHand.Type.values()[bestHand.getInt("type")].toString());
                    JSONArray holeCards = players.getJSONObject(j).getJSONArray("hole_cards");
                    for (int k = 0; k < holeCards.length(); k++)
                        ((ImageView) ((ViewGroup) v).getChildAt(k)).setImageDrawable(
                                new Card(holeCards.getJSONObject(k)).getDrawable());
                    playerLayouts.get(j).get(5).setVisibility(View.VISIBLE);
                    v.setVisibility(View.VISIBLE);
                } else if (this.playerIndex == j &&
                        ((PokerPlayer) this.player).getHoleCards().size() > 0) {
                    View holeCardsLayout = playerLayouts.get(j).get(4);
                    for (int k = 0; k < ((PokerPlayer) this.player).getHoleCards().size(); k++)
                        ((ImageView) ((LinearLayout) holeCardsLayout).getChildAt(k)).
                                setImageDrawable(((PokerPlayer) this.player).getHoleCards()
                                        .get(k).getDrawable());
                    holeCardsLayout.setVisibility(View.VISIBLE);

                }
            }
            if (this.numPlayers == -1)
                this.numPlayers = players.length();
            if (skippingTurns)
                spinningWheel.setVisibility(View.GONE);
            if (showToast) {
                String toastMsg;
                if (showingResults)
                    toastMsg = playerIndex == -1 ? "You have lost" : "A player has lost";
                else
                    toastMsg = "A player has left the game";
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing JSON in update", e);
        }
    }

    @Override
    public void startStep(int phase, int step) {
        if (phase == 0) {
            if (player.getState() == PokerPlayer.PokerState.SMALL_BLIND ||
                    this.numPlayers == 2 && player.getState() == PokerPlayer.PokerState.DEALER) {
                ((PokerPlayer) player).call(100);
                this.currentPot += 100;
            } else if (player.getState() == PokerPlayer.PokerState.BIG_BLIND) {
                ((PokerPlayer) player).call(200);
                this.currentPot += 200;
            }
            finishTurn();
        } else {
            this.currentBet = this.biggestBet - ((PokerPlayer) player).getBet();
            if (this.currentBet < 100)
                this.currentBet = 100;
            betLabel.setText(new Integer(this.currentBet).toString());
            buttonLayout.setVisibility(View.VISIBLE);
            spinningWheel.setVisibility(View.GONE);
        }
    }

    @Override
    public void showResults(JSONObject winners, JSONArray players, JSONObject commonData) {
        showingResults = true;
        spinningWheel.setVisibility(View.GONE);
        try {
            boolean isWinner = false;
            JSONArray winnerNames = winners.getJSONArray("data");
            String winnersAnnouncement;
            if (winnerNames.length() == 0)
                winnersAnnouncement = "No winners";
            else {
                winnersAnnouncement = winnerNames.length() > 1 ? "Players " : "Player ";
                for (int i = 0; i < winnerNames.length(); i++) {
                    String name = winnerNames.getString(i);
                    winnersAnnouncement += name + " ";
                    if (this.player.getName().equals(name))
                        isWinner = true;
                }
                winnersAnnouncement += winnerNames.length() > 1 ? "win" : "wins";
            }
            winnersLabel.setText(winnersAnnouncement);
            winnersLabel.setVisibility(View.VISIBLE);
            if (isWinner)
                ((PokerPlayer) this.player).addFunds(
                        commonData.getInt("current_pot") / winnerNames.length());
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing commonData in showResults", e);
        }
    }

    @Override
    public void newRound() {
        ((PokerPlayer) this.player).newRound();
        this.numPlayers = -1;
        winnersLabel.setVisibility(View.INVISIBLE);
        for (ImageView iv : communityCardImages)
            iv.setVisibility(View.INVISIBLE);
        for (List<View> pl : playerLayouts) {
            pl.get(0).setAlpha(1);
            pl.get(0).setVisibility(View.GONE);
            pl.get(6).setVisibility(View.GONE);
            for (int i = 0; i < 2; i++)
                ((ImageView) ((LinearLayout) pl.get(4)).getChildAt(i)).
                        setImageDrawable(this.getResources().getDrawable(R.drawable.cardback));
            pl.get(5).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void announceWinner(JSONArray players, Integer winner) {
        if (winner == playerIndex)
            winnersLabel.setText("You win!");
        else {
            try {
                String winnerName = players.getJSONObject(winner).getString("name");
                winnersLabel.setText(winnerName + " " + "wins");
            } catch (JSONException e) {
                throw new FrameworkGameException("Error when parsing players list on announceWinner", e);
            }
        }
    }

    @Override
    public void newGame(JSONObject initData) {
        super.newGame(initData);

        this.currentBet = 100;
        this.biggestBet = 200;
        this.currentPot = 0;
        if(this.winnersLabel != null)
            this.newRound();
    }

    public void call(View v) {
        this.currentPot += this.biggestBet - ((PokerPlayer) this.player).getBet();
        ((PokerPlayer) player).call(this.biggestBet);
        this.finishTurn();
    }

    public void fold(View v) {
        ((PokerPlayer) this.player).fold();
        this.finishTurn();
    }

    public void increaseBet(View v) {
        if (this.currentBet + 100 <= ((PokerPlayer) this.player).getFunds()) {
            this.currentBet += 100;
            betLabel.setText(new Integer(this.currentBet).toString());
        }
    }

    public void decreaseBet(View v) {
        if (this.currentBet - 100 >= this.biggestBet) {
            this.currentBet -= 100;
            betLabel.setText(new Integer(this.currentBet).toString());
        }
    }

    public void newBet(View v) {
        this.currentPot += this.currentBet;
        ((PokerPlayer) this.player).raise(this.currentBet, this.biggestBet);
        if (this.biggestBet < ((PokerPlayer) this.player).getBet())
            this.biggestBet += ((PokerPlayer) this.player).getBet() - this.biggestBet;
        this.finishTurn();
    }

    private void finishTurn() {
        buttonLayout.setVisibility(View.INVISIBLE);
        spinningWheel.setVisibility(View.VISIBLE);
        FrameworkCapability.endOfTurn();
    }

}
