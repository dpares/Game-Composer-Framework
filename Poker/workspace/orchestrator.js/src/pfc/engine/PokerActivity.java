package pfc.engine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;
import com.ojs.capabilities.frameworkCapability.FrameworkCapability;
import com.ojs.helpers.SettingHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bmge.framework.Image;
import pfc.pokergame.BestHand;
import pfc.pokergame.Card;
import pfc.pokergame.Player;

/**
 * Created by fare on 29/09/14.
 */
public class PokerActivity extends Activity {

    public static PokerActivity instance;
    private static final int MAX_PLAYERS = 4;

    private TextView winnersLabel;
    private List<ImageView> communityCardImages;
    private List<List<View>> playerLayouts;
    private TextView betLabel;
    private TextView potLabel;
    private LinearLayout buttonLayout;

    private Player player;
    private List<Card> communityCards;
    private int biggestBet;
    private int currentBet;
    private int numPlayers;
    private int playerIndex;
    private int currentPot;

    private boolean pausedActivity = false;

    public static PokerActivity getInstance() {
        return PokerActivity.instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creating a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.poker_layout_new);

        winnersLabel = (TextView) findViewById(R.id.winnersLabel);
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

        instance = this;
        try {
            JSONObject initData = new JSONObject(this.getIntent().getStringExtra("init_data"));
            this.newGame(initData);
        } catch (JSONException e){
            throw new PokerException("Error parsing initData on onCreate", e);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayerState(JSONObject state) {
        this.player = new Player(state);
    }

    public JSONObject getCommonDataJSON() {
        try {
            return new JSONObject().put("biggest_bet", this.biggestBet).put("current_pot",
                    this.currentPot);
        } catch (JSONException e) {
            throw new PokerException("Error parsing commonData into JSON", e);
        }
    }

    public void update(JSONArray players, JSONObject commonData) {
        communityCards = new ArrayList<Card>();
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
            if(this.numPlayers != -1 && this.numPlayers != players.length()){ // A player left the game
                playerLayouts.get(this.numPlayers-1).get(0).setVisibility(View.GONE);
                playerLayouts.get(this.numPlayers-1).get(6).setVisibility(View.GONE);
                this.numPlayers = -1;
                Toast.makeText(this,"A player has left the game",Toast.LENGTH_SHORT).show();
            }
            for (int j = 0; j < players.length(); j++) {
                Player p = new Player(players.getJSONObject(j));
                if(numPlayers == -1) {
                    playerLayouts.get(j).get(0).setVisibility(View.VISIBLE);
                    playerLayouts.get(j).get(6).setVisibility(View.VISIBLE);
                }
                ((TextView) playerLayouts.get(j).get(1)).setText(p.getName());
                ((ImageView) playerLayouts.get(j).get(2)).setImageDrawable(p.getAvatarDrawable());
                if (this.numPlayers == -1 && p.getName().equals(this.player.getName()))
                    this.playerIndex = j; // Locate current player in JSON
                // Change name and avatar once profiles are implemented
                if (p.getState() == Player.State.FOLDED) {
                    LinearLayout layout = (LinearLayout) playerLayouts.get(j).get(0);
                    layout.setAlpha(0.5f);
                } else {
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
                } else if (this.playerIndex == j && this.player.getHoleCards().size() > 0) {
                    View holeCardsLayout = playerLayouts.get(j).get(4);
                    for (int k = 0; k < this.player.getHoleCards().size(); k++)
                        ((ImageView) ((LinearLayout) holeCardsLayout).getChildAt(k)).
                                setImageDrawable(this.player.getHoleCards().get(k).getDrawable());
                    holeCardsLayout.setVisibility(View.VISIBLE);

                }
            }
            if (this.numPlayers == -1)
                this.numPlayers = players.length();
        } catch (JSONException e) {
            throw new PokerException("Error updating PokerActivity", e);
        }
    }

    public void call(View v) {
        this.currentPot += this.biggestBet - player.getBet();
        player.call(this.biggestBet);
        this.finishTurn();
    }

    public void fold(View v) {
        player.fold();
        this.finishTurn();
    }

