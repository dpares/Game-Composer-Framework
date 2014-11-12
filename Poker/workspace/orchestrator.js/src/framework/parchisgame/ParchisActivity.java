package framework.parchisgame;

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
    private boolean skipTurn;
    private Pawn lastMovedPawn;
    private int currentStep;

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
        spinningWheel = (LinearLayout) findViewById(R.id.spinningWheel);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        playerIndex = -1;
        showLastroll = false;
        skipTurn = false;
        this.newRound();
    }

    @Override
    public JSONObject getCommonDataJSON() {
        try {
            JSONObject res = new JSONObject();
            res.put("last_roll", this.roll);
            res.put("last_player", this.playerIndex);
            res.put("skip_turn", this.skipTurn);
            return res;
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing commonData JSON", e);
        }
    }

    @Override
    public void update(JSONArray players, JSONObject commonData) {
        try {
            if (numPlayers == -1)
                numPlayers = players.length();
            boolean skip = commonData.getBoolean("skip_turn");
            if (!skip) {
                this.lastPlayerIndex = commonData.getInt("last_player");
                if (this.lastPlayerIndex != -1 && this.lastPlayerIndex != this.playerIndex) {
                    this.die.setVisibility(View.VISIBLE);
                    this.spinningWheel.setVisibility(View.INVISIBLE);
                    this.lastRoll = commonData.getInt("last_roll");
                    this.showLastroll = true;
                    rollDie(null);
                }
                List<ParchisPlayer> playersList = new ArrayList<ParchisPlayer>();
                for (int i = 0; i < players.length(); i++)
                    playersList.add(new ParchisPlayer(players.getJSONObject(i)));
                if (playerIndex == -1)
                    playerIndex = playersList.indexOf(this.player);
                board.updatePlayers(playersList, false);
            }
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing JSON in update", e);
        }
    }

    @Override
    public void startStep(int phase, int step) {
        currentStep = step;
        if (step == 0 || this.roll == 6) {
            this.skipTurn = false;
            this.spinningWheel.setVisibility(View.INVISIBLE);
            die.setImageResource(R.drawable.dice3droll);
            this.die.setVisibility(View.VISIBLE);
            this.rolled = false;    //user can press again
        } else {
            this.skipTurn = true;
            this.finishTurn();
        }
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
                        Thread.sleep(1000);
                        if (showLastroll)
                            showLastroll = false;
                        else
                            showNextMove(null);
                    } catch (Exception e) {
                        throw new FrameworkGameException("Error waiting for die roll", e);
                    }
                }
            }, 10);
        }
    }

    public void confirmMove(View v) {
        lastMovedPawn = board.confirmMove();
        ((ParchisPlayer) this.player).getPawns().set(lastMovedPawn.getNumber(), lastMovedPawn);
        this.finishTurn();
    }

    public void showNextMove(View v) {
        if(currentStep == 2 && roll == 6 && this.lastMovedPawn != null) {
            board.returnPawnToHome((ParchisPlayer) this.player, this.lastMovedPawn);
            this.confirmMove(v);
        } else {
            boolean canMove = board.showNextMove(this.playerIndex, this.roll);
            if (!canMove) {
                Toast.makeText(FrameworkCapability.getContext(), "You can't move in this turn",
                        Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finishTurn();
                    }
                }, 1000);
            } else
                this.buttonLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showResults(JSONObject winners, JSONArray players, JSONObject commonData) {
        this.spinningWheel.setVisibility(View.INVISIBLE);
    }

    @Override
    public void newRound() {
        this.player.newRound();
        this.board.newGame();
        this.numPlayers = -1;
        this.winnerLabel.setVisibility(View.INVISIBLE);
    }

    @Override
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

    private void finishTurn() {
        if(this.currentStep == 2)
            this.lastMovedPawn = null;
        this.die.setVisibility(View.INVISIBLE);
        this.buttonLayout.setVisibility(View.INVISIBLE);
        this.spinningWheel.setVisibility(View.VISIBLE);
        FrameworkCapability.endOfTurn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        die_sound.pause(sound_id);
    }
}
