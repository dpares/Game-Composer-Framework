package composer.parchisgame;

/**
 * Created by fare on 11/11/14.
 */
public class PawnMovement {
    private static final int MAX_STEPS = 10; //Number of steps in a special animation

    private int startSquare; // Starting square
    private int[] startCoords; // Starting coordinates
    private int destSquare; // Final square
    private int[] destCoords; // Final coordinates
    private int movementCoordinates[][]; /* If it's a regular movement, stores each step's square.
                                            If it goes from/to home, stores true coordinates */
    private int currentStep; // Current step, from 0 to movementCoordinates.length
    private int colour; // Needed for movements that enter corridors

    public PawnMovement(int startSquare, int[] startCoords, int destSquare, int[] destCoords,
                        int pawnSize, int colour) {
        this.startSquare = startSquare;
        this.startCoords = startCoords;
        this.destSquare = destSquare;
        this.destCoords = destCoords;
        this.currentStep = 0;
        this.colour = colour;
        this.calculateRoute(pawnSize);
    }

    private void calculateRoute(int pawnSize) {
        if (this.startSquare == 0 || this.destSquare == 0) {
            this.movementCoordinates = new int[MAX_STEPS][2];
            int stepIncrease[] = {(destCoords[0] - startCoords[0]) / MAX_STEPS,
                    (destCoords[1] - startCoords[1]) / MAX_STEPS};
            for (int i = 0; i < this.movementCoordinates.length; i++)
                for (int j = 0; j < 2; j++)
                    this.movementCoordinates[i][j] = startCoords[j] + i * stepIncrease[j] - pawnSize / 2;
        } else if (this.startSquare < this.destSquare) {
            if(this.startSquare <= Pawn.REGULAR_SQUARES && this.destSquare > Pawn.REGULAR_SQUARES) {
                this.movementCoordinates = new int[Pawn.CORRIDOR_START[colour] - startSquare + 1
                        + destSquare - Pawn.REGULAR_SQUARES + 1][2];
                int aux = startSquare;
                for (int i = 0; i < this.movementCoordinates.length; i++)
                    for (int j = 0; j < 2; j++) {
                        this.movementCoordinates[i][j] = aux++;
                        if(aux >= Pawn.CORRIDOR_START[colour])
                            aux = Pawn.REGULAR_SQUARES;
                    }

            } else {
                this.movementCoordinates = new int[this.destSquare - this.startSquare][2];
                for (int i = 0; i < this.movementCoordinates.length; i++)
                    for (int j = 0; j < 2; j++)
                        this.movementCoordinates[i][j] = startSquare + i;
            }
        } else {
            int turningPoint = Pawn.REGULAR_SQUARES - this.startSquare + 1;
            this.movementCoordinates = new int[turningPoint + this.destSquare][2];
            int aux = startSquare;
            for (int i = 0; i < this.movementCoordinates.length; i++)
                for (int j = 0; j < 2; j++) {
                    this.movementCoordinates[i][j] = aux++;
                    if(aux == Pawn.REGULAR_SQUARES)
                        aux = 0;
                }
        }
    }

    public int[] nextStep() {
        return this.movementCoordinates[this.currentStep++];
    }

    public boolean isFinished() {
        return this.currentStep == this.movementCoordinates.length;
    }

    public boolean isSpecial() {
        return this.movementCoordinates.length == MAX_STEPS;
    }

    public int getInitialSquare(){
        return this.startSquare;
    }

    public int getFinalSquare(){
        return this.destSquare;
    }

}
