package framework.engine;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.ojs.capabilities.frameworkCapability.FrameworkCapability;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fare on 07/11/14.
 */
public abstract class FrameworkPlayer {

    protected String name;
    protected String avatar;
    protected boolean active;
    protected State currentState;

    public FrameworkPlayer(JSONObject initData, String name, String avatar, boolean spectate){
        this.active = !spectate;
        this.name = name;
        this.avatar = avatar;
        this.newRound();
    }

    public FrameworkPlayer(JSONObject p){
        try{
            this.name = p.getString("name");
            this.avatar = p.getString("avatar");
        } catch (JSONException e) {
            throw new FrameworkGameException("Error parsing JSON into Player", e);
        }
    }

    public interface State{};

    public abstract JSONObject getJSON();

    public abstract void newRound();

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public State getState() {
        return this.currentState;
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public String getName() {
        return this.name;
    }

    public Drawable getAvatarDrawable() {
        Context ctx = FrameworkCapability.getContext();
        int resourceId = ctx.getResources().
                getIdentifier(this.avatar, "drawable", ctx.getPackageName());
        return ctx.getResources().getDrawable(resourceId);
    }

    @Override
    public boolean equals(Object o){
        return this.name.equals(((FrameworkPlayer)o).name) &&
                this.avatar.equals(((FrameworkPlayer)o).avatar);
    }

}
