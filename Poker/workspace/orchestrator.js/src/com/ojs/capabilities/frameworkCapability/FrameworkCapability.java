package com.ojs.capabilities.frameworkCapability;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import pfc.engine.PokerException;
import pfc.engine.PokerActivity;
import pfc.pokergame.Player;
/**
 * Created by fare on 29/09/14.
 */
public class FrameworkCapability {

    private static final String TAG = FrameworkCapability.class.getSimpleName();
    /*************************************************************************
     * 	SDP stuff                                                            *
     *************************************************************************/
    private static Context ctx;
    public void initCapability( Context applicationContext ) {
        FrameworkCapability.ctx = applicationContext;
    }
    /*************************************************************************/

    private static boolean playerDataAvailable;

    private static JSONObject nullJSON(){
        try{
            return new JSONObject().put("null",true);
        }catch(JSONException e){
            throw new PokerException("Error parsing null JSON",e);
        }
    }

    /**
     * First method to be called from orchestrator.js
     */
    public void initGame(JSONObject initData) {
        playerDataAvailable = false;

        Intent i = new Intent(FrameworkCapability.ctx, PokerActivity.class);
        i.putExtra("init_data",initData.toString());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }

    public void setPlayerState(JSONObject state){
        PokerActivity.getInstance().setPlayerState(state);
    }

    public JSONObject getPlayerState(){
        if(PokerActivity.getInstance() == null)
            return FrameworkCapability.nullJSON();
        else
            return PokerActivity.getInstance().getPlayer().getJSON();
    }

    public void showCurrentState(JSONArray players, JSONObject commonData){
        PokerActivity.getInstance().update(players, commonData);
    }

    public void startStep(Integer phase, Integer step){
        PokerActivity.getInstance().startStep(phase,step);
    }

    public JSONObject getStepResult(Integer phase, Integer step){
        if(!playerDataAvailable)
            return FrameworkCapability.nullJSON();
        else{
            playerDataAvailable = false;
            JSONObject res = new JSONObject();
            try{
                res.put("common_data",PokerActivity.getInstance().getCommonDataJSON());
                res.put("player_data",PokerActivity.getInstance().getPlayer().getJSON());
                playerDataAvailable = false;
                return res;
            } catch(JSONException e){
                throw new PokerException("Error sending turn result",e);
            }
        }
    }

    public void showResults(int[] winners, JSONArray players, JSONObject commonData){
        PokerActivity.getInstance().showResults(winners,players,commonData);
    }

    public static void endOfTurn(){
        playerDataAvailable = true;
    }
}
