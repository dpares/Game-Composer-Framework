package com.ojs.capabilities.composerCapability;

import android.content.Context;
import android.content.Intent;

import com.ojs.OrchestratorJsActivity;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import composer.engine.ComposerGameActivity;
import composer.engine.ComposerGameException;

/**
 * Created by fare on 29/09/14.
 */
public class ComposerCapability {

    private static final String TAG = ComposerCapability.class.getSimpleName();
    /**
     * **********************************************************************
     * SDP stuff                                                            *
     * ***********************************************************************
     */
    private static Context ctx;

    public void initCapability(Context applicationContext) {
        ComposerCapability.ctx = applicationContext;
    }

    public static Context getContext() {
        return ComposerCapability.ctx;
    }

    /**
     * *********************************************************************
     */
    private static boolean playerDataAvailable;
    private static boolean wantsRematch;
    private static boolean gameLeft;
    private static boolean sendAllPlayersData;
    private static boolean additionalStep;

    private static JSONObject nullJSON() {
        try {
            return new JSONObject().put("null", true);
        } catch (JSONException e) {
            throw new ComposerGameException("Error parsing null JSON", e);
        }
    }

    /**
     * First method to be called from orchestrator.js
     */
    public void initGame(JSONObject initData) {
        playerDataAvailable = false;
        wantsRematch = false;
        gameLeft = false;

        if (ComposerGameActivity.getInstance() == null) {
            try {
                String className = initData.getString("activity_class");
                Intent i = new Intent(ComposerCapability.ctx, Class.forName(className));
                i.putExtra("init_data", initData.toString());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            } catch (JSONException e) {
                throw new ComposerGameException("Error parsing initData JSON", e);
            } catch (ClassNotFoundException e) {
                throw new ComposerGameException("activityClass not found", e);
            }
        } else
            ComposerGameActivity.getInstance().newGame(initData);
    }

    public void setPlayerState(JSONObject state) {
        ComposerGameActivity.getInstance().setPlayerState(state);
    }

    public JSONObject getPlayerInitialState() {
        if (ComposerGameActivity.getInstance() == null)
            return ComposerCapability.nullJSON();
        else
            try {
                JSONObject res = new JSONObject();
                res.put("state", ComposerGameActivity.getInstance().getPlayer().getJSON());
                res.put("active", ComposerGameActivity.getInstance().getPlayer().isActive());
                return res;
            } catch (JSONException e) {
                throw new ComposerGameException("Error when creating initial state JSON", e);
            }
    }

    public void showCurrentState(JSONArray players, JSONObject commonData) {
        ComposerGameActivity.getInstance().update(players, commonData);
    }

    public void startStep(Integer phase, Integer step) {
        ComposerGameActivity.getInstance().startStep(phase, step);
    }

    public JSONObject getStepResult(Integer phase, Integer step) {
        if (!playerDataAvailable)
            return ComposerCapability.nullJSON();
        else {
            playerDataAvailable = false;
            JSONObject res = new JSONObject();
            try {
                res.put("common_data", ComposerGameActivity.getInstance().getCommonDataJSON());
                if(!sendAllPlayersData)
                    res.put("player_data", ComposerGameActivity.getInstance().getPlayer().getJSON());
                else
                    res.put("all_players_data", ComposerGameActivity.getInstance().getAllPlayersJSON());
                if(additionalStep)
                    res.put("additional_step",true);
                sendAllPlayersData = false;
                additionalStep = false;
                return res;
            } catch (JSONException e) {
                throw new ComposerGameException("Error sending turn result", e);
            }
        }
    }

    public JSONObject showResults(JSONObject winners, JSONArray players, JSONObject commonData) {
        ComposerGameActivity.getInstance().showResults(winners, players, commonData);
        return ComposerGameActivity.getInstance().getPlayer().getJSON();
    }

    public JSONObject newRound() {
        ComposerGameActivity.getInstance().newRound();
        return ComposerGameActivity.getInstance().getPlayer().getJSON();
    }

    public void announceWinner(JSONArray players, Integer winner) {
        ComposerGameActivity.getInstance().announceWinner(players, winner);
        playerDataAvailable = false;
        ComposerGameActivity.getInstance().askForRematch();
    }

    public static void rematchAnswer(boolean wantsRematch) {
        ComposerCapability.wantsRematch = wantsRematch;
        ComposerCapability.playerDataAvailable = true;
    }

    public JSONObject askForRematch() {
        if (!playerDataAvailable)
            return ComposerCapability.nullJSON();
        else
            try {
                return new JSONObject().put("value", ComposerCapability.wantsRematch);
            } catch (JSONException e) {
                throw new ComposerGameException("Error when creating JSON on askForRematch", e);
            }
    }

    public void exitGame(String reason) {
        ComposerCapability.leaveGame();
        ComposerGameActivity.getInstance().closeActivity(reason);
    }

    public static void endOfTurn(){
        ComposerCapability.endOfTurn(false, false);
    }

    public static void endOfTurn(boolean sendAllPlayersData, boolean additionalStep) {
        playerDataAvailable = true;
        ComposerCapability.sendAllPlayersData = sendAllPlayersData;
        ComposerCapability.additionalStep = additionalStep;
    }

    public static void leaveGame() {
        if (!gameLeft) {
            OrchestratorJsActivity.singleton.sendEvent("disconnect");
            gameLeft = true;
        }
    }
}
