package com.ojs.capabilities.guessingCapability;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ojs.OrchestratorJsActivity;
import com.ojs.capabilities.dialogCapability.DialogCapabilityActivity;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by fare on 05/08/14.
 */
public class GuessingCapability {
    
    private static Context ctx; // Current context
    static String value; // Guessed value

    public void initCapability(Context c) {
        GuessingCapability.ctx = c.getApplicationContext(); // Context.getApplicationContext prevents storing a wrong Context
    }

    public void startGuessing() throws Exception {
        Intent i = new Intent(GuessingCapability.ctx, GuessingCapabilityActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GuessingCapability.value = "";
        Log.d(OrchestratorJsActivity.TAG,"Antes de crear la actividad");
        GuessingCapability.ctx.startActivity(i);
        Log.d(OrchestratorJsActivity.TAG,"Actividad creada");
    }

    public String getGuess() {
        return GuessingCapability.value.toString();
    }

    public void showResults(boolean winner) {
        Log.d(OrchestratorJsActivity.TAG, winner?"WINNER":"LOSER");
    }
}
