package framework.parchisgame;

/**
 * Created by fare on 07/11/14.
 */
/**
 ** ParchisView.java
 **
 ** Copyright (C) 1992, 1999, 2007, 2011 Mariano Alvarez Fernandez
 ** [e-mail: malfer at telefonica.net]
 **
 ** This file is part of the Parchis4A program.
 **
 ** Parchis4A is free software: you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation, either version 3 of the License, or
 ** (at your option) any later version.
 **
 ** Parchis4A is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 ** GNU General Public License for more details.
 **
 ** You should have received a copy of the GNU General Public License
 ** along with Parchis4A.  If not, see <http://www.gnu.org/licenses/>.
 **
 **/

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.ojs.R;

import java.util.ArrayList;
import java.util.List;

public class ParchisView extends View {

    private Bitmap boardBitmap = null;
    private Bitmap pawnBitmaps[] = new Bitmap[4];
    private Bitmap bigPawnBitmaps[] = new Bitmap[4];
    private Bitmap avatarBitmaps[] = new Bitmap[4];
    private Bitmap destinationBitmap;
    private Paint paint = new Paint();
    private int boardSize;
    private int pawnSize;
    private int offsetX;
    private int offsetY;
    private PosG homePositions[][] = new PosG[4][4];
    private PosG squarePositions[][] = new PosG[68][2];
    private PosG corridorPositions[][][] = new PosG[4][7][2];
    private PosG finishPositions[][] = new PosG[4][4];
    private PosG avatarPositions[] = new PosG[4];
    private double scale;

    private List<ParchisPlayer> players;
    private List<ParchisPlayer> updatedPlayers;
    private GameBoard gameBoard;
    private List<PawnMovement> movements;

    private boolean showingDestination = false;
    private int currentMovementShown;
    private Pawn destinationMark;
    private Pawn pawnToMove;
    private Pawn eatenPawn;
    private int pawnsInHome;
    private int pawnsInLast;
    private boolean blockMoves;

    private class PosG {
        public int x;
        public int y;

        public PosG(int newX, int newY) {
            x = (int) (((newX - 17) * scale) + 0.5 + offsetX);
            y = (int) (((newY - 22) * scale) + 0.5 + offsetY);
        }

        public PosG(int newX, int newY, boolean conv) {
            x = newX;
            y = newY;
        }

        public int[] getIntArray() {
            int res[] = new int[2];
            res[0] = x;
            res[1] = y;
            return res;
        }
    }

    public ParchisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParchisView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Bitmap loadBitmap(Drawable draw, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw.setBounds(0, 0, size, size);
        draw.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Resources r = this.getContext().getResources();
        boardSize = w;
        if (boardSize > h) boardSize = h;
        offsetX = ((w - boardSize) / 2);
        offsetY = ((h - boardSize) / 2);
        scale = (boardSize * 1.0) / 439;
        pawnSize = (int) (19 * scale);
        boardBitmap = loadBitmap(r.getDrawable(R.drawable.parchisboard), boardSize);
        pawnBitmaps[0] = loadBitmap(r.getDrawable(R.drawable.yellowplayer), pawnSize);
        pawnBitmaps[1] = loadBitmap(r.getDrawable(R.drawable.blueplayer), pawnSize);
        pawnBitmaps[2] = loadBitmap(r.getDrawable(R.drawable.redplayer), pawnSize);
        pawnBitmaps[3] = loadBitmap(r.getDrawable(R.drawable.greenplayer), pawnSize);
        bigPawnBitmaps[0] = loadBitmap(r.getDrawable(R.drawable.yellowplayer), pawnSize * 2);
        bigPawnBitmaps[1] = loadBitmap(r.getDrawable(R.drawable.blueplayer), pawnSize * 2);
        bigPawnBitmaps[2] = loadBitmap(r.getDrawable(R.drawable.redplayer), pawnSize * 2);
        bigPawnBitmaps[3] = loadBitmap(r.getDrawable(R.drawable.greenplayer), pawnSize * 2);
        destinationBitmap = loadBitmap(r.getDrawable(R.drawable.pawndestination), pawnSize * 2);
        loadAvatarBitmaps();
        initGBoard();
    }

