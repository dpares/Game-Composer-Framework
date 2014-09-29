package com.ojs.capabilities.frameworkCapability;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import bmge.framework.Game;
import bmge.framework.Screen;
import pfc.engine.PokerActivity;

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

    /**
     * First method to be called from orchestrator.js
     */
    static Game game;
    public void initGame() {
        Log.d(TAG, "initGame");
        if(game != null) {
            Log.d(TAG, "game was NOT null -> finish()");
            ((Activity) game).finish();
            Log.d(TAG, "finish() called!");
        }
        game = null;
        Intent i = new Intent(FrameworkCapability.ctx, PokerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);

    }
}
