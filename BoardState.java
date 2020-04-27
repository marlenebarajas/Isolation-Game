import java.util.ArrayList;

public class BoardState {
    private BoardState parent;
    private String[] state;
    private int depth;
    private int[] firstPlayer; private int[] secondPlayer; //row and column coordinates of players
    private ArrayList<BoardState> frontier = new ArrayList<>();

    public BoardState(){
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
        this.parent = null;
        this.firstPlayer = aCoordinates;
        this.secondPlayer = bCoordinates;
    }
    public BoardState(String[] state, int depth, int[] firstPlayer, int[] secondPlayer, BoardState parent) {
        this.state = state;
        this.depth = depth;
        this.parent = parent;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    void prune(){
        this.parent = null;
        this.frontier = null;
    }

    int getDepth(){
        return depth;
    }

    String[] getBoard(){
        return state;
    }
    BoardState getParent(){
        return parent;
    }

    ArrayList<BoardState> getChildren(){
        return frontier;
    }

    int[] getFirstPlayer(){
        return firstPlayer;
    }

    int[] getSecondPlayer(){
        return secondPlayer;
    }

    void changeCoordinates(char player, int x, int y){
        int oldPlayerIndex;
        int newPlayerIndex = (y*8) + x;
        int[] newCoordinates = {x, y};
        switch(player) {
            case 'X':
                oldPlayerIndex = (this.firstPlayer[1] * 8) + this.firstPlayer[0];
                this.firstPlayer = newCoordinates;
                this.state[oldPlayerIndex] = "#";
                this.state[newPlayerIndex] = "X";
            case 'O':
                oldPlayerIndex = (this.secondPlayer[1] * 8) + this.secondPlayer[0];
                this.secondPlayer = newCoordinates;
                this.state[oldPlayerIndex] = "#";
                this.state[newPlayerIndex] = "O";
        }
    }

    void setDepth(int depth){
        this.depth = depth;
    }

    void addChild(BoardState child){
        frontier.add(child);
    }

    void setBoard(String[] newState){
        this.state = newState;
    }
    void clearFrontier() {
        this.frontier = new ArrayList<>();
    }

    @Override
    public boolean equals(Object other){
        String[] board = this.state;
        String[] otherBoard = ((BoardState)other).getBoard();
        for(int i=0; i<64; i++){
            if(!(board[i]==otherBoard[i])) return false;
        }
        return true;
    }
}
