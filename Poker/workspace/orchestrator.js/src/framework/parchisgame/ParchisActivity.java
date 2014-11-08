package framework.parchisgame;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ojs.R;
import com.ojs.capabilities.frameworkCapability.FrameworkCapability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import framework.engine.FrameworkGameActivity;
import framework.engine.FrameworkGameException;

/**
 * Created by fare on 07/11/14.
 */
public class ParchisActivity extends FrameworkGameActivity {

    private ParchisView board;
    private TextView winnerLabel;
    private LinearLayout spinningWheel;
    
    private ImageView die; //reference to dice picture
    private Random rng=new Random(); //generate random numbers
    private SoundPool die_sound = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    private int sound_id; //Used to control sound stream return by SoundPool
    private Handler handler; //Post message to start roll
    private Timer timer=new Timer(); //Used to implement feedback to user
    private boolean rolling=false; //Is die rolling?
    private int lastRoll; //Last number rolled

    private int playerIndex;
    private int numPlayers;

    @Override
    public void onCreate(Bundle savedBundledInstance){
        super.onCreate(savedBundledInstance);

        instance = this;

        // Creating a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.parchis_layout);

        //load dice sound
        sound_id=die_sound.load(this,R.raw.shake_dice,1);
        //get reference to image widget
        this.die = (ImageView) findViewById(R.id.die);
        //link handler to callback
        handler=new Handler(callback);

        board = (ParchisView) findViewById(R.id.board);
        winnerLabel = (TextView) findViewById(R.id.winnerLabel);
        spinningWheel = (LinearLayout) findViewById(R.id.spinningWheel);
        
        this.newRound();
    }

    //User pressed dice, lets start
    public void HandleClick(View arg0) {
        if(!rolling) {
            rolling=true;
            //Show rolling image
            die.setImageResource(R.drawable.dice3droll);
            //Start rolling sound
            die_sound.play(sound_id,1.0f,1.0f,0,0,1.0f);
            //Pause to allow image to update
            timer.schedule(new Roll(), 400);
        }
    }

    //When pause completed message sent to callback
    class Roll extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    //Receives message from timer to start dice roll
    Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            //Get roll result
            //Remember nextInt returns 0 to 5 for argument of 6
            //hence + 1
            lastRoll = rng.nextInt(6)+1;
            switch(lastRoll) {
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
                default:
            }
            rolling=false;	//user can press again
            finishTurn();
            return true;
        }
    };

    @Override
    public JSONObject getCommonDataJSON() {
        try{
            return new JSONObject().put("last_roll",this.lastRoll);
        } catch (JSONException e){
            throw new FrameworkGameException("Error parsing commonData JSON",e);
        }
    }

    @Override
    public void update(JSONArray players, JSONObject commonData){
        try{
            if(numPlayers == -1)
                numPlayers = players.length();
            List<ParchisPlayer> playersList = new ArrayList<ParchisPlayer>();
            for(int i=0;i<players.length();i++)
                playersList.add(new ParchisPlayer(players.getJSONObject(i)));
            board.updatePlayers(playersList);
        } catch (JSONException e){
            throw new FrameworkGameException("Error parsing JSON in update",e);
        }
    }

    @Override
    public void startStep(int phase, int step) {
        this.die.setVisibility(View.VISIBLE);
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
        this.die.setVisibility(View.INVISIBLE);
    }

    @Override
    public void announceWinner(JSONArray players, Integer winner) {
        if(players.length() == 0)
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
    }

    @Override
    public void newGame(JSONObject initData){
        super.newGame(initData);
        numPlayers = -1;
        if(this.board != null)
            this.newRound();
    }

    private void finishTurn() {
        spinningWheel.setVisibility(View.VISIBLE);
        FrameworkCapability.endOfTurn();
    }

    @Override
    protected void onPause(){
        super.onPause();
        die_sound.pause(sound_id);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        timer.cancel();
    }
}
