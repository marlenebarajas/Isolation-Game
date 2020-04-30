import java.util.ArrayList;
import java.util.Arrays;

public class BoardState {
    private BoardState parent;
    private String[] state;
    private int depth;
    private int score;
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
        this.parent = null;
        this.firstPlayer = aCoordinates;
        this.secondPlayer = bCoordinates;
    }

    BoardState(String[] state, int depth, int[] firstPlayer, int[] secondPlayer, BoardState parent) {
        this.state = state;
        this.depth = depth;
        this.parent = parent;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    int getDepth() {
        return depth;
    }

    String[] getBoard() {
        return state;
    }

    BoardState getParent() {
        return parent;
    }

    int[] getFirstPlayer() {
        return firstPlayer;
    }

    int[] getSecondPlayer() {
        return secondPlayer;
    }

    int getScore() {
        return score;
    }

    String getSpace(int[] move) {
        int index = (move[1] * 8) + (move[0]); //first index is x coordinate, second is y coordinate
        String[] state = getBoard();
        return state[index];
    }

    void changeCoordinates(char player, int x, int y) {
        int oldPlayerIndex;
        int newPlayerIndex = (y * 8) + (x);
        int[] newCoordinates = {x, y};
        switch (player) {
            case 'X':
                oldPlayerIndex = (getFirstPlayer()[1] * 8) + getFirstPlayer()[0];
                this.firstPlayer = newCoordinates;
                this.state[oldPlayerIndex] = "#";
                this.state[newPlayerIndex] = "X";
                break;
            case 'O':
                oldPlayerIndex = (getSecondPlayer()[1] * 8) + getSecondPlayer()[0];
                this.secondPlayer = newCoordinates;
                this.state[oldPlayerIndex] = "#";
                this.state[newPlayerIndex] = "O";
                break;
        }
    }

    void changeCoordinates(char player, int[] coordinates) {
        int x = coordinates[0];
        int y = coordinates[1];
        changeCoordinates(player, x, y);
    }

    void setDepth(int depth) {
        this.depth = depth;
    }

    void setBoard(String[] newState) {
        this.state = newState;
    }

    void setScore(int score) {
        this.score = score;
    }

    /**
     * (Possible Moves * Degrees of Freedom) - Opponent's Possible Moves
     **/
    int evaluate() {
        ArrayList<BoardState> frontier = makeChildren(true);
        int possibleMoves = 3 * frontier.size();
        int opponentMoves = frontier.size();
        return (possibleMoves - opponentMoves);

    }

    /**
     * This method populates the frontier with the children of the current board state.
     * @param player True if computer is playing X
     *               False if computer is playing O
     * @return ArrayList<BoardState> that holds any possible move for either X or O (depending on parameter)
     **/
    public ArrayList<BoardState> makeChildren(boolean player) {
        ArrayList<BoardState> frontier = new ArrayList<>();
        int[] startCoordinates;
        char xOrO;
        int x = 0;
        int y = 0;
        if (player) { //computer is X
            xOrO = 'X';
            startCoordinates = getFirstPlayer();
            x = startCoordinates[0];
            y = startCoordinates[1];
        } else { //computer is O
            xOrO = 'O';
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
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x - loopCount, y);
                    frontier.add(child);
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
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x + loopCount, y);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN COLUMN - ABOVE PLAYER
        start = index-8;
        end = x-1;
        loopCount = 1;
        if(y!=0){ //if there are moves above
            for (int i = start; i >= end; i -= 8) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x, y - loopCount);
                    frontier.add(child);
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
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x, y + loopCount);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - RIGHT OF PLAYER
        start = index-7;
        end = index+((7-x)*7);
        loopCount = 1;
        if(x!=7 && y!=0){ // if there are moves to the right
            for (int i=start; i>=end; i-=7) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x + loopCount, y - loopCount);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - LEFT OF PLAYER
        start = index+7;
        end = index+((7-y)*7);
        loopCount = 1;
        if(x!=0 && y!=7){
            for (int i=start; i<=end;i+=7) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x - loopCount, y + loopCount);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN NEGATIVE DIAGONAL - RIGHT OF PLAYER
        start = index+9;
        end = 63 - ((x-y) * 8);
        loopCount = 1;
        if(x!=7 && y!=7){
            for (int i = start; i <= end; i+=9) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x + loopCount, y + loopCount);
                    frontier.add(child);
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
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(getBoard(), getDepth()+1, getFirstPlayer(), getSecondPlayer(), this);
                    child.changeCoordinates(xOrO, x - loopCount, y - loopCount);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        return frontier;
    }

    /**
     * This method calculates how many options either player might have. Differs from the other makeChildren() method
     * because this method does not change any coordinates or otherwise change the state of the board.
     * @return ArrayList<BoardState> that holds any possible move for either X or O (they can make the same moves)
     */
    public ArrayList<BoardState> makeChildren() {
        ArrayList<BoardState> frontier = new ArrayList<>();
        //In this case, the result is the same whether we use X or O so we will simply use X.
        char xOrO = 'X';
        int[] startCoordinates = getFirstPlayer();
        int x = startCoordinates[0];
        int y = startCoordinates[1];
        int index = (y*8) + x;
        // POSSIBLE MOVES IN ROW - LEFT OF PLAYER
        int start = index-1;
        int end = (index/8) * 8;
        int loopCount = 1;
        if(x!=0){ // if there are moves to the left
            for (int i=start; i>=end; i--) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
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
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN COLUMN - ABOVE PLAYER
        start = index-8;
        end = x-1;
        loopCount = 1;
        if(y!=0){ //if there are moves above
            for (int i = start; i >= end; i -= 8) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
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
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - RIGHT OF PLAYER
        start = index-7;
        end = index+((7-x)*7);
        loopCount = 1;
        if(x!=7 && y!=0){ // if there are moves to the right
            for (int i=start; i>=end; i-=7) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL - LEFT OF PLAYER
        start = index+7;
        end = index+((7-y)*7);
        loopCount = 1;
        if(x!=0 && y!=7){
            for (int i=start; i<=end;i+=7) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        // POSSIBLE MOVES IN NEGATIVE DIAGONAL - RIGHT OF PLAYER
        start = index+9;
        end = 63 - ((x-y) * 8);
        loopCount = 1;
        if(x!=7 && y!=7){
            for (int i = start; i <= end; i+=9) {
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
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
                if (getBoard()[i].equals("-")) {
                    BoardState child = new BoardState(state, depth + 1, firstPlayer, secondPlayer, this);
                    frontier.add(child);
                }
                else break; //if the next space isn't free, then the rest of this path isn't free
                loopCount++;
            }
        }
        return frontier;
    }

    @Override
    public boolean equals(Object other) {
        String[] board = this.state;
        String[] otherBoard = ((BoardState) other).getBoard();
        for (int i = 0; i < 64; i++) {
            if (!(board[i] == otherBoard[i])) return false;
        }
        return true;
    }
}
