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
import android.view.View;

import com.ojs.R;

import java.util.ArrayList;
import java.util.List;

public class ParchisView extends View {

    private Bitmap boardBitmap = null;
    private Bitmap pawnBitmaps[] = new Bitmap[4];
    private Bitmap avatarBitmaps[] = new Bitmap[4];
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

        loadAvatarBitmaps();
        initGBoard();
    }

    private void loadAvatarBitmaps(){
        if (this.players != null) {
            for (int i = 0; i < this.players.size(); i++) {
                int colour = this.players.get(i).getColour().ordinal();
                if(colour != ParchisPlayer.Colour.UNDEFINED.ordinal())
                    avatarBitmaps[colour] = loadBitmap(this.players.get(i).getAvatarDrawable(), pawnSize * 2);
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

        avatarPositions[0] = new PosG(squarePositions[4][0].x+pawnSize/2+2,
                squarePositions[13][0].y+pawnSize/2+2, false);
        avatarPositions[1] = new PosG(squarePositions[30][0].x+pawnSize/2+2,
                squarePositions[19][0].y+pawnSize/2+2, false);
        avatarPositions[2] = new PosG(squarePositions[36][0].x+pawnSize/2+2,
                squarePositions[45][0].y+pawnSize/2+2, false);
        avatarPositions[3] = new PosG(squarePositions[62][0].x+pawnSize/2+2,
                squarePositions[55][0].y+pawnSize/2+2, false);
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
            drawPawns(canvas);
        }


    }

    private void drawAvatars(Canvas canvas) {
        for (int i = 0; i < this.players.size(); i++) {
            int colour = this.players.get(i).getColour().ordinal();
            if(colour != ParchisPlayer.Colour.UNDEFINED.ordinal())
                canvas.drawBitmap(avatarBitmaps[colour], avatarPositions[colour].x,
                        avatarPositions[colour].y, paint);
        }
    }

    private void drawPawns(Canvas canvas) {
        for (ParchisPlayer player : this.players) {
            int colour = player.getColour().ordinal();
            if (colour != ParchisPlayer.Colour.UNDEFINED.ordinal()) {
                List<Pawn> pawns = player.getPawns();
                for (int i = 0; i < 4; i++) {
                    int pos = pawns.get(i).getSquare();
                    if (pos == Pawn.INITIAL_SQUARE)
                        canvas.drawBitmap(pawnBitmaps[colour], homePositions[colour][i].x,
                                homePositions[colour][i].y, paint);
                    else if (pos == Pawn.LAST_SQUARE)
                        canvas.drawBitmap(pawnBitmaps[colour], finishPositions[colour][i].x,
                                finishPositions[colour][0].y, paint);
                    else if(pos > Pawn.REGULAR_SQUARES) {
                        int corridorPos = pos - Pawn.REGULAR_SQUARES;
                        canvas.drawBitmap(pawnBitmaps[colour], corridorPositions[colour][corridorPos][0].x,
                                corridorPositions[colour][corridorPos][0].y,paint);
                    } else
                        canvas.drawBitmap(pawnBitmaps[colour],squarePositions[pos][0].x,
                                squarePositions[pos][0].y,paint);
                }
            }
        }
    }

    public void updatePlayers(List<ParchisPlayer> players) {
            this.players = players;
            loadAvatarBitmaps();
        invalidate();
    }

    public void newGame() {
        this.players = new ArrayList<ParchisPlayer>();
    }


}

