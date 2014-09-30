package pfc.engine;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ojs.R;

import pfc.pokergame.Player;

/**
 * Created by fare on 29/09/14.
 */
public class PokerActivity extends Activity {

    public static PokerActivity instance;

    private TextView statusLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creating a full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.poker_layout);
        statusLabel = (TextView)findViewById(R.id.dealerLabel);
        instance = this;
    }

    public static PokerActivity getInstance(){
        return PokerActivity.instance;
    }

    public void update(Player p){
        statusLabel.setText(p.getState().toString());
    }
}