    private void loadAvatarBitmaps() {
        if (this.players != null) {
            for (ParchisPlayer player : this.players) {
                int colour = player.getColour().ordinal();
                if (colour != ParchisPlayer.Colour.UNDEFINED.ordinal())
                    avatarBitmaps[colour] = loadBitmap(player.getAvatarDrawable(), pawnSize * 2);
            }
        }
    }

    private void initGBoard() {
        int i;
        int posx, posy;

        for (i = 0; i < 8; i++) {
            posx = 426 - (i * 20);
            squarePositions[i][0] = new PosG(posx, 198);
            squarePositions[i][1] = new PosG(posx, 174);
        }
        for (i = 8; i < 16; i++) {
            posy = 173 - ((i - 8) * 20);
            squarePositions[i][0] = new PosG(261, posy);
            squarePositions[i][1] = new PosG(285, posy);
        }
        squarePositions[16][0] = new PosG(215, 33);
        squarePositions[16][1] = new PosG(239, 33);
        for (i = 17; i < 25; i++) {
            posy = 33 + ((i - 17) * 20);
            squarePositions[i][0] = new PosG(193, posy);
            squarePositions[i][1] = new PosG(169, posy);
        }
        for (i = 25; i < 33; i++) {
            posx = 168 - ((i - 25) * 20);
            squarePositions[i][0] = new PosG(posx, 198);
            squarePositions[i][1] = new PosG(posx, 174);
        }
        squarePositions[33][0] = new PosG(28, 220);
        squarePositions[33][1] = new PosG(28, 244);
        for (i = 34; i < 42; i++) {
            posx = 28 + ((i - 34) * 20);
            squarePositions[i][0] = new PosG(posx, 266);
            squarePositions[i][1] = new PosG(posx, 290);
        }
        for (i = 42; i < 50; i++) {
            posy = 291 + ((i - 42) * 20);
            squarePositions[i][0] = new PosG(193, posy);
            squarePositions[i][1] = new PosG(169, posy);
        }
        squarePositions[50][0] = new PosG(215, 431);
        squarePositions[50][1] = new PosG(239, 431);
        for (i = 51; i < 59; i++) {
            posy = 431 - ((i - 51) * 20);
            squarePositions[i][0] = new PosG(261, posy);
            squarePositions[i][1] = new PosG(285, posy);
        }
        for (i = 59; i < 67; i++) {
            posx = 286 + ((i - 59) * 20);
            squarePositions[i][0] = new PosG(posx, 266);
            squarePositions[i][1] = new PosG(posx, 290);
        }
        squarePositions[67][0] = new PosG(426, 220);
        squarePositions[67][1] = new PosG(426, 244);

        homePositions[0][0] = new PosG(squarePositions[4][0].x, squarePositions[13][0].y, false);
        homePositions[0][1] = new PosG(squarePositions[2][0].x, squarePositions[13][0].y, false);
        homePositions[0][2] = new PosG(squarePositions[4][0].x, squarePositions[11][0].y, false);
        homePositions[0][3] = new PosG(squarePositions[2][0].x, squarePositions[11][0].y, false);
        homePositions[1][0] = new PosG(squarePositions[30][0].x, squarePositions[19][0].y, false);
        homePositions[1][1] = new PosG(squarePositions[28][0].x, squarePositions[19][0].y, false);
        homePositions[1][2] = new PosG(squarePositions[30][0].x, squarePositions[21][0].y, false);
        homePositions[1][3] = new PosG(squarePositions[28][0].x, squarePositions[21][0].y, false);
        homePositions[2][0] = new PosG(squarePositions[36][0].x, squarePositions[45][0].y, false);
        homePositions[2][1] = new PosG(squarePositions[38][0].x, squarePositions[45][0].y, false);
        homePositions[2][2] = new PosG(squarePositions[36][0].x, squarePositions[47][0].y, false);
        homePositions[2][3] = new PosG(squarePositions[38][0].x, squarePositions[47][0].y, false);
        homePositions[3][0] = new PosG(squarePositions[62][0].x, squarePositions[55][0].y, false);
        homePositions[3][1] = new PosG(squarePositions[64][0].x, squarePositions[55][0].y, false);
        homePositions[3][2] = new PosG(squarePositions[62][0].x, squarePositions[53][0].y, false);
        homePositions[3][3] = new PosG(squarePositions[64][0].x, squarePositions[53][0].y, false);

        for (i = 0; i < 7; i++) {
            posx = 406 - i * 20;
            corridorPositions[0][i][0] = new PosG(posx, 219);
            corridorPositions[0][i][1] = new PosG(posx, 243);
        }
        for (i = 0; i < 7; i++) {
            posy = 53 + i * 20;
            corridorPositions[1][i][0] = new PosG(215, posy);
            corridorPositions[1][i][1] = new PosG(239, posy);
        }
        for (i = 0; i < 7; i++) {
            posx = 48 + i * 20;
            corridorPositions[2][i][0] = new PosG(posx, 219);
            corridorPositions[2][i][1] = new PosG(posx, 243);
        }
        for (i = 0; i < 7; i++) {
            posy = 411 - i * 20;
            corridorPositions[3][i][0] = new PosG(215, posy);
            corridorPositions[3][i][1] = new PosG(239, posy);
        }

        int x1 = 188;
        int x2 = 208;
        int x3 = 228;
        int x4 = 248;
        int x5 = 268;
        int y1 = 193;
        int y2 = 213;
        int y3 = 233;
        int y4 = 253;
        int y5 = 273;
        finishPositions[0][0] = new PosG(x5 - 2, y2);
        finishPositions[0][1] = new PosG(x5 - 2, y3);
        finishPositions[0][2] = new PosG(x5 - 2, y4);
        finishPositions[0][3] = new PosG(x4 - 2, y3);
        finishPositions[1][0] = new PosG(x2, y1);
        finishPositions[1][1] = new PosG(x3, y1);
        finishPositions[1][2] = new PosG(x4, y1);
        finishPositions[1][3] = new PosG(x3, y2);
        finishPositions[2][0] = new PosG(x1, y2);
        finishPositions[2][1] = new PosG(x1, y3);
        finishPositions[2][2] = new PosG(x1, y4);
        finishPositions[2][3] = new PosG(x2, y3);
        finishPositions[3][0] = new PosG(x2, y5 - 2);
        finishPositions[3][1] = new PosG(x3, y5 - 2);
        finishPositions[3][2] = new PosG(x4, y5 - 2);
        finishPositions[3][3] = new PosG(x3, y4 - 2);

        avatarPositions[0] = new PosG(squarePositions[4][0].x + pawnSize / 2 + 2,
                squarePositions[13][0].y + pawnSize / 2 + 2, false);
        avatarPositions[1] = new PosG(squarePositions[30][0].x + pawnSize / 2 + 2,
                squarePositions[19][0].y + pawnSize / 2 + 2, false);
        avatarPositions[2] = new PosG(squarePositions[36][0].x + pawnSize / 2 + 2,
                squarePositions[45][0].y + pawnSize / 2 + 2, false);
        avatarPositions[3] = new PosG(squarePositions[62][0].x + pawnSize / 2 + 2,
                squarePositions[55][0].y + pawnSize / 2 + 2, false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (boardBitmap == null) {
            Resources r = this.getContext().getResources();
            boardBitmap = loadBitmap(r.getDrawable(R.drawable.parchisboard), boardSize);
        }
        canvas.drawBitmap(boardBitmap, offsetX, offsetY, paint);

        if (this.players != null) {
            drawAvatars(canvas);
            drawAllPawns(canvas);
        }
        if (showingDestination)
            drawPossibleMovement(canvas);

        if (this.movements != null && this.movements.size() > 0) {
            invalidate();
            if (allMovesFinished()) {
                this.movements.clear();
                this.players = this.updatedPlayers;
                this.updatedPlayers = new ArrayList<ParchisPlayer>();
                gameBoard.deleteConflictPawn();
            }
        }
    }

    private boolean allMovesFinished(){
        boolean res = true;
        int i=0;
        while(res && i < this.movements.size()){
            if(this.movements.get(i).isFinished())
                i++;
            else
                res = false;
        }

        return res;
    }

    private void drawAvatars(Canvas canvas) {
        for (ParchisPlayer player : this.players) {
            int colour = player.getColour().ordinal();
            if (colour != ParchisPlayer.Colour.UNDEFINED.ordinal())
                canvas.drawBitmap(avatarBitmaps[colour], avatarPositions[colour].x,
                        avatarPositions[colour].y, paint);
        }
    }

    private void drawAllPawns(Canvas canvas) {
        for (int i = 0; i < this.players.size(); i++) {
            ParchisPlayer player = this.players.get(i);
            pawnsInHome = 0;
            pawnsInLast = 0;
            int colour = player.getColour().ordinal();
            if (colour != ParchisPlayer.Colour.UNDEFINED.ordinal()) {
                if (this.updatedPlayers.size() > 0) { // Move pawns
                    for (int j = 0; j < player.getPawns().size(); j++)
                        movePawn(player, this.players.get(i).getPawns().get(j),
                                this.updatedPlayers.get(i).getPawns().get(j), canvas);
                } else {
                    List<Pawn> pawns = player.getPawns();
                    for (int j = 0; j < 4; j++)
                        drawPawn(pawns.get(j), canvas);
                }
            }
        }
        if (this.updatedPlayers.size() > 0 && this.movements.size() == 0) {
            this.players = this.updatedPlayers;
            this.updatedPlayers = new ArrayList<ParchisPlayer>();
        }
    }

    private ParchisPlayer findPlayerByColour(int colour){
        ParchisPlayer res = null;
        int i = 0;
        while(res == null && i <this.players.size()) {
            if (this.players.get(i).getColour().ordinal() == colour)
                res = this.players.get(i);
            else
                i++;
        }
        return res;
    }

    private void drawPossibleMovement(Canvas canvas) {
        ParchisPlayer player = findPlayerByColour(pawnToMove.getColour());
        Pawn movablePawn = pawnToMove;
        int colour = movablePawn.getColour();
        int pawnInSquare;
        int square;
        Bitmap pawnBitmap;

        for (int i = 0; i < 2; i++) {
            square = i == 0 ? movablePawn.getSquare() : destinationMark.getSquare();
            pawnBitmap = i == 0 ? bigPawnBitmaps[colour] : destinationBitmap;
            if (square == Pawn.INITIAL_SQUARE) {
                pawnInSquare = i == 0 ? player.getHomePawns() - 1 : player.getHomePawns();
                canvas.drawBitmap(pawnBitmap, homePositions[colour][pawnInSquare].x - pawnSize / 2,
                        homePositions[colour][pawnInSquare].y - pawnSize / 2, paint);
            } else if (square == Pawn.LAST_SQUARE) {
                pawnInSquare = player.getFinishedPawns();
                canvas.drawBitmap(pawnBitmap, finishPositions[colour][pawnInSquare].x - pawnSize / 2,
                        finishPositions[colour][pawnInSquare].y - pawnSize / 2, paint);
            } else {
                if (i == 0)
                    pawnInSquare = gameBoard.pawnsInSquare(colour, square).indexOf(movablePawn);
                else {
                    pawnInSquare = gameBoard.nextSpaceInSquare(colour, square);
                    if (pawnInSquare < 0)
                        pawnInSquare = gameBoard.pawnsInSquare(colour, square).get(0).getColour() ==
                                movablePawn.getColour() ? 1 : 0;
                }
                if (square >= Pawn.REGULAR_SQUARES) {
                    int corridorPos = square - Pawn.REGULAR_SQUARES;
                    canvas.drawBitmap(pawnBitmap, corridorPositions[colour][corridorPos][pawnInSquare].x - pawnSize / 2,
                            corridorPositions[colour][corridorPos][pawnInSquare].y - pawnSize / 2, paint);
                } else
                    canvas.drawBitmap(pawnBitmap, squarePositions[square][pawnInSquare].x - pawnSize / 2,
                            squarePositions[square][pawnInSquare].y - pawnSize / 2, paint);
            }
        }
    }

    private void drawPawn(Pawn pawn, Canvas canvas) {
        int colour = pawn.getColour();
        int square = pawn.getSquare();
        Bitmap pawnBitmap = pawnBitmaps[colour];
        if (square == Pawn.INITIAL_SQUARE) {
            canvas.drawBitmap(pawnBitmap, homePositions[colour][pawnsInHome].x,
                    homePositions[colour][pawnsInHome].y, paint);
            pawnsInHome++;
        } else if (square == Pawn.LAST_SQUARE) {
            canvas.drawBitmap(pawnBitmap, finishPositions[colour][pawnsInLast].x,
                    finishPositions[colour][pawnsInLast].y, paint);
            pawnsInLast++;
        } else {
            int pawnInSquare = gameBoard.pawnsInSquare(colour, square).indexOf(pawn);
            if(pawnInSquare < 0)
                pawnInSquare = 1;
            if (square >= Pawn.REGULAR_SQUARES) {
                int corridorPos = square - Pawn.REGULAR_SQUARES;
                canvas.drawBitmap(pawnBitmap, corridorPositions[colour][corridorPos][pawnInSquare].x,
                        corridorPositions[colour][corridorPos][pawnInSquare].y, paint);
            } else
                canvas.drawBitmap(pawnBitmap, squarePositions[square][pawnInSquare].x,
                        squarePositions[square][pawnInSquare].y, paint);
        }
    }

    private PawnMovement findMovement(Pawn pawn){
        PawnMovement res = null;
        int i = 0;
        while(res == null && i < this.movements.size()){
            if(this.movements.get(i).getInitialSquare() == pawn.getSquare())
                res = this.movements.get(i);
            else
                i++;
        }

        return res;
    }

    private void movePawn(ParchisPlayer player, Pawn previousPawn, Pawn finalPawn, Canvas canvas) {
        if (!previousPawn.equals(finalPawn)) {
            PawnMovement move = findMovement(previousPawn);
            if (move == null) {
                int initialCoords[];
                int finalCoords[];
                int square;
                if (previousPawn.getSquare() == Pawn.INITIAL_SQUARE) {
                    initialCoords = homePositions[previousPawn.getColour()]
                            [player.getHomePawns() - 1].getIntArray();
                    finalCoords = squarePositions[finalPawn.getSquare()]
                            [gameBoard.nextSpaceInSquare(finalPawn.getColour(),
                            finalPawn.getSquare())].getIntArray();
                    move = new PawnMovement(previousPawn.getSquare(), initialCoords,
                            finalPawn.getSquare(), finalCoords, pawnSize, previousPawn.getColour());
                } else if (finalPawn.getSquare() == Pawn.INITIAL_SQUARE) {
                    finalCoords = homePositions[finalPawn.getColour()]
                            [player.getHomePawns()].getIntArray();
                    square = previousPawn.getSquare();
                    if (square < Pawn.REGULAR_SQUARES)
                        initialCoords = squarePositions[previousPawn.getSquare()]
                                [gameBoard.pawnsInSquare(previousPawn.getColour(),
                                previousPawn.getSquare()).indexOf(previousPawn)].getIntArray();
                    else
                        initialCoords = corridorPositions[previousPawn.getColour()]
                                [previousPawn.getSquare() - Pawn.REGULAR_SQUARES]
                                [gameBoard.pawnsInSquare(previousPawn.getColour(),
                                previousPawn.getSquare()).indexOf(previousPawn)].getIntArray();
                    move = new PawnMovement(previousPawn.getSquare(), initialCoords,
                            finalPawn.getSquare(), finalCoords, pawnSize, previousPawn.getColour());
                } else {
                    int coords[] = {0, 0}; // In a regular movement, coordinates don't matter
                    move = new PawnMovement(previousPawn.getSquare(), coords,
                            finalPawn.getSquare(), coords, pawnSize, previousPawn.getColour());
                }
                this.movements.add(move);
                if(previousPawn.getSquare() != Pawn.INITIAL_SQUARE)
                    gameBoard.removePawn(previousPawn);
                if (finalPawn.getSquare() != Pawn.LAST_SQUARE &&
                        finalPawn.getSquare() != Pawn.INITIAL_SQUARE) {
                    if(!gameBoard.spaceInSquare(finalPawn.getColour(),finalPawn.getSquare())) {
                        List<Pawn> aux = gameBoard.pawnsInSquare(finalPawn.getColour(),
                                finalPawn.getSquare());
                        int removePos = aux.get(1).getColour() != finalPawn.getColour() ? 1 : 0;
                        gameBoard.removePawn(aux.get(removePos),true);
                    }
                    gameBoard.insertPawn(finalPawn);
                }
            }
            if(!move.isFinished()) {
                int coords[] = move.nextStep();
                if (move.isSpecial())
                    canvas.drawBitmap(bigPawnBitmaps[previousPawn.getColour()], coords[0] - pawnSize / 2,
                            coords[1] - pawnSize / 2, paint);
                else {
                    int spaceInSquare = gameBoard.nextSpaceInSquare(finalPawn.getColour(),
                            finalPawn.getSquare());
                    if (spaceInSquare < 0) // Eaten pawn
                        spaceInSquare = 1;
                    if (coords[0] < Pawn.REGULAR_SQUARES)
                        canvas.drawBitmap(bigPawnBitmaps[previousPawn.getColour()],
                                squarePositions[coords[0]][spaceInSquare].x - pawnSize / 2,
                                squarePositions[coords[0]][spaceInSquare].y - pawnSize / 2, paint);
                    else if (coords[0] < Pawn.LAST_SQUARE) {
                        int pos = coords[0] - Pawn.REGULAR_SQUARES;
                        canvas.drawBitmap(bigPawnBitmaps[previousPawn.getColour()],
                                corridorPositions[finalPawn.getColour()][pos][spaceInSquare].x - pawnSize / 2,
                                corridorPositions[finalPawn.getColour()][pos][spaceInSquare].y - pawnSize / 2,
                                paint);
                    } else {
                        spaceInSquare = player.getFinishedPawns();
                        canvas.drawBitmap(bigPawnBitmaps[previousPawn.getColour()],
                                finishPositions[finalPawn.getColour()][spaceInSquare].x - pawnSize / 2,
                                finishPositions[finalPawn.getColour()][spaceInSquare].y - pawnSize / 2,
                                paint);
                    }
                }
            }
        } else
            drawPawn(previousPawn, canvas);
    }

    public void updatePlayers(List<ParchisPlayer> players, boolean orderChanged) {
       if (orderChanged || this.players.size() < players.size())
            this.players = players;
        else if (this.players.size() > 0 &&
                this.players.get(0).getColour() == ParchisPlayer.Colour.UNDEFINED) {
            this.players = players;
            loadAvatarBitmaps();
            invalidate();
        } else {
            this.updatedPlayers = players;
            invalidate();
        }
    }

    public void newGame() {
        this.players = new ArrayList<ParchisPlayer>();
        this.updatedPlayers = new ArrayList<ParchisPlayer>();
        this.gameBoard = new GameBoard();
        this.showingDestination = false;
        this.currentMovementShown = -1;
        this.movements = new ArrayList<PawnMovement>();
        this.eatenPawn = null;
        this.blockMoves = false;
    }

    public Pair<Integer, Pawn> confirmMove() {
        Pawn movePawn = destinationMark;
        this.blockMoves = false;
        int moveCode = 0;
        if (destinationMark.getSquare() == Pawn.LAST_SQUARE)
            moveCode = 1;
        else if(eatenPawn != null)
            moveCode = 2;

        showingDestination = false;
        destinationMark = null;
        currentMovementShown = -1;
        pawnToMove = null;

        return new Pair<Integer, Pawn>(moveCode, movePawn);
    }

    public Pawn getEatenPawn(){
        return new Pawn(eatenPawn.getColour(), Pawn.INITIAL_SQUARE, eatenPawn.getNumber());
    }

    public boolean showNextMove(int playerIndex, int roll) {
        ParchisPlayer player = this.players.get(playerIndex);
        List<Pawn> pawns = player.getPawns();
        boolean possibleMovement = false;
        eatenPawn = null;

        if (!blockMoves && roll == 5 && player.getHomePawns() > 0 && gameBoard.spaceInSquare(
                player.getColour().ordinal(), Pawn.STARTING_POSITIONS[player.getColour().ordinal()])) {
            currentMovementShown = player.firstHomePawn().getNumber();
            possibleMovement = true;
            showingDestination = true;
            destinationMark = new Pawn(player.getColour().ordinal(),
                    Pawn.STARTING_POSITIONS[player.getColour().ordinal()], currentMovementShown);
            pawnToMove = player.firstHomePawn();
            blockMoves = true;
            invalidate();
        } else if (player.getHomePawns() < 4 && player.getFinishedPawns() < 4 && !blockMoves) {
            int i = 0;
            while (!possibleMovement && i < pawns.size()) {
                currentMovementShown++;
                if (currentMovementShown == pawns.size())
                    currentMovementShown = 0;
                Pawn pawn = pawns.get(currentMovementShown);
                if (pawn.getSquare() != Pawn.LAST_SQUARE) {
                    boolean skipPawn = false;
                    int pos = pawn.getSquare() + roll;
                    int colour = pawn.getColour();
                    if(pawn.getSquare() != Pawn.INITIAL_SQUARE) {
                        if (pawn.getSquare() <= Pawn.CORRIDOR_START[colour] &&
                                pos > Pawn.CORRIDOR_START[colour]) // Enter corridor
                            pos = pos - Pawn.CORRIDOR_START[colour] + Pawn.REGULAR_SQUARES - 1;
                        else if (pawn.getSquare() < Pawn.REGULAR_SQUARES && pos >= Pawn.REGULAR_SQUARES)
                            pos = pos - Pawn.REGULAR_SQUARES;
                        else if (pawn.getSquare() < Pawn.LAST_SQUARE && pos > Pawn.LAST_SQUARE) {
                            i++;
                            skipPawn = true;
                        } else if (pos == Pawn.LAST_SQUARE) {
                            if (!gameBoard.barrierBetween(pawn, pos)) {
                                skipPawn = true;
                                possibleMovement = true;
                                showingDestination = true;
                                destinationMark = new Pawn(pawn.getColour(), Pawn.LAST_SQUARE,
                                        currentMovementShown);
                                pawnToMove = pawn;
                                invalidate();
                            } else
                                i++;
                        }
                    } else
                        pos = Pawn.STARTING_POSITIONS[colour];
                    if (!skipPawn && !gameBoard.barrierBetween(pawn, pos)) {
                        if (pawn.getSquare() != Pawn.INITIAL_SQUARE &&
                                gameBoard.nextSpaceInSquare(colour, pos) == 0) { // Empty square
                            possibleMovement = true;
                            showingDestination = true;
                            destinationMark = new Pawn(pawn.getColour(), pos, currentMovementShown);
                            pawnToMove = pawn;
                            invalidate();
                        } else { // Check if a pawn could be eaten
                            boolean canEat = false;
                            int j = gameBoard.pawnsInSquare(colour, pos).size()-1;
                            while (!canEat && j >= 0) {
                                if (gameBoard.pawnsInSquare(colour, pos).get(j).getColour() !=
                                        pawns.get(currentMovementShown).getColour() &&
                                        !Pawn.isSafeSquare(pos)) {
                                    canEat = true;
                                    eatenPawn = gameBoard.pawnsInSquare(colour, pos).get(j);
                                } else
                                    j--;
                            }
                            if (canEat || gameBoard.spaceInSquare(colour, pos)) {
                                possibleMovement = true;
                                showingDestination = true;
                                destinationMark = new Pawn(pawn.getColour(), pos, currentMovementShown);
                                if(pawn.getSquare() == Pawn.INITIAL_SQUARE) {
                                    blockMoves = true;
                                    pawnToMove = player.firstHomePawn();
                                    currentMovementShown = pawnToMove.getNumber();
                                } else
                                    pawnToMove = pawn;
                                invalidate();
                            } else
                                i++;
                        }
                    } else
                        i++;
                } else
                    i++;
            }
        } else if(blockMoves)
            possibleMovement = true;

        return possibleMovement;
    }

    public void returnPawnToHome(Pawn pawn) {
        showingDestination = true;
        currentMovementShown = pawn.getNumber();
        destinationMark = new Pawn(pawn.getColour(), Pawn.INITIAL_SQUARE, pawn.getNumber());
        pawnToMove = pawn;
        invalidate();
    }

//TODO
// - Fix eating (related to deleting/inserting pawns in gameBoard)
// - Add 20 to roll and show it to other players
// - Break barriers if player rolls a 6
// - Handle disconnections
// - Polish UI

}

