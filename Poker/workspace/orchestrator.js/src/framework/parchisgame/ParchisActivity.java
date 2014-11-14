package framework.parchisgame;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.Random;

import framework.engine.FrameworkGameActivity;
import framework.engine.FrameworkGameException;
import framework.engine.FrameworkPlayer;

/**
 * Created by fare on 07/11/14.
 */
public class ParchisActivity extends FrameworkGameActivity {

    private ParchisView board;
    private TextView winnerLabel;
    private LinearLayout spinningWheel;
    private LinearLayout buttonLayout;

    private ImageView die; //reference to dice picture
    private Random rng = new Random(); //generate random numbers
    private SoundPool die_sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    private int sound_id; //Used to control sound stream return by SoundPool
    private boolean rolled = false; //Is die rolled?
    private int roll; //Last number rolled by the player

    private int playerIndex;
    private int numPlayers;
    private boolean showLastroll;
    private int lastRoll;
    private int lastPlayerIndex;
    private Pawn lastMovedPawn;
    private int sixsRolled;
    private int moveCode;
    private boolean showingResults;

    @Override
    public void onCreate(Bundle savedBundledInstance) {
        super.onCreate(savedBundledInstance);

        instance = this;

        // Creating a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.parchis_layout);

        //load dice sound
        sound_id = die_sound.load(this, R.raw.shake_dice, 1);

        this.die = (ImageView) findViewById(R.id.die);
        board = (ParchisView) findViewById(R.id.board);
        winnerLabel = (TextView) findViewById(R.id.winnerLabel);
        winnerLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/badaboom.TTF"));
        spinningWheel = (LinearLayout) findViewById(R.id.spinningWheel);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        playerIndex = -1;
        showLastroll = false;
        this.newRound();
    }

    @Override
    public JSONObject getCommonDataJSON() {
        try {
            JSONObject res = new JSONObject();
            res.put("last_roll", this.roll);
            res.put("last_player", this.playerIndex);
            return res;
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing commonData JSON", e);
        }
    }

