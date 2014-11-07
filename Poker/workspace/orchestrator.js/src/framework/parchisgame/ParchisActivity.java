package framework.parchisgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ojs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import framework.engine.FrameworkGameActivity;
import framework.engine.FrameworkGameException;

/**
 * Created by fare on 07/11/14.
 */
public class ParchisActivity extends FrameworkGameActivity {

    private static ParchisView board;

    private ParchisPlayer player;
    private int numPlayers;

    @Override
    public void onCreate(Bundle savedBundledInstance){
        super.onCreate(savedBundledInstance);

        // Creating a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.parchis_layout);

        board = ParchisView.getInstance();
        this.newGame(null);
    }

    @Override
    public JSONObject getCommonDataJSON() {
        return new JSONObject();
    }

    @Override
    public void update(JSONArray players, JSONObject commonData){
        try{
            if(numPlayers == -1)
                numPlayers = players.length();
            for(int i=0;i<players.length();i++){
                ParchisPlayer p = new ParchisPlayer(players.getJSONObject(i));
                board.setPlayer(p,i);
            }
        } catch (JSONException e){
            throw new FrameworkGameException("Error parsing JSON in update",e);
        }
    }

    @Override
    public void startStep(int phase, int step) {

    }

    @Override
    public void showResults(JSONObject winners, JSONArray players, JSONObject commonData) {

    }

    @Override
    public void newRound() {
        ((ParchisPlayer)this.player).newRound();
    }

    @Override
    public void announceWinner(JSONArray players, Integer winner) {

    }

    public void newGame(JSONObject initData){
        this.player = new ParchisPlayer(null,"Prueba","avatar20",false);
        numPlayers = -1;
        board.newGame();
    }
}
