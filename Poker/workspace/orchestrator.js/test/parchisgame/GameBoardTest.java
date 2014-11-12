package parchisgame;

import org.junit.Test;
import org.junit.Assert;

import framework.parchisgame.GameBoard;
import framework.parchisgame.Pawn;

/**
 * Created by fare on 11/11/14.
 */
public class GameBoardTest {

    @Test
    public void testInsertPawnInEmptySquare(){
        GameBoard gb = new GameBoard();
        gb.insertPawn(new Pawn(0,5,-1));
        Assert.assertEquals(gb.spaceInSquare(0,5),true);
        Assert.assertEquals(gb.nextSpaceInSquare(0,5),1);
        Assert.assertEquals(gb.pawnsInSquare(0,5).size(),1);
    }

    @Test
    public void testInsertPawnInPartiallyFullSquare(){
        GameBoard gb = new GameBoard();
        gb.insertPawn(new Pawn(0,5,-1));
        gb.insertPawn(new Pawn(0,5,-1));
        Assert.assertEquals(gb.spaceInSquare(0,5),false);
        Assert.assertEquals(gb.nextSpaceInSquare(0,5),-1);
        Assert.assertEquals(gb.pawnsInSquare(0,5).size(),2);
    }

    @Test
    public void testInsertPawnInFullSquare(){
        GameBoard gb = new GameBoard();
        gb.insertPawn(new Pawn(0,5,-1));
        gb.insertPawn(new Pawn(0,5,-1));
        gb.insertPawn(new Pawn(0,5,-1));
        Assert.assertEquals(gb.spaceInSquare(0,5),false);
        Assert.assertEquals(gb.nextSpaceInSquare(0,5),-1);
        Assert.assertEquals(gb.pawnsInSquare(0,5).size(),2);
    }

    @Test
    public void testRemovePawnInEmptySquare(){
        GameBoard gb = new GameBoard();
        gb.removePawn(new Pawn(0,5,-1));
        Assert.assertEquals(gb.spaceInSquare(0,5),true);
        Assert.assertEquals(gb.nextSpaceInSquare(0,5),0);
        Assert.assertEquals(gb.pawnsInSquare(0,5),null);
    }

    @Test
    public void testRemovePawnInPartiallyFullSquare(){
        GameBoard gb = new GameBoard();
        gb.insertPawn(new Pawn(0,5,-1));
        gb.removePawn(new Pawn(0,5,-1));
        Assert.assertEquals(gb.spaceInSquare(0,5),true);
        Assert.assertEquals(gb.nextSpaceInSquare(0,5),0);
        Assert.assertEquals(gb.pawnsInSquare(0,5).size(),0);
    }

    @Test
    public void testRemovePawnInFullSquare(){
        GameBoard gb = new GameBoard();
        gb.insertPawn(new Pawn(0,5,-1));
        gb.insertPawn(new Pawn(0,5,-1));
        gb.removePawn(new Pawn(0,5,-1));
        Assert.assertEquals(gb.spaceInSquare(0,5),true);
        Assert.assertEquals(gb.nextSpaceInSquare(0,5),1);
        Assert.assertEquals(gb.pawnsInSquare(0,5).size(),1);
    }


}
