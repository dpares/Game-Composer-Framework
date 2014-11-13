package com.ojs.capabilities.frameworkCapability;

import android.content.Context;
import android.content.Intent;

import com.ojs.OrchestratorJsActivity;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import framework.engine.FrameworkGameException;
import framework.engine.FrameworkGameActivity;

/**
 * Created by fare on 29/09/14.
 */
public class FrameworkCapability {

    private static final String TAG = FrameworkCapability.class.getSimpleName();
    /**
     * **********************************************************************
     * SDP stuff                                                            *
     * ***********************************************************************
     */
    private static Context ctx;

    public void initCapability(Context applicationContext) {
        FrameworkCapability.ctx = applicationContext;
    }

    public static Context getContext() {
        return FrameworkCapability.ctx;
    }

    /**
     * *********************************************************************
     */
    private static boolean playerDataAvailable;
    private static boolean wantsRematch;
    private static boolean gameLeft;
    private static boolean sendAllPlayersData;

    private static JSONObject nullJSON() {
        try {
            return new JSONObject().put("null", true);
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing null JSON", e);
        }
    }

    /**
     * First method to be called from orchestrator.js
     */
    public void initGame(JSONObject initData) {
        playerDataAvailable = false;
        wantsRematch = false;
        gameLeft = false;

        if (FrameworkGameActivity.getInstance() == null) {
            try {
                String className = initData.getString("activity_class");
                Intent i = new Intent(FrameworkCapability.ctx, Class.forName(className));
                i.putExtra("init_data", initData.toString());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            } catch (JSONException e) {
                throw new FrameworkGameException("Error parsing initData JSON", e);
            } catch (ClassNotFoundException e) {
                throw new FrameworkGameException("activityClass not found", e);
            }
        } else
            FrameworkGameActivity.getInstance().newGame(initData);
    }

    public void setPlayerState(JSONObject state) {
        FrameworkGameActivity.getInstance().setPlayerState(state);
    }

    public JSONObject getPlayerInitialState() {
        if (FrameworkGameActivity.getInstance() == null)
            return FrameworkCapability.nullJSON();
        else
            try {
                JSONObject res = new JSONObject();
                res.put("state", FrameworkGameActivity.getInstance().getPlayer().getJSON());
                res.put("active", FrameworkGameActivity.getInstance().getPlayer().isActive());
                return res;
            } catch (JSONException e) {
                throw new FrameworkGameException("Error when creating initial state JSON", e);
            }
    }

    public void showCurrentState(JSONArray players, JSONObject commonData) {
        FrameworkGameActivity.getInstance().update(players, commonData);
    }

    public void startStep(Integer phase, Integer step) {
        FrameworkGameActivity.getInstance().startStep(phase, step);
    }

    public JSONObject getStepResult(Integer phase, Integer step) {
        if (!playerDataAvailable)
            return FrameworkCapability.nullJSON();
        else {
            playerDataAvailable = false;
            JSONObject res = new JSONObject();
            try {
                res.put("common_data", FrameworkGameActivity.getInstance().getCommonDataJSON());
                if(!sendAllPlayersData)
                    res.put("player_data", FrameworkGameActivity.getInstance().getPlayer().getJSON());
                else
                    res.put("all_players_data", FrameworkGameActivity.getInstance().getAllPlayersJSON());
                return res;
            } catch (JSONException e) {
                throw new FrameworkGameException("Error sending turn result", e);
            }
        }
    }

    public JSONObject showResults(JSONObject winners, JSONArray players, JSONObject commonData) {
        FrameworkGameActivity.getInstance().showResults(winners, players, commonData);
        return FrameworkGameActivity.getInstance().getPlayer().getJSON();
    }

    public JSONObject newRound() {
        FrameworkGameActivity.getInstance().newRound();
        return FrameworkGameActivity.getInstance().getPlayer().getJSON();
    }

    public void announceWinner(JSONArray players, Integer winner) {
        FrameworkGameActivity.getInstance().announceWinner(players, winner);
        playerDataAvailable = false;
        FrameworkGameActivity.getInstance().askForRematch();
    }

    public static void rematchAnswer(boolean wantsRematch) {
        FrameworkCapability.wantsRematch = wantsRematch;
        FrameworkCapability.playerDataAvailable = true;
    }

    public JSONObject askForRematch() {
        if (!playerDataAvailable)
            return FrameworkCapability.nullJSON();
        else
            try {
                return new JSONObject().put("value", FrameworkCapability.wantsRematch);
            } catch (JSONException e) {
                throw new FrameworkGameException("Error when creating JSON on askForRematch", e);
            }
    }

    public void exitGame(String reason) {
        FrameworkCapability.leaveGame();
        FrameworkGameActivity.getInstance().closeActivity(reason);
    }

    public static void endOfTurn(){
        FrameworkCapability.endOfTurn(false);
    }

    public static void endOfTurn(boolean sendAllPlayersData) {
        playerDataAvailable = true;
        FrameworkCapability.sendAllPlayersData = sendAllPlayersData;
    }

    public static void leaveGame() {
        if (!gameLeft) {
            OrchestratorJsActivity.singleton.sendEvent("disconnect");
            gameLeft = true;
        }
    }
}
