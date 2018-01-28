/**
 * Created by Grzegorz on 2017-05-22.
 */
import java.util.Random;
import static java.lang.Thread.sleep;
/**
 * actually it is the whole thing. Module compute everything, communicate with controller to change position of blocks,
 * and gives component map to show map, score etc.
 */
public class Module {
    /**
     * direction is used for telling module, which key was pressed and what signal is send by controller
     */
    public enum Direction {
        NONE, LEFT, RIGHT, DOWN, MAXDOWN, SPACE
    }
    /**
     * what is chance for drawing tetrimino, for tetriminos S and Z it is getting higher every line is cleared
     */
    private static final int CHANCE_FOR_FIGURE = 40;
    /**
     * how many lines has been cleared since start of game
     */
    private int lineCleared;
    /**
     * it tells module, when was last player action time, when he last made a move
     */
    private long playerActionTime;
    /**
     * links View with module, it is only linked because view class needs to know when to repaint component
     */
    private View view;
    /**
     * two-dimensional map of bytes, each byte informs us what type of block (from which tetrimino) is placed everywhere
     * it is the heart of the game, it is in most functions
     */
    private byte[][] area;
    private int[][] positionOfNextFigure;
    /**
     * array of x positions of the current tetrimino
     */
    private int[] XpositionOfFigure;
    /**
     * array of y positions of the current tetrimino
     */
    private int[] YpositionOfFigure;
    /**
     * x and y of center of current tetrimino
     */
    private int[] centerOfFigure;
    /**
     * score of current game
     */
    private long score;
    /**
     * constant that informs how many points you get by clearing one single line
     */
    private int scoreForLine;
    /**
     * byte informing us about the type of current tetrimino
     */
    private byte currentFigure;
    /**
     * it is actually quite different thing from what is says it is. nextFigure is random number between 0 and 280 + line cleared
     * it is used to draw a new tetrimino
     */
    private byte nextFigure;
    /**
     * random numbers generator
     */
    private Random rnd;
    /**
     * it says what direction player chose to move
     */
    private Direction moveDirection;
    /**
     * true when the game is over
     */
    private boolean end;
    /**
     * sets what is delay between refreshing map when the game starts
     */
    private int autoMoveTime;
    /**
     * constructor, sets all parameters for initial values, sets map to be 10x20, score to 0, line cleared to 0, first
     * random figure etc.
     */
    Module() {
        lineCleared = 0;
        end = false;
        area = new byte[10][20];
        positionOfNextFigure = new int[4][2];
        XpositionOfFigure = new int[4];
        YpositionOfFigure = new int[4];
        centerOfFigure = new int[2];
        score = 0;
        scoreForLine = 50;
        rnd = new Random();
        nextFigure = (byte)(rnd.nextInt(7) + 1);
        autoMoveTime = 500;
        moveDirection = Direction.NONE;
        drawFigure();
    }
    /**
     * when game is over, it pauses the game, it is a signal for controller and view class to stop doing what they were doing
     * @return true when game is paused, false if opposite
     */
    public boolean isPaused() {
        return end;
    }
    /**
     * links module with view, look at view parameter info
     * @param view1 what view is supposed to be linked
     */
    public void getView(View view1) {
        this.view = view1;
    }
    /**
     * used by View class, gives information what Tetris Block is in given coordinates
     * @param x x position of area point
     * @param y y position of area point
     * @return byte, information what type of Tetris Block we are dealing with
     */
    public byte getAreaPoint(int x, int y) {
        for (int i = 0; i < 4; i++) {
            if(XpositionOfFigure[i] == x && YpositionOfFigure[i] == y)
                return currentFigure;
        }
        return area[x][y];
    }
    /**
     * used by Controller class, sets time when player made last move
     */
    public void setPlayerActionTime(){
        playerActionTime = System.currentTimeMillis();
    }
    /**
     * pretty much the same as constructor, preparing map and everything for new game. When everything is initialized, it
     * sets end to false to get the game rolling
     */
    public void resetGame() {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 20; j++) {
                area[i][j] = 0;
            }
        }
        autoMoveTime = 500;
        score = 0;
        lineCleared = 0;
        scoreForLine = 50;
        nextFigure = (byte)(rnd.nextInt(7) + 1);
        moveDirection = Direction.NONE;
        drawFigure();
        end = false;
        synchronized (this) {
            this.notify();
        }
    }
    /**
     * there is something to be explained. This function handles time between moves, auto moves and this made by player
     * if after some delay between two automoves player made a move, we need to wait some time more before next automove,
     * to be exact we need to wait another delay time less time, that tells us, when from now was last player action.
     * After that we move figure down and show that by calling show function
     *
     */
    public void step() {
        while(true)
        {
            try {
                sleep(autoMoveTime);
            }
            catch (InterruptedException ex) { }
            while (System.currentTimeMillis() - playerActionTime < autoMoveTime) {
                try {
                    sleep(autoMoveTime - (System.currentTimeMillis() - playerActionTime));
                }
                catch (InterruptedException ex) { }
            }

            moveFigureDown(false);
            if (end) {
                show();
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) { }
                }
            }
            show();
        }
    }
    /**
     * calls function of view to repaint interior of the frame, component
     */
    public void show() {
        view.validate();
        view.repaint();
    }
    /**
     * check all lines in map if is full, to clear it. if it is, then gives some points to score, and if multiple lines
     * are cleared, points for each line that is additional are doubled
     */
    private void checkLines() {
        boolean isFull;
        boolean isEmpty;
        int combo = 0;
        for(int i = 19; i >= 0; i--) {
            isFull = true;
            isEmpty = true;
            for (int j = 0; j < 10; j++) {
                if(area[j][i] == 0)
                    isFull = false;
                if(area[j][i] != 0)
                    isEmpty = false;
            }
            if(isEmpty) break;
            if(isFull) {
                ++lineCleared;
                if(autoMoveTime > 100) autoMoveTime -= 2;
                ++scoreForLine;
                ++combo;
                combo = combo<<1;
                for (int j = 0; j < 10; j++) {
                    area[j][i] = 0;
                }
                dropBlocks(i);
                i = 20;
            }
        }
        score += combo * scoreForLine;
    }
    /**
     * used by View class, gives information what is our current score
     * @return current game score
     */
    public long getScore(){
        return score;
    }
    /**
     * after the line is cleared, it descends all the blocks that were above the cleared line
     */
    private void dropBlocks(int line) {
        /*int k;
        for(int i = 18; i >= 0; i--) {
            for (int j = 0; j < 10; j++) {
                if(area[j][i] != 0) {
                    k = 1;
                    while (area[j][i + k] == 0) {
                        if (i + k == 19) {
                            ++k;
                            break;
                        }
                        ++k;
                    }

                    area[j][i + k - 1] = area[j][i];
                    area[j][i] = 0;
                }
            }
        }*/
        for(int i = line; i > 0; i--) {
            for (int j = 0; j < 10; j++) {
                area[j][i] = area[j][i - 1];
            }
        }
    }
    /**
     * main function, it moves our current tetrimino down after delay time (find autoMoveTime) or if player pressed S or
     * X button, it moves down tetrimino or moves it as low as possible, and another one is draw
     */
    private void moveFigureDown(boolean maximumDown) {
        do {
            boolean isFinished = false;
            for(int i = 0; i < 4; ++i){
                if ( YpositionOfFigure[i] + 1 > 19 || area[XpositionOfFigure[i]][YpositionOfFigure[i] + 1] != 0 ) {
                    isFinished = true;
                    break;
                }
            }
            if ( isFinished ) {
                for(int i = 0; i < 4; ++i)
                    area[XpositionOfFigure[i]][YpositionOfFigure[i]] = currentFigure;
                checkLines();
                drawFigure();
                break;
            }
            else {
                for(int i = 0; i < 4; ++i)
                    ++YpositionOfFigure[i];
                ++centerOfFigure[1];
            }
        } while (maximumDown);
    }
    /**
     * used by controller, send information which move player wants to make
     * @param direction tells us which button player pressed
     */
    public void setDirection(Direction direction)
    {
        moveDirection = direction;
    }
    /**
     * function used by controller, it is used to call proper function that corresponds with the move we wanted to make
     */
    public void playerMove() {
        switch (moveDirection) {
            case LEFT:
                move();
                break;
            case RIGHT:
                move();
                break;
            case DOWN:
                moveFigureDown(false);
                break;
            case MAXDOWN:
                moveFigureDown(true);
            case SPACE:
                rotateFigure();
                break;
            case NONE:
                break;
        }
        moveDirection = Direction.NONE;
    }
    /**
     * moves current Tetrimino left or right, depending on parameter moveDirection. if unable to move, does nothing
     */
    private void move() {
        boolean unableToMove = false;
        for (int i = 0; i < 4; i++) {
            if (moveDirection == Direction.LEFT) {
                if (XpositionOfFigure[i] == 0) {
                    unableToMove = true;
                    break;
                }
                if(area[XpositionOfFigure[i]-1][YpositionOfFigure[i]] != 0) {
                    unableToMove = true;
                    break;
                }
            }
            if (moveDirection == Direction.RIGHT) {
                if (XpositionOfFigure[i] == 9) {
                    unableToMove = true;
                    break;
                }
                if(area[XpositionOfFigure[i]+1][YpositionOfFigure[i]] != 0) {
                    unableToMove = true;
                    break;
                }
            }
        }
        if (!unableToMove) {
            for (int i = 0; i < 4; i++) {
                if(moveDirection == Direction.LEFT) {
                    --XpositionOfFigure[i];
                }
                else if (moveDirection == Direction.RIGHT) {
                    ++XpositionOfFigure[i];
                }
            }
            if (moveDirection == Direction.LEFT) {
                --centerOfFigure[0];
            } else {
                ++centerOfFigure[0];
            }
        }
    }
    /**
     * rotates current Tetrimino. if it is square tetrimino, does nothing, if it is line, a little different algorithm
     * is used, but very similar. It checks if rotation is possible, and if it is true, it rotates figure.
     */
    private void rotateFigure() {
        boolean unableToRotate = false;
        int x = centerOfFigure[0];
        int y = centerOfFigure[1];
        int[][] tempPositions = new int[4][2];
        if(currentFigure != 4) {
            for(int i = 0; i < 4; ++i) {
                for (int j = -1; j < 2; ++j) {
                    for (int k = -1; k < 2; ++k) {
                        if (x + j == XpositionOfFigure[i] && y + k == YpositionOfFigure[i]) {
                            if (x - k < 0 || x - k >= 10 || y + j < 0 || y + j >= 20 || area[x - k][y + j] != 0 ) {
                                unableToRotate = true;
                                break;
                            }
                            tempPositions[i][0] = x - k;
                            tempPositions[i][1] = y + j;
                        }
                    }
                    if(unableToRotate) break;
                }
                if(unableToRotate) break;
            }
        } else { unableToRotate = true; }
        if (currentFigure == 1) {
            for(int i = 0; i < 4; ++i) {
                for (int j = -2; j < 3; j += 2) {
                    for (int k = -2; k < 3; k += 2) {
                        if (x + j == XpositionOfFigure[i] && y + k == YpositionOfFigure[i]) {
                            if (x - k < 0 || x - k >= 10 || y + j < 0 || y + j >= 20 || area[x - k][y + j] != 0 ) {
                                unableToRotate = true;
                                break;
                            }
                            tempPositions[i][0] = x - k;
                            tempPositions[i][1] = y + j;
                        }
                    }
                    if(unableToRotate) break;
                }
                if(unableToRotate) break;
            }
        }
        if (!unableToRotate) {
            for (int i = 0; i < 4; i++) {
                XpositionOfFigure[i] = tempPositions[i][0];
                YpositionOfFigure[i] = tempPositions[i][1];
            }
        }
    }
    private void drawCurrent() {
        switch(currentFigure){
            case 1:
                tetriminoI();
                break;
            case 2:
                tetriminoJ();
                break;
            case 3:
                tetriminoL();
                break;
            case 4:
                tetriminoO();
                break;
            case 5:
                tetriminoS();
                break;
            case 6:
                tetriminoT();
                break;
            case 7:
                tetriminoZ();
                break;
            default:
                break;
        }
        centerOfFigure[0] = 4;
        centerOfFigure[1] = 0;
    }
    private void newDraw() {
        int randomFigure = rnd.nextInt((7*CHANCE_FOR_FIGURE) + lineCleared);
        if(randomFigure < CHANCE_FOR_FIGURE) {
            nextFigure = 1;
        } else if (randomFigure < 2*CHANCE_FOR_FIGURE) {
            nextFigure = 2;
        } else if (randomFigure < 3*CHANCE_FOR_FIGURE) {
            nextFigure = 3;
        } else if (randomFigure < 4*CHANCE_FOR_FIGURE) {
            nextFigure = 4;
        } else if (randomFigure < 5*CHANCE_FOR_FIGURE) {
            nextFigure = 6;
        } else if (randomFigure < ((6*CHANCE_FOR_FIGURE)+(lineCleared>>1)) ) {
            nextFigure = 5;
        } else {
            nextFigure = 7;
        }
    }
    /**
     * chooses what will be the next figure to draw using parameter nextFigure, and sets parameter currentFigure as the
     * next was
     */
    private void drawFigure() {
        if(end) return;
        currentFigure = nextFigure;
        drawCurrent();
        newDraw();
        showNextFigure();
    }
    private void tetriminoI() {
        for(int i = 0; i < 4; ++i) {
            if(area[i+3][0] != 0) {
                end = true;
            }
            XpositionOfFigure[i] = i + 3;
            YpositionOfFigure[i] = 0;
        }
    }
    private void tetriminoJ() {
        for(int i = 0; i < 3; ++i) {
            if(area[i+3][0] != 0) {
                end = true;
            }
            XpositionOfFigure[i] = i + 3;
            YpositionOfFigure[i] = 0;
        }
        if(area[5][1] != 0) {
            end = true;
        }
        XpositionOfFigure[3] = 5;
        YpositionOfFigure[3] = 1;
    }
    private void tetriminoL() {
        for(int i = 0; i < 3; ++i) {
            if(area[i+3][0] != 0) {
                end = true;
            }
            XpositionOfFigure[i] = i + 3;
            YpositionOfFigure[i] = 0;
        }
        if(area[3][1] != 0) {
            end = true;
        }
        XpositionOfFigure[3] = 3;
        YpositionOfFigure[3] = 1;
    }
    private void tetriminoO() {
        for(int i = 0; i < 4; ++i) {
            if(area[(i%2)+4][i>>1] != 0) {
                end = true;
            }
            XpositionOfFigure[i] = (i % 2) + 4;
            YpositionOfFigure[i] = i >> 1;
        }
    }
    private void tetriminoS() {
        for(int i = 0; i < 2; ++i) {
            if(area[i+3][1] != 0 || area[i+4][0] != 0) {
                end = true;
            }
            XpositionOfFigure[i] = i + 3;
            YpositionOfFigure[i] = 1;
            XpositionOfFigure[i+2] = i + 4;
            YpositionOfFigure[i+2] = 0;
        }
    }
    private void tetriminoT() {
        for(int i = 0; i < 3; ++i) {
            if(area[i+3][0] != 0) {
                end = true;
            }
            XpositionOfFigure[i] = i + 3;
            YpositionOfFigure[i] = 0;
        }
        if(area[4][1] != 0) {
            end = true;
        }
        XpositionOfFigure[3] = 4;
        YpositionOfFigure[3] = 1;
    }
    private void tetriminoZ() {
        for(int i = 0; i < 2; ++i) {
            if(area[i+3][0] != 0 || area[i+4][1] != 0) {
                end = true;
            }
            XpositionOfFigure[i] = i + 3;
            YpositionOfFigure[i] = 0;
            XpositionOfFigure[i+2] = i + 4;
            YpositionOfFigure[i+2] = 1;
        }
    }
    public int getXnextFigure(int blockNumber){
        return positionOfNextFigure[blockNumber][0];
    }
    public int getYnextFigure(int blockNumber){
        return positionOfNextFigure[blockNumber][1];
    }
    public byte getNextFigure(){
        return nextFigure;
    }
    private void showNextFigure() {
        positionOfNextFigure[0][0] = 1;
        positionOfNextFigure[0][1] = 0;
        positionOfNextFigure[1][0] = 1;
        positionOfNextFigure[1][1] = 1;
        switch(nextFigure){
            case 1:
                for(int i = 2; i < 4; i++) {
                    positionOfNextFigure[i][0] = 1;
                    positionOfNextFigure[i][1] = i;
                }
                break;
            case 2:
                positionOfNextFigure[2][0] = 1;
                positionOfNextFigure[2][1] = 2;
                positionOfNextFigure[3][0] = 0;
                positionOfNextFigure[3][1] = 2;
                break;
            case 3:
                positionOfNextFigure[2][0] = 1;
                positionOfNextFigure[2][1] = 2;
                positionOfNextFigure[3][0] = 2;
                positionOfNextFigure[3][1] = 2;
                break;
            case 4:
                positionOfNextFigure[2][0] = 0;
                positionOfNextFigure[2][1] = 0;
                positionOfNextFigure[3][0] = 0;
                positionOfNextFigure[3][1] = 1;
                break;
            case 5:
                positionOfNextFigure[2][0] = 2;
                positionOfNextFigure[2][1] = 0;
                positionOfNextFigure[3][0] = 0;
                positionOfNextFigure[3][1] = 1;
                break;
            case 6:
                positionOfNextFigure[2][0] = 0;
                positionOfNextFigure[2][1] = 0;
                positionOfNextFigure[3][0] = 2;
                positionOfNextFigure[3][1] = 0;
                break;
            case 7:
                positionOfNextFigure[2][0] = 0;
                positionOfNextFigure[2][1] = 0;
                positionOfNextFigure[3][0] = 2;
                positionOfNextFigure[3][1] = 1;
                break;
            default:
                break;

        }
    }
}

