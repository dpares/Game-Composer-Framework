package pfc.engine;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ojs.R;
import com.ojs.capabilities.frameworkCapability.FrameworkCapability;

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
    private List<LinearLayout> playerLayouts;
    private LinearLayout holeCardsLayout;
    private TextView betLabel;
    private LinearLayout buttonLayout;

    private Player player;
    private List<Card> communityCards;
    private int biggestBet;
    private int currentBet;

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
        communityCardImages.add((TextView)findViewById(R.id.communityCard1));
        communityCardImages.add((TextView)findViewById(R.id.communityCard2));
        communityCardImages.add((TextView)findViewById(R.id.communityCard3));
        communityCardImages.add((TextView)findViewById(R.id.communityCard4));
        communityCardImages.add((TextView)findViewById(R.id.communityCard5));
        playerLayouts = new ArrayList<LinearLayout>();
        playerLayouts.add((LinearLayout)findViewById(R.id.player1Layout));
        playerLayouts.add((LinearLayout)findViewById(R.id.player2Layout));
        holeCardsLayout = (LinearLayout)findViewById(R.id.holeCardsLayout);
        buttonLayout = (LinearLayout)findViewById(R.id.buttonLayout);
        betLabel = (TextView)findViewById(R.id.currentBet);

        instance = this;
        this.currentBet = 100;
        this.biggestBet = 200;
        try {
            JSONObject initData = new JSONObject(this.getIntent().getStringExtra("init_data"));
            int initialFunds = initData.getInt("initial_funds");
            this.player = new Player(initialFunds);
        }catch(JSONException e){
            throw new PokerException("Error parsing initial data", e);
        }
    }

    public static PokerActivity getInstance(){
        return PokerActivity.instance;
    }

    public Player getPlayer(){
        return this.player;
    }

    public void setPlayerState(JSONObject state){
        this.player = new Player(state);
    }

    public JSONObject getCommonDataJSON(){
        try{
            return new JSONObject().put("biggest_bet",this.biggestBet);
        }catch (JSONException e){
            throw new PokerException("Error parsing commonData into JSON",e);
        }
    }

    public void update(JSONArray players, JSONObject commonData){
        communityCards = new ArrayList<Card>();
        try {
            this.biggestBet = commonData.getInt("biggest_bet");

            /* Community cards treatment */
            JSONArray cards = commonData.getJSONArray("community_cards");
            for (int i = 0; i < cards.length(); i++)
                communityCards.add(new Card(cards.getJSONObject(i)));
            int i = 0;
            while(i < communityCards.size()){
                if(communityCardImages.get(i).getVisibility() == View.INVISIBLE){
                    communityCardImages.get(i).setVisibility(View.VISIBLE);
                    communityCardImages.get(i).setText(communityCards.get(i).toString());
                }
                i++;

            }

            /* Player treatment */
            for(int j = 0;j<players.length();j++){
                Player p = new Player(players.getJSONObject(j));
                LinearLayout layout = playerLayouts.get(j);
                layout.setVisibility(View.VISIBLE);
                View v = layout.getChildAt(1);
                ((TextView)((ViewGroup)v).getChildAt(0)).setText("Funds: " + p.getFunds());
                ((TextView)((ViewGroup)v).getChildAt(1)).setText("Current Bet: " + p.getBet());
                ((TextView)((ViewGroup)v).getChildAt(2)).setText("Status: " + p.getState().toString());
            }
            if(this.player.getHoleCards().size() > 0){
                holeCardsLayout.setVisibility(View.VISIBLE);
                for(int k = 0;k<this.player.getHoleCards().size();k++)
                    ((TextView)((ViewGroup)holeCardsLayout).getChildAt(k)).
                            setText(this.player.getHoleCards().get(k).toString());
            }
        } catch (JSONException e){
            throw new PokerException("Error updating PokerActivity", e);
        }
    }

    public void call(View v){
        player.call(this.biggestBet);
        this.finishTurn();
    }

    public void fold(View v) {
        player.fold();
        this.finishTurn();
    }

    public void increaseBet(View v){
        if(this.currentBet+100 <= player.getFunds()){
            this.currentBet+=100;
            betLabel.setText(new Integer(this.currentBet).toString());
        }
    }

    public void decreaseBet(View v){
        if(this.currentBet-100 >= this.biggestBet){
            this.currentBet-=100;
            betLabel.setText(new Integer(this.currentBet).toString());
        }
    }

    public void newBet(View v){
        player.raise(this.currentBet,this.biggestBet);
        this.finishTurn();
    }

    public void startStep(int phase, int step){
        buttonLayout.setVisibility(View.VISIBLE);
    }

    private void finishTurn(){
        buttonLayout.setVisibility(View.INVISIBLE);
        this.currentBet = 100;
        betLabel.setText(new Integer(this.currentBet).toString());
        FrameworkCapability.endOfTurn();
    }

}
