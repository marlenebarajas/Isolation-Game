import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class BoardState {
    private String[] state;
    private int score;
    private int depth;
    private int[] firstPlayer;
    private int[] secondPlayer; //row and column coordinates of players

    BoardState() {
        String[] newState = {
                "X", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "O"};
        int[] aCoordinates = {0, 0};
        int[] bCoordinates = {7, 7};
        this.state = newState;
        this.depth = 0;
        this.score = 0;
        this.firstPlayer = aCoordinates;
        this.secondPlayer = bCoordinates;
    }

    BoardState(BoardState parent, Point move, char xOrO) {
        this.state = Arrays.copyOf(parent.getBoard(),parent.getBoard().length);
        this.firstPlayer = new int[]{0, 0};
        this.secondPlayer = new int[]{7, 7};
        this.score = 0;
        this.depth = parent.getDepth() + 1;
    }

    void setFirstPlayer(int[] coordinates){
        this.firstPlayer = coordinates;
    }

    void setSecondPlayer(int[] coordinates){
        this.secondPlayer = coordinates;
    }

    int getScore(){
        return score;
    }

    int getDepth() {
        return depth;
    }

    String[] getBoard() {
        return state;
    }

    int[] getFirstPlayer() {
        return firstPlayer;
    }

    int[] getSecondPlayer() {
        return secondPlayer;
    }

    String getSpace(int[] move) {
        int index = (move[1] * 8) + (move[0]); //first index is x coordinate, second is y coordinate
        String[] state = getBoard();
        return state[index];
    }

    String getSpace(int x, int y) {
        int index = (y * 8) + (x); //first index is x coordinate, second is y coordinate
        String[] state = getBoard();
        return state[index];
    }

    void changeCoordinates(char player, int x, int y) {
        int oldPlayerIndex;
        int newPlayerIndex = (y * 8) + (x);
        int[] newCoordinates = {x, y};
        switch (player) {
            case 'X':
                oldPlayerIndex = (this.getFirstPlayer()[1] * 8) + this.getFirstPlayer()[0];
                this.setFirstPlayer(newCoordinates);
                this.state[oldPlayerIndex] = "#";
                this.state[newPlayerIndex] = "X";
                break;
            case 'O':
                oldPlayerIndex = (this.getSecondPlayer()[1] * 8) + this.getSecondPlayer()[0];
                this.setSecondPlayer(newCoordinates);
                this.state[oldPlayerIndex] = "#";
                this.state[newPlayerIndex] = "O";
                break;
        }
    }

    void changeCoordinates(char player, int[] coordinates) {
        int x = coordinates[0];
        int y = coordinates[1];
        this.changeCoordinates(player, x, y);
    }

    int makeScore(char xOrO){
        int opponentScore = 0;
        int compScore = 0;
        switch(xOrO){
            case 'X':
                opponentScore = evaluate(this.secondPlayer[0], this.secondPlayer[1]);
                compScore = evaluate(this.firstPlayer[0], this.firstPlayer[1]);
                break;
            case 'O':
                opponentScore = evaluate(this.firstPlayer[0], this.firstPlayer[1]);
                compScore= evaluate(this.secondPlayer[0], this.secondPlayer[1]);
                break;
        }
        return compScore - opponentScore;
    }

    /**
     * This is similar to makeChildren() wherein we traverse every free space, but this time we are adding a "score"
     * value to it at each space. Directly adjacent free spaces are worth more.
     **/
    int evaluate(int x, int y){
        int index = (y*8) + x;
        int score = 0;
        int multiplier = 0;
        // POSSIBLE MOVES IN ROW - LEFT OF PLAYER
        int start = index-1;
        int end = (index/8) * 8;
        if(x!=0){ // if there are moves to the left
            for (int i=start; i>=end; i--) {
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        // POSSIBLE MOVES IN ROW - RIGHT OF PLAYER
        start = index+1;
        end = index+(7-x);
        if(x!=7){ //if there are moves to the right
            for (int i = start; i<=end; i++) {
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        // POSSIBLE MOVES IN COLUMN - ABOVE PLAYER
        start = index-8;
        end = x;
        if(y!=0){
            for (int i = start; i > end; i -= 8) {
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        // POSSIBLE MOVES IN COLUMN - BELOW PLAYER
        start = index+8;
        end = ((7-y)*8) + index;
        if(y!=7){
            for (int i=start; i< end; i += 8) {
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - RIGHT OF PLAYER
        start = index-7;
        end = index-((7-x)*7);
        if(x!=7 && y!=0){ // if there are moves to the right
            for (int i=start; i>end; i-=7) {
                if(i<8) break;
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - LEFT OF PLAYER
        start = index+7;
        end = index+(x*7);
        if(x!=0 && y!=7){
            for (int i=start; i<end;i+=7) {
                if(i>55) break;
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        // POSSIBLE MOVES IN NEGATIVE DIAGONAL - RIGHT OF PLAYER
        start = index+9;
        end = 63 - ((7-y)*9);
        if(x!=7 && y!=7){
            for (int i = start; i <= end; i+=9) {
                if(i>55) break;
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        // POSSIBLE MOVES IN NEGATIVE DIAGONAL - LEFT OF PLAYER
        start = index-9;
        if(x!=0 && y!=0){
            for (int i = start; i >= 9; i-=9) {
                if(i<8) break;
                if (getBoard()[i].equals("-")) {
                    if(i==start) multiplier++; // empty spots immediately surrounding are worth 3 times as much
                    score++;
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
            }
        }
        return score*multiplier;
    }

    int evaluate(Point coordinates) {
        int x = (int) coordinates.getX();
        int y = (int) coordinates.getY();
        return evaluate(x, y);
    }

    /**
     * This method populates the frontier with the children of the current board state.
     * @param player True if computer is playing X
     *               False if computer is playing O
     * @return ArrayList<Point> that holds any possible move for either X or O (depending on parameter)
     **/
    PriorityQueue<Point> makeChildren(boolean player) {
        PriorityQueue<Point> frontier = new PriorityQueue<>(new StateComparator());
        int[] startCoordinates;
        int x = 0;
        int y = 0;
        if (player) { //computer is X
            startCoordinates = getFirstPlayer();
            x = startCoordinates[0];
            y = startCoordinates[1];
        } else { //computer is O
            startCoordinates = getSecondPlayer();
            x = startCoordinates[0];
            y = startCoordinates[1];
        }
        int index = (y*8) + x;
        // POSSIBLE MOVES IN ROW - LEFT OF PLAYER
        int start = index-1;
        int end = (index/8) * 8;
        int loopCount = 1;
        if(x!=0){ // if there are moves to the left
            for (int i=start; i>=end; i--) {
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x-loopCount, y));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN ROW - RIGHT OF PLAYER
        start = index+1;
        end = index+(7-x);
        loopCount = 1;
        if(x!=7){ //if there are moves to the right
            for (int i = start; i<=end; i++) {
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x+loopCount, y));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN COLUMN - ABOVE PLAYER
        start = index-8;
        end = x;
        loopCount = 1;
        if(y!=0){ //if there are moves above
            for (int i = start; i > end; i -= 8) {
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x, y-loopCount));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN COLUMN - BELOW PLAYER
        start = index+8;
        end = ((7-y)*8) + index;
        loopCount = 1;
        if(y!=7){
            for (int i=start; i<=end; i += 8) {
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x, y+loopCount));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - RIGHT OF PLAYER
        start = index-7;
        end = index-((7-x)*7);
        loopCount = 1;
        if(x!=7 && y!=0){ // if there are moves to the right
            for (int i=start; i>end; i-=7) {
                if(i<8) break;
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x+loopCount, y-loopCount));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - LEFT OF PLAYER
        start = index+7;
        end = index+(x*7);
        loopCount = 1;
        if(x!=0 && y!=7){
            for (int i=start; i<end;i+=7) {
                if(i>55) break;
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x-loopCount, y+loopCount));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN NEGATIVE DIAGONAL - RIGHT OF PLAYER
        start = index+9;
        end = 63 - ((7-y)*9);
        loopCount = 1;
        if(x!=7 && y!=7){
            for (int i = start; i <= end; i+=9) {
                if(i>55) break;
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x+loopCount, y+loopCount));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN NEGATIVE DIAGONAL - LEFT OF PLAYER
        start = index-9;
        loopCount = 1;
        if(x!=0 && y!=0){
            for (int i = start; i >= 9; i-=9) {
                if(i<8) break;
                if (getBoard()[i].equals("-")) {
                    frontier.add(new Point(x-loopCount, y-loopCount));
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        return frontier;
    }

    /**
     * Overrides the comparator used in PriorityQueue so that children are always ordered from least score to the highest.
     */
    public class StateComparator implements Comparator<Point> {
        @Override
        public int compare(Point t1, Point t2){
            if(evaluate(t1)>evaluate(t2)){
                return 1;
            }
            else if(evaluate(t1)<evaluate(t2)){
                return -1;
            } return 0;
        }
    }
}