    public void increaseBet(View v) {
        if (this.currentBet + 100 <= player.getFunds()) {
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
        player.raise(this.currentBet, this.biggestBet);
        if (this.biggestBet < player.getBet())
            this.biggestBet += player.getBet() - this.biggestBet;
        this.finishTurn();
    }

    public void startStep(int phase, int step) {
        if (phase == 0) {
            if (player.getState() == Player.State.SMALL_BLIND ||
                    this.numPlayers == 2 && player.getState() == Player.State.DEALER) {
                player.call(100);
                this.currentPot += 100;
            } else if (player.getState() == Player.State.BIG_BLIND) {
                player.call(200);
                this.currentPot += 200;
            }
            finishTurn();
        } else {
            this.currentBet = this.biggestBet - player.getBet();
            if (this.currentBet < 100)
                this.currentBet = 100;
            betLabel.setText(new Integer(this.currentBet).toString());
            buttonLayout.setVisibility(View.VISIBLE);
        }
    }

    private void finishTurn() {
        buttonLayout.setVisibility(View.INVISIBLE);
        FrameworkCapability.endOfTurn();
    }

    public void showResults(JSONObject winners, JSONArray players, JSONObject commonData) {
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
                this.player.addFunds(commonData.getInt("current_pot") / winnerNames.length());
        } catch (JSONException e) {
            throw new PokerException("Error parsing commonData in showResults", e);
        }
    }

    public void newRound() {
        this.player.newHand();
        this.numPlayers = -1;
        winnersLabel.setVisibility(View.INVISIBLE);
        for (ImageView iv : communityCardImages)
            iv.setVisibility(View.INVISIBLE);
        for (List<View> pl : playerLayouts) {
            pl.get(0).setAlpha(1);
            pl.get(0).setVisibility(View.GONE);
            pl.get(6).setVisibility(View.GONE);
            for(int i=0;i<2;i++)
                ((ImageView) ((LinearLayout) pl.get(4)).getChildAt(i)).
                        setImageDrawable(this.getResources().getDrawable(R.drawable.cardback));
            pl.get(5).setVisibility(View.INVISIBLE);
        }
    }

    public void announceWinner(JSONArray players, Integer winner) {
        if (winner == playerIndex)
            winnersLabel.setText("You win!");
        else {
            try {
                String winnerName = players.getJSONObject(winner).getString("name");
                winnersLabel.setText(winnerName + " " + "wins");
            } catch (JSONException e) {
                throw new PokerException("Error when parsing players list on announceWinner", e);
            }
        }
    }

    public void askForRematch() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Rematch?").setMessage("Do you want a rematch?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FrameworkCapability.rematchAnswer(true);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FrameworkCapability.rematchAnswer(false);
            }
        }).show();
    }

    public void newGame(JSONObject initData) {
        this.currentBet = 100;
        this.biggestBet = 200;
        this.currentPot = 0;
        this.numPlayers = -1;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int initialFunds = initData.getInt("initial_funds");
            String name = prefs.getString("pref_player_name", "Player");
            String avatar = SettingHelpers.getStringValue("pref_player_avatar", this);
            boolean spectate = prefs.getBoolean("pref_player_spectate", false);
            this.player = new Player(initialFunds, name, avatar, spectate);
            this.newRound();
        } catch (JSONException e) {
            throw new PokerException("Error parsing initData on newGame", e);
        }
    }

    public void closeActivity(String reason) {
        Toast.makeText(this,reason,Toast.LENGTH_LONG).show();
        PokerActivity.instance = null;
        super.onBackPressed();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Leaving game").setMessage("Are you sure you want to leave the current game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FrameworkCapability.leaveGame();
                        PokerActivity.getInstance().closeActivity("You have left the game");
                    }
                }).setNegativeButton("No", null).show();
    }

    public void onPause() {
        super.onPause();
        FrameworkCapability.leaveGame();
        this.pausedActivity = true;
    }

    public void onResume() {
        super.onResume();
        if (pausedActivity) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Alert").setMessage("You have lost connection to the server")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PokerActivity.getInstance().closeActivity("You have left the game");
                        }
                    }).show();
        }
    }

}
