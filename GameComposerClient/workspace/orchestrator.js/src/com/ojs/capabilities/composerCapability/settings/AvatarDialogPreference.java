package com.ojs.capabilities.composerCapability.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.ojs.R;

/**
 * Created by fare on 26/10/14.
 */
public class AvatarDialogPreference extends DialogPreference {

    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        String value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeString(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private static Context ctx;
    private static String avatarName;
    private static ImageView currentAvatar;

    private static final String DEFAULT_VALUE = "avatar20";

    public AvatarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        setDialogLayoutResource(R.layout.framework_avatar_layout);
    }

    private static void updateSelectedAvatar() {
        int resourceId = ctx.getResources().getIdentifier(avatarName, "drawable", ctx.getPackageName());
        currentAvatar.setImageDrawable(ctx.getResources().getDrawable(resourceId));
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        ViewGroup rootLayout = (ViewGroup) view;
        currentAvatar = (ImageView) rootLayout.findViewById(R.id.currentAvatar);
        GridView gridView = (GridView) rootLayout.findViewById(R.id.avatarGrid);
        gridView.setAdapter(new AvatarAdapter(ctx));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                avatarName = "avatar" + (position + 1);
                updateSelectedAvatar();
            }
        });

        updateSelectedAvatar();

    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            avatarName = this.getPersistedString(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            avatarName = (String) defaultValue;
            persistString(avatarName);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistString(avatarName);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.value = avatarName;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        updateSelectedAvatar();
    }
}