package composer.engine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ojs.capabilities.composerCapability.ComposerCapability;
import com.ojs.helpers.SettingHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by fare on 07/11/14.
 */
public abstract class ComposerGameActivity extends Activity {
    protected static ComposerGameActivity instance;

    protected ComposerPlayer player;
    protected List<ComposerPlayer> playersList;
    private String playerClassName;
    private boolean pausedActivity = false;

    public static ComposerGameActivity getInstance() {
        return ComposerGameActivity.instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creating a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Context ctx = ComposerCapability.getContext();

        try {
            JSONObject initData = new JSONObject(this.getIntent().getStringExtra("init_data"));
            this.newGame(initData);
        } catch (JSONException e){
            throw new ComposerGameException("Error parsing initData on onCreate", e);
        }
    }

    public ComposerPlayer getPlayer() {
        return this.player;
    }

    public JSONArray getAllPlayersJSON(){
        try {
            JSONArray res = new JSONArray();
            for (ComposerPlayer fp : this.playersList)
                res.put(fp.getJSON());

            return res;
        } catch (NullPointerException e){
            throw new ComposerGameException("Players list is null",e);
        }
    }

    public void setPlayerState(JSONObject state) {
        try {
            Class playerClass = Class.forName(this.playerClassName);
            Constructor playerConstructor = playerClass.getConstructor(JSONObject.class);
            this.player = (ComposerPlayer) playerConstructor.newInstance(state);
        } catch (ClassNotFoundException e){
            throw new ComposerGameException("Player class not found",e);
        } catch (NoSuchMethodException e){
            throw new ComposerGameException("Player constructor not found",e);
        } catch (Exception e){
            throw new ComposerGameException("Error instantiating new FrameworkPlayer",e);
        }
    }

    public abstract JSONObject getCommonDataJSON();

    public abstract void update(JSONArray players, JSONObject commonData);

    public abstract void startStep(int phase, int step);

    public abstract void showResults(JSONObject winners, JSONArray players, JSONObject commonData);

    public abstract void newRound();

    public abstract void announceWinner(JSONArray players, Integer winner);

    public void askForRematch() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Rematch?").setMessage("Do you want a rematch?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ComposerCapability.rematchAnswer(true);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ComposerCapability.rematchAnswer(false);
            }
        }).show();
    }

    public void newGame(JSONObject initData) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String name = prefs.getString("pref_player_name", "Player");
            String avatar = prefs.getString("pref_player_avatar", "avatar20");
            boolean spectate = prefs.getBoolean("pref_player_spectate", false);
            this.playerClassName = initData.getString("player_class");
            Class playerClass = Class.forName(this.playerClassName);
            Constructor playerConstructor = playerClass.getConstructor(JSONObject.class,
                    String.class,String.class,boolean.class);
            this.player = (ComposerPlayer) playerConstructor.newInstance(initData,name,avatar,spectate);
        } catch (JSONException e) {
            throw new ComposerGameException("Error parsing initData on newGame", e);
        } catch (ClassNotFoundException e){
            throw new ComposerGameException("Player class not found",e);
        } catch (NoSuchMethodException e){
            throw new ComposerGameException("Player constructor not found",e);
        } catch (Exception e){
            throw new ComposerGameException("Error instantiating new FrameworkPlayer",e);
        }
    }

    public void closeActivity(String reason) {
        Toast.makeText(this,reason,Toast.LENGTH_LONG).show();
        ComposerGameActivity.instance = null;
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Leaving game").setMessage("Are you sure you want to leave the current game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ComposerCapability.leaveGame();
                        ComposerGameActivity.getInstance().closeActivity("You have left the game");
                    }
                }).setNegativeButton("No", null).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ComposerCapability.leaveGame();
        this.pausedActivity = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pausedActivity) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Alert").setMessage("You have lost connection to the server")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ComposerGameActivity.getInstance().closeActivity("You have left the game");
                        }
                    }).show();
        }
    }
}