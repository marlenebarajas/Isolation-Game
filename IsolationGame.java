import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;

/***************************************************************
 * file: IsolationGame.java
 * author: Marlene Barajas
 * class: CS 4200.01 - Artificial Intelligence
 *
 * assignment: Project 4: Isolation Game
 * date last modified: 5/02/2020
 ****************************************************************/

public class IsolationGame {
    //BOARD STATE VARS
    private static String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static String[] columnLabels = {" ", "1", "2", "3", "4", "5", "6", "7", "8"};
    private static String space = "       ";
    //GAME LOGIC VARS
    private boolean computer = false; //is the computer the first player?
    private ArrayList<String> moves = new ArrayList<>();
    private int depth = 4;
    private int[] playerMove = {-1, -1};

    /**
     * This method prints out the current board states using the labels that are saved (to avoid having to use that
     * space in state space) within the class and the current board state.
     * @param currentState the current game state
     **/
    private void printBoard(BoardState currentState){
        String[] board = currentState.getBoard();
        int moveIndex;
        for(int i = 0; i < 9; i++){
            System.out.print(columnLabels[i] + " ");
        }
        System.out.print(moves.get(0));
        System.out.println();
        for(int j = 0; j < 8; j++){
            System.out.print(rowLabels[j] + " ");
            int rowStart = j * 8;
            int rowEnd = rowStart + 8;
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
     * @param root BoardState that is always currentState
     * @param depth the depth limit for this alpha-beta pruning algorithm
     **/
    private int alphaBeta(BoardState root, int depth){
        int negativeInfinity = (int) Double.NEGATIVE_INFINITY;
        int positiveInfinity = (int) Double.POSITIVE_INFINITY;
        return maxValue(root, negativeInfinity, positiveInfinity, depth);
    }

    /**
     * maxValue() makes children out of the possible moves that the computer can make and is the maximizing portion of
     * the alpha-beta pruning algorithm.
     * @param root BoardState of the current child that is maximizing in alpha-beta pruning
     * @param alpha maximum value in the algorithm so far
     * @param beta minimum value in the algorithm so far
     * @param depth the depth limit for this alpha-beta pruning algorithm
     * @return int for the score/maximum value at this point in alpha-beta pruning algorithm
     **/
    private int maxValue(BoardState root, int alpha, int beta, int depth){
        char xOrO;
        Point move;
        int score = -1;
        PriorityQueue<Point> frontier;
        if(computer) xOrO = 'X';
        else xOrO = 'O';

        if(root.getDepth()>=depth){//if reached depth limit
            return root.getScore();
        }
        frontier = root.makeChildren(computer); //COORDINATES OF EVERY POSSIBLE MOVE
        for(int i=0;i<frontier.size();i++){ //now A's children must be queried to get the minimum value of its children
            move = frontier.poll();
            BoardState child = new BoardState(root, move, xOrO); //creating child with new coordinate position of computer
            child.setFirstPlayer(root.getFirstPlayer());
            child.setSecondPlayer(root.getSecondPlayer());
            child.changeCoordinates(xOrO, (int) move.getX(), (int) move.getY());
            child.makeScore(xOrO);
            score = Math.max(score, minValue(child, alpha, beta, depth)); //getting the minimum valued move that opponent human player can make
            if(score>=beta){
                return score;
            }
            alpha = Math.max(alpha, score);
        }
        return score;
    }

    /**
     * minValue() makes children out of the possible moves that the computer can make and is the minimizing portion of
     * the alpha-beta pruning algorithm.
     * @param root BoardState of the current child that is maximizing in alpha-beta pruning
     * @param alpha maximum value in the algorithm so far
     * @param beta minimum value in the algorithm so far
     * @param depth the depth limit for this alpha-beta pruning algorithm
     * @return int for the score/minimum value at this point in alpha-beta pruning algorithm
     **/
    private int minValue(BoardState root, int alpha, int beta, int depth){
        char xOrO;
        Point move;
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
            move = frontier.poll();
            BoardState child = new BoardState(root, move, xOrO); //creating child with new coordinate position of computer
            child.setFirstPlayer(root.getFirstPlayer());
            child.setSecondPlayer(root.getSecondPlayer());
            child.changeCoordinates(xOrO, (int) move.getX(), (int) move.getY());
            child.makeScore(xOrO);
            score = Math.min(score, maxValue(child, alpha, beta, depth)); //getting the minimum valued move that opponent human player can make
            if(score>=beta){
                return score;
            }
            alpha = Math.min(beta, score);
        }
        return score;
    }

    /**
     * This method moves the computer's character around the board with the help of the alpha-beta algorithm deciding
     * the best move to make.
     * @param currentState the current game state
     **/
    private void moveComputer(BoardState currentState){
        Point move;
        PriorityQueue<Point> frontier = currentState.makeChildren(computer);
        int bestScore = alphaBeta(currentState, depth);

        Point bestMove = frontier.peek();
        for(int i=0;i<frontier.size();i++){
            move = frontier.poll();
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
     * @param currentState the current game state
     */
    private void movePlayer(BoardState currentState){
        String move;
        char row;
        char column;
        boolean valid;
        char xOrO;
        Scanner input = new Scanner(System.in);
        System.out.print("What space would you like to move to? (ex. B2)");
        while(true){
            move = input.next();
            if(move.length()==2){
                row = move.charAt(0);
                column = move.charAt(1);
                switch(row){
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'h':
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
                valid = checkValidity(currentState, move);
                if(valid){
                    makeMove(currentState);
                    addMove(move);
                    break;
                }
            }
            System.out.print("This move is not valid, please try again: ");
        }
    }

    /**
     * This method checks whether a space (such as "D3") is empty, and therefore available for human player to move to.
     * @param currentState the current game state
     * @param move String that represents a space on the board
     * @return true if the space is empty("-"), false is otherwise
     */
    private boolean checkValidity(BoardState currentState, String move){
        int[] oldSpace;
        if(computer) oldSpace = currentState.getSecondPlayer();
        else oldSpace = currentState.getFirstPlayer();
        if(move.equals("--")) return false;
        int columnIndex = Character.getNumericValue(move.charAt(1)) - 1;
        char row = move.charAt(0);
        int rowIndex = 0;
        switch(row){
            case 'A':
            case 'a':
                break;
            case 'B':
            case 'b':
                rowIndex = 1;
                break;
            case 'C':
            case 'c':
                rowIndex = 2;
                break;
            case 'D':
            case 'd':
                rowIndex = 3;
                break;
            case 'E':
            case 'e':
                rowIndex = 4;
                break;
            case 'F':
            case 'f':
                rowIndex = 5;
                break;
            case 'G':
            case 'g':
                rowIndex = 6;
                break;
            case 'H':
            case 'h':
                rowIndex = 7;
                break;
        }
        playerMove[0] = columnIndex;
        playerMove[1] = rowIndex;

        if(oldSpace[0]==playerMove[0]){ //if in the same column
            if(oldSpace[1]<playerMove[1]){ //if new space is over the old space
                for(int i=1; i<=7;i++){
                    if(oldSpace[1]+i>7) return false; //reached the limit without encountering new space
                    if(playerMove[1]==oldSpace[1]+i) return true; //reached new space without fault
                    if(!(currentState.getSpace(oldSpace[0], oldSpace[1]+i)).equals("-")) return false;
                }
            }
            else{ //if the new space is under the old space
                for(int i=1; i<=7;i++){
                    if(oldSpace[1]-i>7) return false; //reached the limit without encountering new space
                    if(playerMove[1]==oldSpace[1]-i) return true; //reached new space without fault
                    if(!(currentState.getSpace(oldSpace[0], oldSpace[1]-i)).equals("-")) return false;
                }
            }
        }
        else if(oldSpace[1]==playerMove[1]){ //if in the same row
            if(oldSpace[0]<playerMove[0]){ //if new space to the left of the old space
                for(int i=1; i<=7;i++){
                    if(oldSpace[0]+i>7) return false; //reached the limit without encountering new space
                    if(playerMove[0]==oldSpace[0]+i) return true; //reached new space without fault
                    if(!(currentState.getSpace(oldSpace[0]+i, oldSpace[1])).equals("-")) return false;
                }
            }
            else{ //if the new space is to the right of the old space
                for(int i=1; i<=7;i++){
                    if(oldSpace[0]-i<0) return false; //reached the limit without encountering new space
                    if(playerMove[0]==oldSpace[0]-i) return true; //reached new space without fault
                    if(!(currentState.getSpace(oldSpace[0]-i, oldSpace[1])).equals("-")) return false;
                }
            }
        }
        else if(oldSpace[0]<playerMove[0] && oldSpace[1]<playerMove[1]){ //in the same neg diagonal, but to the right
            for(int i=1; i<=7;i++){
                if((oldSpace[1]+i)>7 ||(oldSpace[0]+i)>7) return false; //reached the limit without encountering new space
                if(playerMove[0]==oldSpace[0]+i && playerMove[1]==oldSpace[1]+i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]+i, oldSpace[1]+i)).equals("-")) return false;
            }
        }
        else if(oldSpace[0]>playerMove[0] && oldSpace[1]>playerMove[1]){ //in the same neg diagonal, but to the left
            for(int i=1; i<=7;i++){
                if((oldSpace[1]-i)<0 ||(oldSpace[0]-i)<0) return false; //reached the limit without encountering new space
                if(playerMove[0]==oldSpace[0]-i && playerMove[1]==oldSpace[1]-i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]-i, oldSpace[1]-i)).equals("-")) return false;
            }
        }
        else if(oldSpace[0]<playerMove[0] && oldSpace[1]>playerMove[1]){ //in the same pos diagonal, but to the right
            for(int i=1; i<=7;i++){
                if((oldSpace[1]-i)<0 ||(oldSpace[0]+i)>7) return false; //reached the limit without encountering new space
                if(playerMove[0]==oldSpace[0]+i && playerMove[1]==oldSpace[1]-i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]+i, oldSpace[1]-i)).equals("-")) return false;
            }
        }
        else if(oldSpace[0]>playerMove[0] && oldSpace[1]<playerMove[1]){ //in the same pos diagonal, but to the left
            for(int i=1; i<=7;i++){
                if((oldSpace[1]+i)>7||(oldSpace[0]-i)<0) return false; //reached the limit without encountering new space
                if(playerMove[0]==oldSpace[0]-i && playerMove[1]==oldSpace[1]+i) return true; //reached new space without fault
                if(!(currentState.getSpace(oldSpace[0]-i, oldSpace[1]+i)).equals("-")) return false;
            }
        }
        return false; //if none of the previous conditions result in a return value, this is not a valid move
    }

    /**
     * This method changes the position of the human player's X/O in the current state.
     */
    private void makeMove(BoardState currentState){
        if(computer) currentState.changeCoordinates('O', playerMove); // computer=true, human is O
        else currentState.changeCoordinates('X', playerMove); // computer=false, human is X
    }

    /**
     * This method translates a move from an index to a String that the user will understand (ex. "E5"). It then adds it
     * to the moves list.
     * @param coordinates where X or O is
     **/
    private void addMove(int[] coordinates){
        String letter = "A";
        String number;
        int coordinate;
        int result;
        coordinate = coordinates[1];
        switch(coordinate){ //LETTER
            case 0:
                break;
            case 1:
                letter = "B";
                break;
            case 2:
                letter = "C";
                 break;
            case 3:
                letter = "D";
                break;
            case 4:
                letter = "E";
                break;
            case 5:
                letter = "F";
                break;
            case 6:
                letter = "G";
                break;
            case 7:
                letter = "H";
                break;
        }
        number = String.valueOf(coordinates[0] + 1) ; //NUMBER
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
     * @param currentState the current game state
     * @param player X if checking if first player is trapped, O if checking if second player is trapped
     * @return true if player is trapped, false if player is not
     */
    private boolean gameOver(BoardState currentState, char player){
        String space;
        int[] coordinates;
        int totalHashes = 0;
        int actualSpaces = 0;
        switch(player){
            case 'X':
                coordinates = Arrays.copyOf(currentState.getFirstPlayer(), 2);
                break;
            case 'O':
                coordinates = Arrays.copyOf(currentState.getSecondPlayer(), 2);
                break;
            default:
                coordinates = new int[2];
        }
        boolean leftWall = (coordinates[0]==0); // true if next to left wall, false if not
        boolean topWall = (coordinates[1]==0); // true if next to top wall, false if not
        boolean rightWall = (coordinates[0]==7); // true if next to right wall, false if not
        boolean bottomWall = (coordinates[1]==7); // true if next to bottom wall, false if not
        int[] resetCoordinates = Arrays.copyOf(coordinates, 2);
        if(!leftWall){ // if we're not at a left wall, we can check these spaces
            coordinates[0]--;
            space = currentState.getSpace(coordinates);
            actualSpaces++;
            if(!space.equals("-")) totalHashes++;
            coordinates = Arrays.copyOf(resetCoordinates, 2);
            if(!topWall){
                actualSpaces++;
                coordinates[0]--;
                coordinates[1]--;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) totalHashes++;
            }
            coordinates = Arrays.copyOf(resetCoordinates, 2);
            if(!bottomWall){
                actualSpaces++;
                coordinates[0]--;
                coordinates[1]++;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) totalHashes++;
            }
        }
        coordinates = Arrays.copyOf(resetCoordinates, 2);
        if(!rightWall){ // if we're not at a right wall, we can check these spaces
            actualSpaces++;
            coordinates[0]++;
            space = currentState.getSpace(coordinates);
            if(!space.equals("-")) totalHashes++;
            coordinates = Arrays.copyOf(resetCoordinates, 2);
            if(!topWall){
                actualSpaces++;
                coordinates[0]++;
                coordinates[1]--;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) totalHashes++;
            }
            coordinates = Arrays.copyOf(resetCoordinates, 2);
            if(!bottomWall){
                actualSpaces++;
                coordinates[0]++;
                coordinates[1]++;
                space = currentState.getSpace(coordinates);
                if(!space.equals("-")) totalHashes++;
            }
        }
        coordinates = Arrays.copyOf(resetCoordinates, 2);
        if(!topWall){ // if we're not at a top wall, we can check these spaces
            actualSpaces++;
            coordinates[1]--;
            space = currentState.getSpace(coordinates);
            if(!space.equals("-")) totalHashes++;
        }
        coordinates = Arrays.copyOf(resetCoordinates, 2);
        if(!bottomWall){ // if we're not at a bottom wall, we can check these spaces
            actualSpaces++;
            coordinates[1]++;
            space = currentState.getSpace(coordinates);
            if(!space.equals("-")) totalHashes++;
        }
        return actualSpaces == totalHashes;
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
                this.computer = true;
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
     * Runs the main portion of the game with the help of many methods, revolving around currentState.
     **/
    private void game() {
        BoardState currentState = new BoardState();
        printBoard(currentState);
        while(true){
            if(computer){
                moveComputer(currentState); //method uses new frontier to change currentState, and computer moves first
                printBoard(currentState);
                if(gameOver(currentState, 'O')){ //CHECKS IF COMPUTER WON
                    System.out.println("\nGAME OVER: Computer won, better luck next time!");
                    printBoard(currentState);
                    break;
                }
                movePlayer(currentState); //human moves second
                printBoard(currentState);
                if(gameOver(currentState, 'X')){ //CHECKS IF HUMAN WON
                    System.out.println("\nGAME OVER: You won, congratulations!");
                    printBoard(currentState);
                    break;
                }
            }
            else{
                movePlayer(currentState); //human moves first
                printBoard(currentState);
                if(gameOver(currentState, 'O')){ //CHECKS IF HUMAN WON
                    System.out.println("\nGAME OVER: You won, congratulations!");;
                    printBoard(currentState);
                    break;
                }
                moveComputer(currentState); //computer moves second
                printBoard(currentState);
                if(gameOver(currentState, 'X')){ //CHECKS IF COMPUTER WON
                    System.out.println("\nGAME OVER: Computer won, better luck next time!");
                    printBoard(currentState);
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
