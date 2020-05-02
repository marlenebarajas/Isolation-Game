import java.awt.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

/***************************************************************
 * file: IsolationGame.java
 * author: Marlene Barajas
 * class: CS 4200.01 - Artificial Intelligence
 *
 * assignment: Project 4: Isolation Game
 * date last modified: 4/28/2020
 ****************************************************************/

public class IsolationGame {
    //BOARD STATE VARS
    private static String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static String[] columnLabels = {" ", "1", "2", "3", "4", "5", "6", "7", "8"};
    private static String space = "       ";
    private int rowStart; private int rowEnd;
    private BoardState currentState; //shows the current game state
    //GAME LOGIC VARS
    private boolean computer = false; //is the computer the first player?
    private long startTime = 0; private long timeLimit = 20; private long elapsedTime; // time limit in seconds
    private ArrayList<String> moves = new ArrayList<>();
    private int depth = 3;
    private int[] playerMove = {-1, -1};

    /**
     * This method prints out the current board states using the labels that are saved (to avoid having to use that
     * space in state space) within the class and the current board state.
     **/
    private void printBoard(){
        String[] board = currentState.getBoard();
        int moveIndex;
        for(int i = 0; i < 9; i++){
            System.out.print(columnLabels[i] + " ");
        }
        System.out.print(moves.get(0));
        System.out.println();
        for(int j = 0; j < 8; j++){
            System.out.print(rowLabels[j] + " ");
            rowStart = j*8;
            rowEnd = rowStart + 8;
            for(int k = rowStart; k < rowEnd; k++) { //traverses through a row
                System.out.print(board[k] + " ");
                if (k + 1 == rowEnd) { //if this is the end of the row
                    moveIndex = j * 2 + 1;
                    if (moves.size() > moveIndex) {
                        System.out.print(space + moves.get(moveIndex));
                        if (moves.size() - 1 > moveIndex) {
                            System.out.print(space + moves.get(moveIndex + 1));
                        }
                    }
                    System.out.println();
                }
            }
        }
        if(moves.size() > 17) printMoves();
    }

    /**
     * Prints the rest of the move list if it extends beyond the length of the board.
     **/
    private void printMoves(){
        String longSpace = "                         ";
        for(int i=17;i<moves.size();i++){
            if(i%2==1) System.out.print(longSpace+moves.get(i)); //first of the current move
            else System.out.print(space+moves.get(i)+"\n");
        }
    }

    /**
     * Runs the alpha-beta pruning algorithm to get the best possible move for the computer player.
     **/
    private int alphaBeta(BoardState root, int depth){
        int negativeInfinity = (int) Double.NEGATIVE_INFINITY;
        int positiveInfinity = (int) Double.POSITIVE_INFINITY;
        return maxValue(root, negativeInfinity, positiveInfinity, depth);
    }

    /**
     * maxValue() makes children out of the possible moves that the computer can make.
     **/
    private int maxValue(BoardState root, int alpha, int beta, int depth){
        char xOrO;
        int score = -1;
        PriorityQueue<Point> frontier;
        if(computer) xOrO = 'X'; //decided whether computer player is X or O
        else xOrO = 'O';

        if(root.getDepth()>=depth){//if reached depth limit
            return root.getScore();
        }
        frontier = root.makeChildren(computer); //COORDINATES OF EVERY POSSIBLE MOVE
        for(int i=0;i<frontier.size();i++){ //now A's children must be queried to get the minimum value of its children
            BoardState child = new BoardState(root, frontier.poll(), xOrO); //creating child with new coordinate position of computer
            score = Math.max(score, minValue(child, alpha, beta, depth)); //getting the minimum valued move that opponent human player can make
            if(score>=beta){
                return score;
            }
            alpha = Math.max(alpha, score);
        }
        return score;
    }

