package pokergame;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.Assert;

import framework.engine.FrameworkGameException;
import framework.pokergame.PokerPlayer;

/**
 * Created by fare on 21/09/14.
 */
public class PokerPlayerTest {

    @Test
    public void testNewBet() {
        try {
            JSONObject initData = new JSONObject().put("initial_funds", 1000);
            PokerPlayer p = new PokerPlayer(initData, "0", "", false);
            p.newBet(300);
            Assert.assertEquals(p.getState(), PokerPlayer.PokerState.DEFAULT);
            Assert.assertEquals(p.getFunds(), 700);
            Assert.assertEquals(p.getBet(), 300);
            p.newBet(500);
            Assert.assertEquals(p.getState(), PokerPlayer.PokerState.DEFAULT);
            Assert.assertEquals(p.getFunds(), 200);
            Assert.assertEquals(p.getBet(), 800);
            p.newBet(300);
            Assert.assertEquals(p.getState(), PokerPlayer.PokerState.ALL_IN);
            Assert.assertEquals(p.getFunds(), 0);
            Assert.assertEquals(p.getBet(), 1000);
        } catch (JSONException e){
            throw new FrameworkGameException("Error creating initData JSON",e);
        }
    }

    /*** Testing for the chooseAction must be added once the final version is developed ***/
}
