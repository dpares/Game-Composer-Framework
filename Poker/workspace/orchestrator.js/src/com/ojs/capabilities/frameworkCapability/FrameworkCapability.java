package com.ojs.capabilities.frameworkCapability;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pfc.engine.PokerException;
import pfc.engine.PokerActivity;
import pfc.pokergame.Player;
/**
 * Created by fare on 29/09/14.
 */
public class FrameworkCapability {

    public interface IPokerActivityCallback{
        void update(Player p);
    }

    private static final String TAG = FrameworkCapability.class.getSimpleName();
    private Player player;
    private PokerActivity activity;
    /*************************************************************************
     * 	SDP stuff                                                            *
     *************************************************************************/
    private static Context ctx;
    public void initCapability( Context applicationContext ) {
        FrameworkCapability.ctx = applicationContext;
    }
    /*************************************************************************/

    /**
     * First method to be called from orchestrator.js
     */
    public JSONObject initGame(JSONObject initData) {
        Intent i = new Intent(FrameworkCapability.ctx, PokerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        JSONObject res;
        try {
            int initialFunds = initData.getInt("initial_funds");
            player = new Player(initialFunds);
            ctx.startActivity(i);
            return player.getJSON();
        } catch (JSONException e){
            throw new PokerException("Error parsing initData JSON",e);
        }
    }

    public void setStatus(Integer state){
        player.setState(Player.State.values()[state]);
        PokerActivity.getInstance().update(player);
    }
}