    private int minValue(BoardState root, int alpha, int beta, int depth){
        char xOrO;
        int score = -1;
        PriorityQueue<Point> frontier;
        if(!computer) xOrO = 'X';
        else xOrO = 'O';
        frontier = root.makeChildren(!computer); //looking at the moves that human player might make? and using
                                                //minimum score possible
        if(root.getDepth()>=depth){//if reached depth limit
            return root.getScore();
        }
        for(int i=0;i<frontier.size();i++){
            BoardState child = new BoardState(root, frontier.poll(), xOrO); //creating child with new coordinate position of computer
                                                                            // this call also assigns a score to this move
            score = Math.min(score, maxValue(child, alpha, beta, depth)); //getting the minimum valued move that opponent human player can make
            if(score>=beta){
                return score;
            }
            alpha = Math.min(beta, score);
        }
        return score;
    }

    /**
     * NEEDS TO RESULT IN A CHANGE TO currentState TO BE RETURNED TO GAME
     * This method moves the computer's character around the board with the help of the alpha-beta algorithm deciding
     * the best move to make.
     **/
    private void moveComputer(BoardState currentState){
        PriorityQueue<Point> frontier = currentState.makeChildren(computer);
        int bestScore = alphaBeta(currentState, depth);
        Point bestMove = frontier.peek();
        for(Point move: frontier){
            if(currentState.evaluate(move)==bestScore) bestMove = move;
        }
        int[] coordinates = new int[2];
        coordinates[0] = (int) bestMove.getX();
        coordinates[1] = (int) bestMove.getY();
        if(computer) currentState.changeCoordinates('X', coordinates); //if computer is X player
        else currentState.changeCoordinates('O', coordinates); // or if computer is O player instead
        addMove(coordinates);
    }

    /**
     * This method gets human player's input to move their X/O to another space.
     */
    private void movePlayer(){
        String move; char row; char column; boolean valid; char xOrO;
        Scanner input = new Scanner(System.in);
        System.out.print("What space would you like to move to?");
        if(computer) xOrO = 'X'; //decided whether computer player is X or O
        else xOrO = 'O';
        while(true){
            move = input.next();
            row = move.charAt(0);
            column = move.charAt(1);
            switch(row){
                case 'A':
                case 'F':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'G':
                case 'H':
                    break;
                default:
                    row = '-';
                    break;
            }
            switch(column){
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                    break;
                default:
                    column = '-';
                    break;
            }
            move = String.valueOf(row)+ column;
            valid = checkValidity(move);
            if(valid){
                makeMove();
                addMove(move);
                break;
            }
            System.out.print("This move is not valid, please try again: ");
        }
    }

