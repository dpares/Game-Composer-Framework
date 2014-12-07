package com.ojs.capabilities.composerCapability.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.ojs.R;

/**
 * Created by fare on 26/10/14.
 */
public class AvatarAdapter extends ArrayAdapter {
    private Context ctx;

    private Integer[] avatarIds = {
            R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
            R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8,
            R.drawable.avatar9, R.drawable.avatar10, R.drawable.avatar11, R.drawable.avatar12,
            R.drawable.avatar13, R.drawable.avatar14, R.drawable.avatar15, R.drawable.avatar16,
            R.drawable.avatar17, R.drawable.avatar18, R.drawable.avatar19, R.drawable.avatar20
    };

    public AvatarAdapter(Context c) {
        super(c,0);
        ctx = c;
    }

    public int getCount() {
        return avatarIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(ctx);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(avatarIds[position]);
        return imageView;
    }
}