    @Override
    public void update(JSONArray players, JSONObject commonData) {
        try {
            boolean disconnection = false;
            if (numPlayers == -1)
                numPlayers = players.length();
            this.lastPlayerIndex = commonData.getInt("last_player");

            if (players.length() != this.numPlayers) { // Handle disconnection
                playerIndex = -1;
                this.numPlayers = players.length();
                disconnection = true;
            }
            if (this.lastPlayerIndex != -1 && this.lastPlayerIndex != this.playerIndex &&
                    !disconnection) {
                this.die.setVisibility(View.VISIBLE);
                this.spinningWheel.setVisibility(View.INVISIBLE);
                this.winnerLabel.setText(this.playersList.get(lastPlayerIndex).getName() + "'s turn");
                this.winnerLabel.setVisibility(View.VISIBLE);
                this.lastRoll = commonData.getInt("last_roll");
                this.showLastroll = true;
                rollDie(null);
            }
            this.playersList = new ArrayList<FrameworkPlayer>();
            List<ParchisPlayer> auxList = new ArrayList<ParchisPlayer>();
            for (int i = 0; i < players.length(); i++) {
                ParchisPlayer pp = new ParchisPlayer(players.getJSONObject(i));
                this.playersList.add(pp);
                auxList.add(pp);
            }
            if (playerIndex == -1)
                playerIndex = this.playersList.indexOf(this.player);
            if (this.lastPlayerIndex != this.playerIndex && this.lastRoll == 20) {
                this.player = playersList.get(playerIndex);
                board.updatePlayersEatenPawn((ParchisPlayer) this.player);
            }
            board.updatePlayers(auxList, disconnection);
            if (disconnection) {
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spinningWheel.setVisibility(View.INVISIBLE);
                winnerLabel.setText("Your turn");
                winnerLabel.setVisibility(View.VISIBLE);
                if (moveCode == 0) {
                    die.setImageResource(R.drawable.dice3droll);
                    die.setVisibility(View.VISIBLE);
                    rolled = false;    //user can press again
                } else
                    showNextMove(null);
            }
        }, 500);
    }

    public void rollDie(View v) {
        if (!rolled || showLastroll) {
            rolled = true;
            //Show rolling image
            die.setImageResource(R.drawable.dice3droll);
            //Start rolling sound
            die_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);
            //Pause to allow image to update
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        if (showLastroll)
                            roll = lastRoll;
                        else
                            roll = rng.nextInt(6) + 1;
                        if (roll <= 6) {
                            switch (roll) {
                                case 1:
                                    die.setImageResource(R.drawable.one);
                                    break;
                                case 2:
                                    die.setImageResource(R.drawable.two);
                                    break;
                                case 3:
                                    die.setImageResource(R.drawable.three);
                                    break;
                                case 4:
                                    die.setImageResource(R.drawable.four);
                                    break;
                                case 5:
                                    die.setImageResource(R.drawable.five);
                                    break;
                                case 6:
                                    die.setImageResource(R.drawable.six);
                                    break;
                            }
                            if (!showLastroll && roll == 6)
                                sixsRolled++;
                        }
                        if (showLastroll)
                            showLastroll = false;
                        else {
                            Thread.sleep(1000);
                            showNextMove(null);
                        }
                    } catch (Exception e) {
                        throw new FrameworkGameException("Error waiting for die roll", e);
                    }
                }
            }, 10);
        }
    }

    private ParchisPlayer findPlayerByColour(int colour) {
        ParchisPlayer res = null;
        int i = 0;
        while (res == null && i < this.playersList.size()) {
            if (((ParchisPlayer) this.playersList.get(i)).getColour().ordinal() == colour)
                res = (ParchisPlayer) this.playersList.get(i);
            else
                i++;
        }
        return res.clone();
    }

    public void confirmMove(View v) {
        Pair<Integer, Pawn> move = board.confirmMove();
        lastMovedPawn = move.second;
        moveCode = move.first;
        ((ParchisPlayer) this.player).getPawns().set(lastMovedPawn.getNumber(), lastMovedPawn);
        if (moveCode == 2) {
            this.playersList.set(this.playersList.indexOf(this.player), this.player);
            Pawn aux = board.getEatenPawn();
            Pawn eatenPawn = new Pawn(aux.getColour(), Pawn.INITIAL_SQUARE, aux.getNumber());
            ParchisPlayer eatenPlayer = findPlayerByColour(eatenPawn.getColour());
            int index = this.playersList.indexOf(eatenPlayer);
            eatenPlayer.getPawns().set(eatenPawn.getNumber(), eatenPawn);
            this.playersList.set(index, eatenPlayer);
        }
        this.finishTurn(moveCode == 2, moveCode != 0 || roll == 6 && sixsRolled < 3 ||
                ((roll > 6 && lastRoll == 6) && (roll != 10 ||
                        ((ParchisPlayer) this.player).getFinishedPawns() < 4)));
    }

    public void showNextMove(View v) {
        if (sixsRolled == 3 && roll == 6 && this.lastMovedPawn != null) {
            board.returnPawnToHome(this.lastMovedPawn);
            this.confirmMove(v);
        } else {
            if (moveCode != 0)
                this.roll = 10 * moveCode;
            boolean canMove = board.showNextMove(this.playerIndex, this.roll);
            if (!canMove) {
                Toast.makeText(FrameworkCapability.getContext(), "You can't move in this turn",
                        Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finishTurn(false, roll == 6 || ((roll > 6 && lastRoll == 6) &&
                                (roll!=10 || ((ParchisPlayer)player).getFinishedPawns() < 4)));
                    }
                }, 1000);
            } else
                this.buttonLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showResults(JSONObject winners, JSONArray players, JSONObject commonData) {
        this.spinningWheel.setVisibility(View.INVISIBLE);
        showingResults = true;
        this.winnerLabel.setVisibility(View.INVISIBLE);
        this.spinningWheel.setVisibility(View.INVISIBLE);
    }

    @Override
    public void newRound() {
        this.player.newRound();
        this.board.newGame();
        this.numPlayers = -1;
        moveCode = 0;
        sixsRolled = 0;
        showingResults = false;
        this.winnerLabel.setVisibility(View.INVISIBLE);
    }

    @Override // ¿Cambiar número por nombre?
    public void announceWinner(JSONArray players, Integer winner) {
        if (players.length() == 0)
            this.winnerLabel.setText("No winners");
        if (winner == playerIndex)
            this.winnerLabel.setText("You win!");
        else {
            try {
                String winnerName = players.getJSONObject(winner).getString("name");
                this.winnerLabel.setText(winnerName + " " + "wins");
            } catch (JSONException e) {
                throw new FrameworkGameException("Error when parsing players list on announceWinner", e);
            }
        }
        this.winnerLabel.setVisibility(View.VISIBLE);
    }

    @Override
    public void newGame(JSONObject initData) {
        super.newGame(initData);

        this.numPlayers = -1;
        if (this.board != null)
            this.newRound();
    }

    private void finishTurn(boolean playersListModified, boolean additionalStep) {
        if (!additionalStep) {
            this.lastMovedPawn = null;
            moveCode = 0;
            sixsRolled = 0;
        }
        this.die.setVisibility(View.INVISIBLE);
        this.buttonLayout.setVisibility(View.INVISIBLE);
        this.spinningWheel.setVisibility(View.VISIBLE);
        if (!additionalStep)
            this.winnerLabel.setVisibility(View.INVISIBLE);
        FrameworkCapability.endOfTurn(playersListModified, additionalStep);
    }

    @Override
    protected void onPause() {
        super.onPause();
        die_sound.pause(sound_id);
    }
}
