package pokergame;

import org.junit.Test;
import org.junit.Assert;

import pfc.pokergame.Player;

/**
 * Created by fare on 21/09/14.
 */
public class PlayerTest {

    @Test
    public void testNewBet() {
        Player p = new Player(1000);
        p.newBet(300);
        Assert.assertEquals(p.getState(), Player.State.DEFAULT);
        Assert.assertEquals(p.getFunds(), 700);
        Assert.assertEquals(p.getBet(), 300);
        p.newBet(500);
        Assert.assertEquals(p.getState(), Player.State.DEFAULT);
        Assert.assertEquals(p.getFunds(), 200);
        Assert.assertEquals(p.getBet(), 800);
        p.newBet(300);
        Assert.assertEquals(p.getState(), Player.State.ALL_IN);
        Assert.assertEquals(p.getFunds(), 0);
        Assert.assertEquals(p.getBet(), 1000);
    }

    /*** Testing for the chooseAction must be added once the final version is developed ***/
}
