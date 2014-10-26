package com.ojs.capabilities.frameworkCapability.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.ojs.R;

/**
 * Created by fare on 25/10/14.
 */

public class FrameworkSettingsActivity extends PreferenceActivity {

    public static class FrameworkSettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.framework_settings);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new FrameworkSettingsFragment()).commit();
    }
}
