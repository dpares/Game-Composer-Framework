package pokergame;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fare on 21/09/14.
 */
public class BestHandTest {

    private Player p = new Player(1000);

    @Test
    public void testHighCard(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(2,12));
        communityCards.add(new Card(0,7));
        communityCards.add(new Card(1,2));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.HIGH_CARD);
    }

    @Test
    public void testOnePair(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(2,3));
        communityCards.add(new Card(0,7));
        communityCards.add(new Card(1,2));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.ONE_PAIR);
    }

    @Test
    public void testTwoPair(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(2,3));
        communityCards.add(new Card(0,7));
        communityCards.add(new Card(1,7));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.TWO_PAIR);
    }

    @Test
    public void testThreeOfAKind(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(2,3));
        communityCards.add(new Card(0,3));
        communityCards.add(new Card(1,2));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.THREE_OF_A_KIND);
    }

    @Test
    public void testStraight(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(2,6));
        communityCards.add(new Card(0,7));
        communityCards.add(new Card(1,4));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.STRAIGHT);
    }

    @Test
    public void testFlush(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(1,12));
        communityCards.add(new Card(1,7));
        communityCards.add(new Card(1,4));
        communityCards.add(new Card(1,9));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.FLUSH);
    }

    @Test
    public void testFullHouse(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(2,3));
        communityCards.add(new Card(0,3));
        communityCards.add(new Card(1,2));
        communityCards.add(new Card(3,2));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.FULL_HOUSE);
    }

    @Test
    public void testFourOfAKind(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(2,3));
        communityCards.add(new Card(0,3));
        communityCards.add(new Card(3,3));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(0,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.FOUR_OF_A_KIND);
    }

    @Test
    public void testStraightFlush(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,3));
        holeCards.add(new Card(1,6));
        communityCards.add(new Card(1,7));
        communityCards.add(new Card(1,4));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(1,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.STRAIGHT_FLUSH);
    }

    @Test
    public void testStraightFlushWithAceLow(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,0));
        holeCards.add(new Card(1,1));
        communityCards.add(new Card(1,2));
        communityCards.add(new Card(1,3));
        communityCards.add(new Card(1,4));
        communityCards.add(new Card(2,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.STRAIGHT_FLUSH);
    }

    @Test
    public void testStraightFlushWithAceHigh(){
        List<Card> holeCards = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards.add(new Card(1,9));
        holeCards.add(new Card(1,10));
        communityCards.add(new Card(1,11));
        communityCards.add(new Card(1,12));
        communityCards.add(new Card(1,0));
        communityCards.add(new Card(2,5));
        communityCards.add(new Card(0,11));
        BestHand bh = new BestHand(holeCards,communityCards,p);
        Assert.assertEquals(bh.getType(),BestHand.Type.STRAIGHT_FLUSH);
    }

    @Test
    public void testCompareAceHighToKingHigh(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,0));
        holeCards1.add(new Card(2,5));
        holeCards2.add(new Card(3,3));
        holeCards2.add(new Card(3,4));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(2,7));
        communityCards.add(new Card(1,1));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(0,2));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),1);
    }

    @Test
    public void testCompareAceHighToPair(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,0));
        holeCards1.add(new Card(2,5));
        holeCards2.add(new Card(3,1));
        holeCards2.add(new Card(3,4));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(2,7));
        communityCards.add(new Card(1,1));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(0,2));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
     public void testComparePairs(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,0));
        holeCards1.add(new Card(2,0));
        holeCards2.add(new Card(3,1));
        holeCards2.add(new Card(3,4));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(2,7));
        communityCards.add(new Card(1,1));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(0,2));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),1);
    }

    @Test
    public void testComparePairToTwoPair(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,0));
        holeCards1.add(new Card(2,0));
        holeCards2.add(new Card(3,1));
        holeCards2.add(new Card(3,12));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(2,7));
        communityCards.add(new Card(1,1));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(0,2));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void testCompareTwoPairs(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,0));
        holeCards1.add(new Card(2,11));
        holeCards2.add(new Card(3,1));
        holeCards2.add(new Card(3,12));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(2,0));
        communityCards.add(new Card(1,1));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(0,2));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),1);
    }

    @Test
    public void testCompareTwoPairToThreeOfAKind(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,0));
        holeCards1.add(new Card(2,11));
        holeCards2.add(new Card(3,1));
        holeCards2.add(new Card(3,12));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(2,7));
        communityCards.add(new Card(1,0));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(0,12));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void testCompareThreesOfAKind(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,11));
        holeCards1.add(new Card(2,11));
        holeCards2.add(new Card(1,12));
        holeCards2.add(new Card(3,12));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(2,7));
        communityCards.add(new Card(1,0));
        communityCards.add(new Card(2,12));
        communityCards.add(new Card(0,6));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void testCompareStraightToThreeOfAKind(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,1));
        holeCards1.add(new Card(2,2));
        holeCards2.add(new Card(3,1));
        holeCards2.add(new Card(3,12));
        communityCards.add(new Card(0,3));
        communityCards.add(new Card(2,4));
        communityCards.add(new Card(1,0));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(0,12));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),1);
    }

    @Test
     public void testCompareStraights(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,1));
        holeCards1.add(new Card(2,2));
        holeCards2.add(new Card(3,9));
        holeCards2.add(new Card(3,8));
        communityCards.add(new Card(0,3));
        communityCards.add(new Card(2,4));
        communityCards.add(new Card(1,5));
        communityCards.add(new Card(3,6));
        communityCards.add(new Card(0,7));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void testCompareStraightToFlush(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,1));
        holeCards1.add(new Card(2,2));
        holeCards2.add(new Card(3,0));
        holeCards2.add(new Card(3,11));
        communityCards.add(new Card(0,3));
        communityCards.add(new Card(2,4));
        communityCards.add(new Card(3,5));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(3,2));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void CompareFlushes(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(3,0));
        holeCards1.add(new Card(1,1));
        holeCards2.add(new Card(3,4));
        holeCards2.add(new Card(0,11));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(3,2));
        communityCards.add(new Card(2,5));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(3,7));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),1);
    }

    @Test
    public void CompareFlushToFullHouse(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(3,0));
        holeCards1.add(new Card(3,1));
        holeCards2.add(new Card(3,4));
        holeCards2.add(new Card(2,4));
        communityCards.add(new Card(3,9));
        communityCards.add(new Card(2,9));
        communityCards.add(new Card(3,5));
        communityCards.add(new Card(3,12));
        communityCards.add(new Card(1,4));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void CompareFullHouses(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(3,11));
        holeCards1.add(new Card(2,11));
        holeCards2.add(new Card(3,4));
        holeCards2.add(new Card(2,4));
        communityCards.add(new Card(0,9));
        communityCards.add(new Card(1,9));
        communityCards.add(new Card(2,9));
        communityCards.add(new Card(0,12));
        communityCards.add(new Card(3,5));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),1);
    }

    @Test
    public void CompareFullHouseToFourOfAKind(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(3,11));
        holeCards1.add(new Card(2,11));
        holeCards2.add(new Card(3,9));
        holeCards2.add(new Card(2,4));
        communityCards.add(new Card(0,9));
        communityCards.add(new Card(1,9));
        communityCards.add(new Card(2,9));
        communityCards.add(new Card(0,12));
        communityCards.add(new Card(3,5));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void CompareFoursOfAKind(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(3,11));
        holeCards1.add(new Card(2,11));
        holeCards2.add(new Card(3,9));
        holeCards2.add(new Card(2,9));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(1,11));
        communityCards.add(new Card(1,9));
        communityCards.add(new Card(0,9));
        communityCards.add(new Card(3,5));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),1);
    }

    @Test
    public void CompareFourOfAKindToStraightFlush(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(3,11));
        holeCards1.add(new Card(2,11));
        holeCards2.add(new Card(3,4));
        holeCards2.add(new Card(3,7));
        communityCards.add(new Card(0,11));
        communityCards.add(new Card(1,11));
        communityCards.add(new Card(3,8));
        communityCards.add(new Card(3,6));
        communityCards.add(new Card(3,5));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }

    @Test
    public void CompareStraightFlushes(){
        List<Card> holeCards1 = new ArrayList<Card>();
        List<Card> holeCards2 = new ArrayList<Card>();
        List<Card> communityCards = new ArrayList<Card>();
        holeCards1.add(new Card(1,2));
        holeCards1.add(new Card(1,3));
        holeCards2.add(new Card(1,9));
        holeCards2.add(new Card(1,10));
        communityCards.add(new Card(1,4));
        communityCards.add(new Card(1,5));
        communityCards.add(new Card(1,6));
        communityCards.add(new Card(1,7));
        communityCards.add(new Card(1,8));
        BestHand bh1 = new BestHand(holeCards1,communityCards,p);
        BestHand bh2 = new BestHand(holeCards2,communityCards,p);
        Assert.assertEquals(bh1.compareTo(bh2),-1);
    }
}
