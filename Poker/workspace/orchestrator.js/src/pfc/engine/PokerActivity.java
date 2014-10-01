package pfc.engine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ojs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pfc.pokergame.Card;
import pfc.pokergame.Player;

/**
 * Created by fare on 29/09/14.
 */
public class PokerActivity extends Activity {

    public static PokerActivity instance;

    private TextView statusLabel;
    private List<TextView> communityCardImages;

    private Player player;
    private List<Card> communityCards;
    private int biggestBet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creating a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.poker_layout);

        statusLabel = (TextView)findViewById(R.id.dealerLabel);
        communityCardImages = new ArrayList<TextView>();
        communityCardImages.add((TextView)findViewById(R.id.commonCard1));
        communityCardImages.add((TextView)findViewById(R.id.commonCard2));
        communityCardImages.add((TextView)findViewById(R.id.commonCard3));
        communityCardImages.add((TextView)findViewById(R.id.commonCard4));
        communityCardImages.add((TextView)findViewById(R.id.commonCard5));
        instance = this;
    }

    public static PokerActivity getInstance(){
        return PokerActivity.instance;
    }

    public void update(Player p){
        if(player == null)
            player = p;
        statusLabel.setText(p.getState().toString());
    }

    public void update(JSONArray players, JSONObject commonData){
        communityCards = new ArrayList<Card>();
        try {
            biggestBet = commonData.getInt("biggestBet");
            JSONArray cards = commonData.getJSONArray("communityCards");
            for (int i = 0; i < cards.length(); i++)
                communityCards.add(new Card(cards.getJSONObject(i)));
            int i = 0;
            while(i < communityCards.size()){
                if(communityCardImages.get(i).getVisibility() == View.GONE){
                    communityCardImages.get(i).setVisibility(View.VISIBLE);
                    communityCardImages.get(i).setText(communityCards.get(i).toString());
                }
                i++;

            }
        } catch (JSONException e){
            throw new PokerException("Error updating PokerActivity", e);
        }
    }

    public void call(){
        statusLabel.setText("CALLED");
    }
}
