package com.ojs.capabilities.composerCapability.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.ojs.R;

/**
 * Created by fare on 25/10/14.
 */

public class ComposerSettingsActivity extends PreferenceActivity {

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
