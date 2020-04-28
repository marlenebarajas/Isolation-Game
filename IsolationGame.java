import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/***************************************************************
 * file: IsolationGame.java
 * author: Marlene Barajas
 * class: CS 4200.01 - Artificial Intelligence
 *
 * assignment: Project 4: Isolation Game
 * date last modified: 4/27/2020
 *
 * purpose:
 *
 ****************************************************************/
public class IsolationGame {
    //BOARD STATE VARS
    private static String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static String[] columnLabels = {" ", "1", "2", "3", "4", "5", "6", "7", "8"};
    private static String space = "       ";
    private int rowStart; private int rowEnd;
    //GAME LOGIC VARS
    private boolean computer;
    private long startTime = 0; private long timeLimit = 20; private long elapsedTime; // time limit in seconds
    private ArrayList<BoardState> frontier; // holds the successors of current move
    private ArrayList<String> moves = new ArrayList<>(); //holds all possible moves
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
        frontier = currentNode.getChildren();
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
     * This method moves the computer's character around the board with the help of the alpha-beta algorithm deciding
     * the best move to make.
     **/
    private void moveComputer(){
        BoardState move;
        startTime = System.nanoTime();
        frontier.clear();
        int score = 0; // = alphaBeta(root, depth);
        //alphaBeta changes frontier to be computer's children
        for(BoardState child : frontier){
            if(child.getScore() == score){
                move = child; //this is the best move (or the same score as one of them at least)
                break;
            }
        }
        //add best move to moves list, which needs a way to calculate what the LetterNumber descriptor off
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
        BoardState board = new BoardState();
        printBoard(board.getBoard());
        moves.add("      Computer vs. Opponent"); //always needed at index 0
        if(computer){ //if AI is first player
            //run alpha-beta for AI to move X
            //THEN, after, ask for user input to move O
        }
        else{
            //ask for user input to move X
            //run alpha-beta for AI to move O
        }
    }

    public static void main(String[] args){
        IsolationGame start = new IsolationGame();
        start.startGame();
    }
}
