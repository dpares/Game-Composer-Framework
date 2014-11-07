package pfc.parchisgame;

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

    private int resDBoard = R.drawable.tablero1;
    private Bitmap mBoard = null;
    private Bitmap mPawn[] = new Bitmap[4];
    private Paint mPaint = new Paint();
    private int mBoardSize;
    private int mPawnSize;
    private int mXOffset;
    private int mYOffset;
    private PosG mCasa[][] = new PosG[4][4];
    private PosG mCorredor[][] = new PosG[68][2];
    private PosG mLlegada[][][] = new PosG[4][7][2];
    private PosG mMeta[][] = new PosG[4][4];
    private double mScale;
    private PosG mPosPawnJg[] = new PosG[4];

    private List<ParchisPlayer> players;

    private static ParchisView instance;

    private class PosG {
        public int x;
        public int y;

        public PosG(int newX, int newY) {
            x = (int) (((newX - 17) * mScale) + 0.5 + mXOffset);
            y = (int) (((newY - 22) * mScale) + 0.5 + mYOffset);
        }

        public PosG(int newX, int newY, boolean conv) {
            x = newX;
            y = newY;
        }
    }

    public ParchisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(ParchisView.instance == null)
            ParchisView.instance = this;
    }

    public ParchisView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(ParchisView.instance == null)
            ParchisView.instance = this;
    }

    public static ParchisView getInstance(){
        return ParchisView.instance;
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
        mBoardSize = w;
        if (mBoardSize > h) mBoardSize = h;
        mXOffset = ((w - mBoardSize) / 2);
        mYOffset = ((h - mBoardSize) / 2);
        mScale = (mBoardSize * 1.0) / 439;
        mPawnSize = (int) (19 * mScale);
        mBoard = loadBitmap(r.getDrawable(resDBoard), mBoardSize);
        mPawn[0] = loadBitmap(r.getDrawable(R.drawable.yellowplayer), mPawnSize);
        mPawn[1] = loadBitmap(r.getDrawable(R.drawable.blueplayer), mPawnSize);
        mPawn[2] = loadBitmap(r.getDrawable(R.drawable.redplayer), mPawnSize);
        mPawn[3] = loadBitmap(r.getDrawable(R.drawable.greenplayer), mPawnSize);
        initGBoard();
    }

    protected void initGBoard() {
        int i;
        int posx, posy;

        for( i=0; i<8; i++ ){
            posx = 426 - (i * 20);
            mCorredor[i][0] = new PosG(posx, 198);
            mCorredor[i][1] = new PosG(posx, 174);
        }
        for( i=8; i<16; i++ ){
            posy = 173 - ((i - 8) * 20);
            mCorredor[i][0] = new PosG(261, posy);
            mCorredor[i][1] = new PosG(285, posy);
        }
        mCorredor[16][0] = new PosG(215, 33);
        mCorredor[16][1] = new PosG(239, 33);
        for( i=17; i<25; i++ ){
            posy = 33 + ((i - 17) * 20);
            mCorredor[i][0] = new PosG(193, posy);
            mCorredor[i][1] = new PosG(169, posy);
        }
        for( i=25; i<33; i++ ){
            posx = 168 - ((i - 25) * 20);
            mCorredor[i][0] = new PosG(posx, 198);
            mCorredor[i][1] = new PosG(posx, 174);
        }
        mCorredor[33][0] = new PosG(28, 220);
        mCorredor[33][1] = new PosG(28, 244);
        for( i=34; i<42; i++ ){
            posx = 28 + ((i - 34) * 20);
            mCorredor[i][0] = new PosG(posx, 266);
            mCorredor[i][1] = new PosG(posx, 290);
        }
        for( i=42; i<50; i++ ){
            posy = 291 + ((i - 42) * 20);
            mCorredor[i][0] = new PosG(193, posy);
            mCorredor[i][1] = new PosG(169, posy);
        }
        mCorredor[50][0] = new PosG(215, 431);
        mCorredor[50][1] = new PosG(239, 431);
        for( i=51; i<59; i++ ){
            posy = 431 - ((i - 51) * 20);
            mCorredor[i][0] = new PosG(261, posy);
            mCorredor[i][1] = new PosG(285, posy);
        }
        for( i=59; i<67; i++ ){
            posx = 286 + ((i - 59) * 20);
            mCorredor[i][0] = new PosG(posx, 266);
            mCorredor[i][1] = new PosG(posx, 290);
        }
        mCorredor[67][0] = new PosG(426, 220);
        mCorredor[67][1] = new PosG(426, 244);

        mCasa[0][0] = new PosG(mCorredor[ 4][0].x, mCorredor[13][0].y, false);
        mCasa[0][1] = new PosG(mCorredor[ 2][0].x, mCorredor[13][0].y, false);
        mCasa[0][2] = new PosG(mCorredor[ 4][0].x, mCorredor[11][0].y, false);
        mCasa[0][3] = new PosG(mCorredor[ 2][0].x, mCorredor[11][0].y, false);
        mCasa[1][0] = new PosG(mCorredor[30][0].x, mCorredor[19][0].y, false);
        mCasa[1][1] = new PosG(mCorredor[28][0].x, mCorredor[19][0].y, false);
        mCasa[1][2] = new PosG(mCorredor[30][0].x, mCorredor[21][0].y, false);
        mCasa[1][3] = new PosG(mCorredor[28][0].x, mCorredor[21][0].y, false);
        mCasa[2][0] = new PosG(mCorredor[36][0].x, mCorredor[45][0].y, false);
        mCasa[2][1] = new PosG(mCorredor[38][0].x, mCorredor[45][0].y, false);
        mCasa[2][2] = new PosG(mCorredor[36][0].x, mCorredor[47][0].y, false);
        mCasa[2][3] = new PosG(mCorredor[38][0].x, mCorredor[47][0].y, false);
        mCasa[3][0] = new PosG(mCorredor[62][0].x, mCorredor[55][0].y, false);
        mCasa[3][1] = new PosG(mCorredor[64][0].x, mCorredor[55][0].y, false);
        mCasa[3][2] = new PosG(mCorredor[62][0].x, mCorredor[53][0].y, false);
        mCasa[3][3] = new PosG(mCorredor[64][0].x, mCorredor[53][0].y, false);

        for( i=0; i<7; i++ ){
            posx = 406 - i * 20;
            mLlegada[0][i][0] = new PosG(posx, 219);
            mLlegada[0][i][1] = new PosG(posx, 243);
        }
        for( i=0; i<7; i++ ){
            posy = 53 + i * 20;
            mLlegada[1][i][0] = new PosG(215, posy);
            mLlegada[1][i][1] = new PosG(239, posy);
        }
        for( i=0; i<7; i++ ){
            posx = 48 + i * 20;
            mLlegada[2][i][0] = new PosG(posx, 219);
            mLlegada[2][i][1] = new PosG(posx, 243);
        }
        for( i=0; i<7; i++ ){
            posy = 411 - i * 20;
            mLlegada[3][i][0] = new PosG(215, posy);
            mLlegada[3][i][1] = new PosG(239, posy);
        }

        int x1 = 188; int x2 = 208; int x3 = 228; int x4 = 248; int x5 = 268;
        int y1 = 193; int y2 = 213; int y3 = 233; int y4 = 253; int y5 = 273;
        mMeta[0][0] = new PosG(x5-2, y2);
        mMeta[0][1] = new PosG(x5-2, y3);
        mMeta[0][2] = new PosG(x5-2, y4);
        mMeta[0][3] = new PosG(x4-2, y3);
        mMeta[1][0] = new PosG(x2, y1);
        mMeta[1][1] = new PosG(x3, y1);
        mMeta[1][2] = new PosG(x4, y1);
        mMeta[1][3] = new PosG(x3, y2);
        mMeta[2][0] = new PosG(x1, y2);
        mMeta[2][1] = new PosG(x1, y3);
        mMeta[2][2] = new PosG(x1, y4);
        mMeta[2][3] = new PosG(x2, y3);
        mMeta[3][0] = new PosG(x2, y5-2);
        mMeta[3][1] = new PosG(x3, y5-2);
        mMeta[3][2] = new PosG(x4, y5-2);
        mMeta[3][3] = new PosG(x3, y4-2);
    }

    private void calcPawnJgPos() {
            //if (lugar == PosFicha.CORREDOR) {
                mPosPawnJg[0] = new PosG(mCorredor[0][0].x, mCorredor[0][0].y, false);
            /*} else if (lugar == PosFicha.LLEGADA) {
                mPosPawnJg[i] = new PosG(mLlegada[c][pos][ind].x, mLlegada[c][pos][ind].y, false);
            }*/
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBoard == null) {
            Resources r = this.getContext().getResources();
            mBoard = loadBitmap(r.getDrawable(resDBoard), mBoardSize);
        }
        canvas.drawBitmap(mBoard, mXOffset, mYOffset, mPaint);
        drawPawns(canvas);


    }

    private void drawPawns(Canvas canvas){
        for(ParchisPlayer player: this.players){
            int colour = player.getColour().ordinal();
            if(colour != ParchisPlayer.Colour.UNDEFINED.ordinal()) {
                List<Pawn> pawns = player.getPawns();
                for (int i = 0; i < 4; i++) {
                    if(colour==3)
                        canvas.drawBitmap(mPawn[colour], mMeta[0][i].x, mMeta[0][i].y, mPaint);
                    else
                        canvas.drawBitmap(mPawn[colour], mLlegada[i][i][0].x, mLlegada[i][i][0].y, mPaint);
                }
            }
        }
    }

    public void setPlayer(ParchisPlayer p, int i){
        this.players.add(i,p);
    }

    public void newGame() {
        this.players = new ArrayList<ParchisPlayer>();
        ParchisPlayer p = new ParchisPlayer("Hola", "avatar01", false);
        p.setColour(ParchisPlayer.Colour.BLUE);
        this.setPlayer(p, 0);
        p = new ParchisPlayer("Hola", "avatar01", false);
        p.setColour(ParchisPlayer.Colour.RED);
        this.setPlayer(p, 1);
    }


}

