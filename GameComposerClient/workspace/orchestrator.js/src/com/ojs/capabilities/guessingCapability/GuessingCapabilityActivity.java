package com.ojs.capabilities.guessingCapability;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ojs.OrchestratorJsActivity;
import com.ojs.R;

/**
 * Created by fare on 05/08/14.
 */
public class GuessingCapabilityActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.guessing_capability_layout);
        Button b = (Button)findViewById(R.id.guessingCapabilityButton);
        NumberPicker np = (NumberPicker)findViewById(R.id.guessingCapabilityNumberPicker);
        np.setMaxValue(9);
        np.setMinValue(0);
        b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        NumberPicker np = (NumberPicker)findViewById(R.id.guessingCapabilityNumberPicker);
                        Integer aux = np.getValue();
                        GuessingCapability.value = aux.toString();
                        onBackPressed();
                    }catch(Exception e){ e.printStackTrace(); }
                }
        });
    }
}