    /**
     * This method checks whether a space (such as "D3") is empty, and therefore available for human player to move to.
     * @param move String that represents a space on the board
     * @return true if the space is empty("-"), false is otherwise
     */
    private boolean checkValidity(String move){
        int[] oldSpace = new int[2];
        if(computer) oldSpace = currentState.getSecondPlayer();
        else oldSpace = currentState.getFirstPlayer();
        if(move.equals("--")) return false;
        int columnIndex = Character.getNumericValue(move.charAt(1)) - 1;
        char row = move.charAt(0);
        int rowIndex = 0;
        switch(row){
            case 'A':
                break;
            case 'B':
                rowIndex = 1;
                break;
            case 'C':
                rowIndex = 2;
                break;
            case 'D':
                rowIndex = 3;
                break;
            case 'E':
                rowIndex = 4;
                break;
            case 'F':
                rowIndex = 5;
                break;
            case 'G':
                rowIndex = 6;
                break;
            case 'H':
                rowIndex = 7;
                break;
        }
        playerMove[0] = columnIndex;
        playerMove[1] = rowIndex;

        if(oldSpace[0]==playerMove[0]){ //if in the same column
            if(oldSpace[1]<playerMove[1]){ //if new space is under the old space
                int start = oldSpace[1] + 1;
                for(int i=start; i<7;i++){
                    if(!(currentState.getSpace(oldSpace[0], i)).equals("-")) return false;
                    if(playerMove[1]==i) return true; //reached new space without fault
                }
            }
            else{ //if the new space is above the old space
                int start = oldSpace[1] - 1;
                for(int i=start; i>0;i--){
                    if(!(currentState.getSpace(oldSpace[0], i)).equals("-")) return false;
                    if(playerMove[1]==i) return true; //reached new space without fault
                }
            }
        }
        else if(oldSpace[1]==playerMove[1]){ //if in the same row
            if(oldSpace[0]<playerMove[0]){ //if new space to the left of the old space
                int start = oldSpace[0] + 1;
                for(int i=start; i<7;i++){
                    if(!(currentState.getSpace(i, oldSpace[1])).equals("-")) return false;
                    if(playerMove[0]==i) return true; //reached new space without fault
                }
            }
            else{ //if the new space is to the right of the old space
                int start = oldSpace[1] - 1;
                for(int i=start; i>0;i--){
                    if(!(currentState.getSpace(i, oldSpace[1])).equals("-")) return false;
                    if(playerMove[0]==i) return true; //reached new space without fault
                }
            }
        }
        else if(oldSpace[0]<playerMove[0] && oldSpace[1]<playerMove[1]){ //in the same neg diagonal, but to the right
            for(int i=1; i<7;i++){
                if((oldSpace[1]+i)>7 ||(oldSpace[0]+i)>7) return true; //reached the limit without encountering fault
                if(playerMove[0]==i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]+i, oldSpace[1]+i)).equals("-")) return false;
            }
        }
        else if(oldSpace[0]>playerMove[0] && oldSpace[1]>playerMove[1]){ //in the same neg diagonal, but to the left
            for(int i=1; i<7;i++){
                if((oldSpace[1]+i)>7 ||(oldSpace[0]+i)>7) return true; //reached the limit without encountering fault
                if(playerMove[0]==i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]-i, oldSpace[1]-i)).equals("-")) return false;
            }
        }
        else if(oldSpace[0]==(playerMove[0]+1) && oldSpace[1]==(playerMove[1]-1)){ //in the same pos diagonal, but to the right
            for(int i=1; i<7;i++){
                if((oldSpace[1]-i)<0 ||(oldSpace[0]+i)>7) return true; //reached the limit without encountering fault
                if(playerMove[0]==i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]+i, oldSpace[1]-i)).equals("-")) return false;
            }
        }
        else if(oldSpace[0]==(playerMove[0]-1) && oldSpace[1]==(playerMove[1]+1)){ //in the same pos diagonal, but to the left
            for(int i=1; i<7;i++){
                if((oldSpace[1]+i)>7||(oldSpace[0]-i)<0) return true; //reached the limit without encountering fault
                if(playerMove[0]==i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]-i, oldSpace[1]+i)).equals("-")) return false;
            }
        }
        return false; //if none of the previous conditions result in a return value, this is not a valid move
    }

    /**
     * This method changes the position of the human player's X/O in the current state.
     */
    private void makeMove(){
        if(computer) currentState.changeCoordinates('O', playerMove); // computer=true, human is O
        else currentState.changeCoordinates('X', playerMove); // computer=false, human is X
    }

    /**
     * This method translates a move from an index to a String that the user will understand (ex. "E5"). It then adds it
     * to the moves list.
     * @param coordinates where X or O is
     **/
    private void addMove(int[] coordinates){
        String letter = "A"; String number; int coordinate; int result;
        coordinate = coordinates[0];
        switch(coordinate){ //LETTER
            case 0:
                letter = "A";
            case 1:
                letter = "B";
            case 2:
                letter = "C";
            case 3:
                letter = "D";
            case 4:
                letter = "E";
            case 5:
                letter = "F";
            case 6:
                letter = "G";
            case 7:
                letter = "H";
        }
        number = String.valueOf(coordinates[1]); //NUMBER
        result = moves.size();
        if(result%2==1){ //if there is an odd number of moves in move list, this added one will start a new count
            result = result/2 + 1;
            if (result<10){
                moves.add(String.valueOf(0)+result+"."+" "+letter+number);
            }
            else moves.add(result+"."+" "+letter+number);
        }
        else moves.add(letter+number);
    }

    /**
     * This method adds a move to the move list without necessitating a translation from coordinate indices to a String.
     * @param move String with a valid move
     */
    private void addMove(String move){
        int result = moves.size();
        if(result%2==1){ //if there is an odd number of moves in move list, this added one will start a new count
            result = result/2 + 1;
            if (result<10){
                moves.add(String.valueOf(0)+result+"."+" "+move);
            }
            else moves.add(result+"."+" "+move);
        }
        else moves.add(move);
    }

    /**
     * Checks if human/computer player is trapped by '#' spaces
     * @param player X if checking if first player is trapped, O if checking if second player is trapped
     * @return true if player is trapped, false if player is not
     */
    private boolean gameOver(char player){
        int[] coordinates;
        switch(player){
            case 'X':
                coordinates = currentState.getFirstPlayer();
            case 'O':
                coordinates = currentState.getSecondPlayer();
                break;
            default:
                coordinates = new int[2];
        }
        boolean leftWall = (coordinates[0]==0); // true if next to left wall, false if not
        boolean topWall = (coordinates[1]==0); // true if next to top wall, false if not
        boolean rightWall = (coordinates[0]==7); // true if next to right wall, false if not
        boolean bottomWall = (coordinates[1]==7); // true if next to bottom wall, false if not
        String space = currentState.getSpace(coordinates);
        if(!space.equals("-")) return false;
        if(leftWall){ // if we're not at a left wall, we can check these spaces
            coordinates[0]--;
            space = currentState.getSpace(coordinates);
            if(!space.equals("-")) return false;
            if(topWall){
                coordinates[1]--;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) return false;
            }
            if(bottomWall){
                coordinates[1] += 2;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) return false;
            }
        }
        if(rightWall){
            coordinates[0] += 2; // COULD BE AN ERROR HERE?
            coordinates[1]--;
            space = currentState.getSpace(coordinates);
            if(!space.equals("-")) return false;
            if(topWall){
                coordinates[1]--;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) return false;
            }
            if(bottomWall){
                coordinates[1] += 2;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) return false;
            }
        }
        if(topWall){
            coordinates[0]--;
            coordinates[1] -= 2;
            space = currentState.getSpace(coordinates);
            if(!space.equals("-")) return false;
        }
        if(bottomWall){
            coordinates[1] += 2;
            space = currentState.getSpace(coordinates);
            if(!space.equals("-")) return false;
        }
        return false;
    }

    /**
     * This method runs the intro to the game and sets up who is the first player. This method leads to a call
     * to the method that runs the rest of the game.
     **/
    private void startGame(){
        Scanner input = new Scanner(System.in); String first; String second;
        System.out.println("Welcome to the Isolation Game!\nWho goes first? Enter 'C' for computer or 'O' for opponent.");

        while(true) {
            first = input.next();
            if (first.equals("C") || first.equals("c")) {
                first = "Computer";
                computer = true;
                second = "Opponent";
                moves.add("      Computer vs. Opponent"); //always needed at index 0
                break;
            } else if (first.equals("O") || first.equals("o")) {
                first = "Opponent";
                this.computer = false;
                second = "Computer";
                moves.add("      Opponent vs. Computer"); //always needed at index 0
                break;
            }
            System.out.println("Please enter a correct value.\nWho goes first? Enter 'C' for computer or 'O' for opponent.");
        }
        System.out.println(first+" will be X and "+second+" will be O.");
        game();
    }

    /**
     *
     **/
    private void game() {
        currentState = new BoardState();
        printBoard();
        while(true){
            if(computer){
                moveComputer(currentState); //method uses new frontier to change currentState, and computer moves first
                printBoard();
                if(gameOver('X')) break; //CHECKS IF COMPUTER WON
                movePlayer(); //human moves second
                printBoard();
                if(gameOver('O')) break; //CHECKS IF HUMAN WON
            }
            else{
                movePlayer(); //human moves first
                printBoard();
                if(gameOver('X')){ //CHECKS IF HUMAN WON
                    System.out.println("Game over!");
                    printBoard();
                    break;
                }
                moveComputer(currentState); //computer moves second
                printBoard();
                if(gameOver('O')){ //CHECKS IF COMPUTER WON
                    System.out.println("Game over!");
                    printBoard();
                    break;
                }
            }
        }
    }

    public static void main(String[] args){
        IsolationGame start = new IsolationGame();
        start.startGame();
    }
}
