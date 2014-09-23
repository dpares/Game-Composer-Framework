package pokergame;

import org.junit.Test;
import org.junit.Assert;

/**
 * Created by fare on 18/09/14.
 */
public class CardTest {

    @Test
    public void testCompareAceToKing() {
        Card c1 = new Card(0, 0);
        Card c2 = new Card(2, 12);
        Assert.assertEquals(c1.compareTo(c2), 1);
    }

    @Test
    public void testCompareAceToThree() {
        Card c1 = new Card(0, 0);
        Card c2 = new Card(2, 2);
        Assert.assertEquals(c1.compareTo(c2), 1);
    }

    @Test
    public void testCompareSixToEight() {
        Card c1 = new Card(0, 5);
        Card c2 = new Card(0, 7);
        Assert.assertEquals(c1.compareTo(c2), -1);
    }

    @Test
    public void testCompareDifferentSuits() {
        Card c1 = new Card(1, 5);
        Card c2 = new Card(2, 5);
        Assert.assertEquals(c1.compareTo(c2), -1);
    }

    @Test
    public void testCompareToEqual() {
        Card c1 = new Card(3, 9);
        Card c2 = new Card(3, 9);
        Assert.assertEquals(c1.compareTo(c2), 0);
    }

    @Test
    public void testGetAceOfSpadesState() {
        Card c = new Card(2, 0);
        Assert.assertEquals(c.getSuit(), Card.Suit.SPADE.ordinal());
        Assert.assertEquals(c.getRank(), Card.HIGHEST_VALUE);
    }

    @Test
    public void testGetNineOfHeartsState() {
        Card c = new Card(0, 8);
        Assert.assertEquals(c.getSuit(), Card.Suit.HEART.ordinal());
        Assert.assertEquals(c.getRank(), 9);
    }

}
