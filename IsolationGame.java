import java.util.ArrayList;
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
    private boolean computer; //is the computer the first player?
    private long startTime = 0; private long timeLimit = 20; private long elapsedTime; // time limit in seconds
    private ArrayList<BoardState> frontier; // holds the successors of current move
    private ArrayList<String> moves = new ArrayList<>(); //holds all possible moves
    int round = 0;
    int columnIndex; int rowIndex;

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
    private double alphaBeta(BoardState root, int depth, boolean isMaximizingPlayer, double alpha, double beta){
        double maxEvaluation; double minEvaluation; int evaluation;
        BoardState currentNode = root;
        currentNode.makeChildren(isMaximizingPlayer); //MIGHT BE INCORRECT USE
        if (depth==0) return root.evaluate(); // if node is root, algorithm is over

        if(isMaximizingPlayer){
            maxEvaluation = Double.NEGATIVE_INFINITY;
            for(BoardState child : frontier){
                evaluation = (int) alphaBeta(child, depth-1, false, alpha, beta);
                child.setScore(evaluation); //this algorithm is in charge of setting scores of everything in frontier
                maxEvaluation = Math.max(maxEvaluation, evaluation);
                alpha = Math.max(alpha, evaluation);
                if(beta <= alpha) break;
            } return maxEvaluation;
        }
        else{
            minEvaluation = Double.POSITIVE_INFINITY;
            for(BoardState child : frontier){
                evaluation = (int) alphaBeta(child, depth-1, true, alpha, beta);
                child.setScore(evaluation); //this algorithm is in charge of setting scores of everything in frontier
                minEvaluation = Math.min(minEvaluation, evaluation);
                beta = Math.min(minEvaluation, evaluation);
                if(beta<=alpha) break;
            } return minEvaluation;
        }
    }

    /**
     * NEEDS TO RESULT IN A CHANGE TO currentState TO BE RETURNED TO GAME
     * This method moves the computer's character around the board with the help of the alpha-beta algorithm deciding
     * the best move to make.
     **/
    private void moveComputer(){
        int[] coordinates;
        BoardState move = currentState;
        startTime = System.nanoTime();
        int score = 0; // = alphaBeta(root, depth);
        //alphaBeta changes frontier to be computer's children
        for(BoardState child : frontier){
            if(child.getScore() == score){
                move = child; //this is the best move (or the same score as one of them at least)
                break;
            }
        }
        //add best move to moves list, which needs a method to calculate what the LetterNumber descriptor of move is
        if(computer) coordinates = move.getFirstPlayer(); //if computer is X player
        else coordinates = move.getSecondPlayer(); // or if computer is O player instead
        addMove(coordinates);
    }

    /**
     * This method gets human player's input to move their X/O to another space.
     * @param isFirst true if human is X
     *                false if human is O
     */
    private void movePlayer(boolean isFirst){
        String move; char row; char column; boolean valid;
        Scanner input = new Scanner(System.in);
        System.out.print("What space would you like to move to?");
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
        if(move.equals("--")) return false;
        columnIndex = Character.getNumericValue(move.charAt(1));
        char row = move.charAt(0);
        rowIndex = 0;
        switch(row){
            case 'A':
                break;
            case 'B':
                rowIndex = 1;
            case 'C':
                rowIndex = 2;
            case 'D':
                rowIndex = 3;
            case 'E':
                rowIndex = 4;
            case 'F':
                rowIndex = 5;
            case 'G':
                rowIndex = 6;
            case 'H':
                rowIndex = 7;
        }
        String space = currentState.getSpace(columnIndex, rowIndex);
        return space.equals("-");
    }

    /**
     * This method changes the position of the human player's X/O in the current state.
     */
    private void makeMove(){
        if(computer) currentState.changeCoordinates('O', columnIndex, rowIndex); // computer=true, human is O
        else currentState.changeCoordinates('X', columnIndex, rowIndex); // computer=false, human is X
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
            result = result/2;
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
            result = result/2;
            if (result<10){
                moves.add(String.valueOf(0)+result+"."+" "+move);
            }
            else moves.add(result+"."+" "+move);
        }
        else moves.add(move);
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
                first = "Computer"; computer = true;
                second = "Opponent";
                break;
            } else if (first.equals("O") || first.equals("o")) {
                first = "Opponent"; computer = false;
                second = "Computer";
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
        //VARIABLE SET UP
        currentState = new BoardState();
        boolean computerTurn = computer;
        //EXTRA SET UP
        moves.add("      Computer vs. Opponent"); //always needed at index 0
        printBoard();

        while(true){
            if(computerTurn){
                frontier = currentState.makeChildren(computer); //THIS CHANGES THE FRONTIER FOR EVERY METHOD
                moveComputer(); //method uses new frontier to change currentState, and computer moves first
                printBoard();
                movePlayer(false); //human moves second
                printBoard();
                round++;
            }
            else{
                movePlayer(true); //human moves first
                printBoard();
                frontier = currentState.makeChildren(computer);
                moveComputer(); //computer moves second
                printBoard();
                round++;
            }
        }
    }

    public static void main(String[] args){
        IsolationGame start = new IsolationGame();
        start.startGame();
    }
}
