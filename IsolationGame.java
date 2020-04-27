public class IsolationGame {
    //BOARD STATE VARS
    private String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private String[] columnLabels = {" ", "1", "2", "3", "4", "5", "6", "7", "8"};
    private int rowStart; private int rowEnd;
    //GAME LOGIC VARS
    private char computer; private char opponent; private char currentPlayer; // two players
    private long timeLimit = 20; private long elapsedTime;// time limit in seconds
    private ArrayList<BoardState> frontier; // holds the successors of current move
    private int[] bestPossibleMove = new int[2]; // holds coordinates of best move

    /**
     * @param root is the Board state that children will be derived from.
     * @param player
     **/
    public void makeChildren(BoardState root, char player){
        int[] startCoordinates; int start; int end; int loopCount;
        int x = 0; int y = 0;
        String[] childState = Arrays.copyOf(root.getBoard(), root.getBoard().length);
        switch(player){
            case 'X':
                startCoordinates = root.getFirstPlayer();
                x = startCoordinates[0];
                y = startCoordinates[1];
            case 'O':
                startCoordinates = root.getSecondPlayer();
                x = startCoordinates[0];
                y = startCoordinates[1];
        }
        // POSSIBLE MOVES IN ROW
        start = y*8; end = ((y+1)*7)+y; loopCount = 0;
        for(int i = start; i<end; i++){
            if(root.getBoard()[i].equals("-")){
                BoardState child = new BoardState(childState, root.getDepth()+1, root.getFirstPlayer(), root.getSecondPlayer(), root);
                child.changeCoordinates(player, x, y+loopCount);
                root.addChild(child);
            }
            loopCount++;
        }
        // POSSIBLE MOVES IN COLUMN
        start = y; end = 48 + y;  loopCount = 0;
        for(int i = start; i<end; i+=8){
            if(root.getBoard()[i].equals("-")){
                BoardState child = new BoardState(childState, root.getDepth()+1, root.getFirstPlayer(), root.getSecondPlayer(), root);
                child.changeCoordinates(player, x+loopCount, y);
                root.addChild(child);
            }
            loopCount++;
        }
        // POSSIBLE MOVES IN POSITIVE DIAGONAL
        start = x+y; end = start*8;  loopCount = 0;
        for(int i = start; i<end; i+=7){
            if(root.getBoard()[i].equals("-")){
                BoardState child = new BoardState(childState, root.getDepth()+1, root.getFirstPlayer(), root.getSecondPlayer(), root);
                child.changeCoordinates(player, x-loopCount, y+loopCount);
                root.addChild(child);
            }
            loopCount++;
        }
        // POSSIBLE MOVES IN NEGATIVE DIAGONAL
        end = x-y; loopCount = 0;
        if(end>=0) start = 63-(end*8);
        else start = 63-end;
        for(int i = start; i>7; i-=9){
            if(root.getBoard()[i].equals("-")){
                BoardState child = new BoardState(childState, root.getDepth()+1, root.getFirstPlayer(), root.getSecondPlayer(), root);
                child.changeCoordinates(player, x-loopCount, y-loopCount);
                root.addChild(child);
            }
            loopCount++;
        }
    }
    
    /**
     * This method prints out the current board states using the labels that are saved (to avoid having to use that
     * space in state space) within the class and the current board state.
     * @param board The current state of the board we want to print to the user.
     **/
    private void printBoard(String[] board){
        for(int i = 0; i < 9; i++){
            System.out.print(columnLabels[i] + " ");
        }
        System.out.println();
        for(int j = 0; j < 8; j++){
            System.out.print(rowLabels[j] + " ");
            rowStart = j*8;
            rowEnd = rowStart + 8;
            for(int k = rowStart; k < rowEnd; k++){
                System.out.print(board[k] + " ");
                if(k+1==rowEnd) System.out.println();
            }
        }
    }
    
    private int minimax(BoardState root, int depth, boolean isMaximizingPlayer, double alpha, double beta){
        int score = maxVal(root, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3);
        BoardState currentNode = root;
        if (depth==3) return score; // if node is a leaf node

        return score;
    }
    
    private int maxVal(BoardState root, double alpha, double beta, int depthLimit){

    }
    
    private int minVal(BoardState root, double alpha, double beta, int depthLimit){
        while(root.getChildren()!=null){

        }
    }
    
    /**
     * This method runs the intro to the game and sets up who is the first player. This method leads to a call
     * to the method that runs the rest of the game.
     **/
    private void startGame(){
        Scanner input = new Scanner(System.in); String first;
        System.out.println("Welcome to the Isolation Game!\nWho goes first? Enter 'C' for computer or 'O' for opponent.");
        first = input.next();
        if(first.equals("C")){
            setComputer('X');
            setOpponent('O');
        }
        else{
            setComputer('O');
            setOpponent('X');
        }
    }
    
    private void game(char firstPlayer, char secondPlayer) {
        BoardState board = new BoardState();
        makeChildren(board, 'A');
        printBoard(board.getBoard());

    }
    
    private void setComputer(char xOrO){
        computer = xOrO;
    }
    
    private void setOpponent(char xOrO){
        opponent = xOrO;
    }
    
    public static void main(String[] args){
        IsolationGame start = new IsolationGame();
        start.startGame();
    }
}
