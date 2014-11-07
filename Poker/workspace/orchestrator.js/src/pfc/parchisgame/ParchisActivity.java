package pfc.parchisgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ojs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pfc.engine.PokerException;

/**
 * Created by fare on 07/11/14.
 */
public class ParchisActivity extends Activity {

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

    public void update(JSONArray players, JSONObject commonData){
        try{
            if(numPlayers == -1)
                numPlayers = players.length();
            for(int i=0;i<players.length();i++){
                ParchisPlayer p = new ParchisPlayer(players.getJSONObject(i));
                board.setPlayer(p,i);
            }
        } catch (JSONException e){
            throw new PokerException("TDS PTS",e);
        }
    }

    public void newGame(JSONObject initData){
        this.player = new ParchisPlayer("Prueba","avatar20",false);
        numPlayers = -1;
        board.newGame();
    }
}
