

/**
 * @author Rohan
 * 
 * A tic-tac-toe classic CPU vs. Human game.
 * Four levels: Easy, Medium, Hard, and Wizard.
 * 
 * Easy chooses randomly.
 * Medium is the average human.
 * Hard is a little trickier and uses one type of trap.
 * Wizard is undefeatable and uses psychological tactics
 * to make strategies hard to figure. It uses all three types of traps.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

/**
 * Utility is an all-static uninstantiable class.
 * It contains static methods used throughout the application
 * dealing with arrays.
 */
class Utility {
    /** Private constructor prevents instantiablity. */
    private Utility() {}
    /** Is pair in array? */
    static boolean isIn(Pair[] array, Pair pair) {
        for (Pair p: array) if (p == pair) return true;
        return false;
    }
    /**
     * @return the set intersection of two arrays.
     * @param array1 is a Pair[]
     * @param array2 is a Pair[]
     */
    static Pair[] intersection(Pair[] array1, Pair[] array2) {
        // Create an ArrayList
        ArrayList<Pair> arrax = new ArrayList<>();
        for (Pair p : array1) // For every Pair in the array
            if (isIn(array2, p)) // if its in the second array
                arrax.add(p); // Add it to the ArrayList
        Pair[] newArray = new Pair[1];
        arrax.toArray(newArray);
        return newArray;           
    }
    /**
     *
     * @return the set intersection of two arrays.
     * @param array1 is a Pair[]
     * @param array2 is an ArrayList<Pair>
     */
    static Pair[] intersection(Pair[] array1, ArrayList<Pair> array2) {
        ArrayList<Pair> arrax = new ArrayList<>(); // Create an ArrayList
        for (Pair p : array1) // For every Pair in the array
            if (array2.contains(p)) // if its in the second array
                arrax.add(p); // Add it to the ArrayList
        Pair[] newArray = new Pair[1];
        arrax.toArray(newArray);
        return newArray;           
    }
}
/**
 * A class representing a pair of coordinates.
 * Coordinates used for tic-tac-toe positions.
 */
class Pair {
    // Static fields:
    /** 
     * All the pairs, pre-instantiated. 
     * References are made of Pairs contained in the array of arrays. 
     */
    private static final Pair[][] gridPairs = {
        { new Pair(-1, -1), new Pair(-1, 0), new Pair(-1, 1) },
        { new Pair(0, -1), new Pair(0, 0), new Pair(0, 1) },
        { new Pair(1, -1), new Pair(1, 0), new Pair(1, 1) }
    }; // The pairs, preinstantiated, of which references are of
    // Fields:
    /** x-coordinate. */
    private final int x;
    /** y-coordinate. */
    private final int y;
    /** Constructor for Pair, private to prevent outside instantiation. */
    private Pair(int x, int y) { this.x = x;   this.y = y; }
    // Accessors:
    /** Accessor for x. */
    int getX() { return x; }
    /** Accessor for y. */
    int getY() { return y; }
    // Mutation returners:
    /** Return opposite pair. */
    Pair opposite() { return getPair(-x, -y); }
    /** Reflect pair over y-axis. */
    Pair revertX() { return getPair(-x, y); }
    /** Reflect pair over x-axis. */
    Pair revertY() { return getPair(x, -y); }
//    /** Shift pair by other pair. */
//    Pair shift(Pair other) { return getPair(x+other.x, y+other.y); }
//    /** Shift by coordinates of other pair. */
//    Pair shift(int x, int y) { return getPair(this.x+x, this.y+y); }
    /** 
     * Static Pair-reference-getter method.
     * Get pair from a certain position.
     * @param x the x-coordinate of the pair.
     * @param y the y-coordinate of the pair.
     */
    static Pair getPair(int x, int y) { return gridPairs[x+1][y+1]; }
}
/** This enumeration represents the different groups a space can belong to. */
enum SpaceGroup { CENTER, SIDE, CORNER }
/** 
 * A space on the tic-tac-toe game board. 
 * A Space not only has a position (Pair), it has a representation
 * which is a character. This is always 'X', 'O', or ' ' (blank).
 * That is the only field that isn't final.
 */
class Space {
    // Fields:
    /** Location of space. */
    private final Pair loc;
    /** Character for piece. */
    private char repr = ' ';
    /** Group piece is in. */
    private final SpaceGroup spaceGroup;
    /** Constructor given two coordinates, setting fields. */
    Space(int x, int y) {
        this.loc = Pair.getPair(x,y); // Set Pair loc field (location)
        if (loc == Board.center) // If its the center
            spaceGroup = SpaceGroup.CENTER; // Set its type
        else if (Utility.isIn(Board.corners, loc))
            // if the location is at the corner
            spaceGroup = SpaceGroup.CORNER; // set type to corner
        else // Or else, set the type to 
            spaceGroup = SpaceGroup.SIDE; // side
    }
    // Accessors:
    /** @return location of Space. */
    Pair getLoc() { return loc; }
    /** @return character at Space. */
    char getRepr() { return repr; }
    /** @return group space belongs to. */
    SpaceGroup getGroup() { return spaceGroup; }
    // Mutator:
    /** Set character at space. */
    void setRepr(char repl) { repr = repl; }
}
/** An enumeration describing the levels of the game. */
enum Level { EASY, MEDIUM, HARD, WIZARD }
/**
 * A class representing the board, which must be instantiated in order
 * to play the game.
 * Most game methods go here.
 * 
 * Class designed to be independent of implementation of game.
 * Computer is always 'X', user is always 'O'.
 */
class Board {
    // Static fields:
    /** Random object used to determine a random choice of move. */
    private static final Random rand = new Random();
    /** The array of the pairs which are corners. */
    static final Pair[] corners = {
        Pair.getPair(1,1), Pair.getPair(-1,1), 
        Pair.getPair(1,-1), Pair.getPair(-1,-1)
    };
    /** The array of the pairs which are sides. */
    static final Pair[] sides = {
        Pair.getPair(0,1), Pair.getPair(0,-1), 
        Pair.getPair(1,0), Pair.getPair(-1,0)
    };
    /** A named constant, the center. */
    static final Pair center = Pair.getPair(0,0);
    /** An array of all the triplets of pairs which are a win. */
    private static final Pair[][] winGroups = {
        { Pair.getPair(-1,1), Pair.getPair(0,1), Pair.getPair(1,1) },
        { Pair.getPair(-1,0), Pair.getPair(0,0), Pair.getPair(1,0) },
        { Pair.getPair(-1,-1), Pair.getPair(0,-1), Pair.getPair(1,-1) },
        { Pair.getPair(-1,-1), Pair.getPair(-1,0), Pair.getPair(-1,1) },
        { Pair.getPair(0,-1), Pair.getPair(0,0), Pair.getPair(0,1) },
        { Pair.getPair(1,-1), Pair.getPair(1,0), Pair.getPair(1,1) },
        { Pair.getPair(-1,-1), Pair.getPair(0,0), Pair.getPair(1,1) },
        { Pair.getPair(-1,1), Pair.getPair(0,0), Pair.getPair(1,-1) },
    };
    // Fields:
    /** The board itself, made of Space objects rather than Pairs. */
    private final Space[][] board = {
        { new Space(-1, -1), new Space(-1, 0), new Space(-1, 1) },
        { new Space(0, -1), new Space(0, 0), new Space(0, 1) },
        { new Space(1, -1), new Space(1, 0), new Space(1, 1)},
    };
    /** A move sequence of both the user's and computer's moves. 
     * They are array lists because it is dynamic. */
    private ArrayList<Space> UserSeq = new ArrayList<>();
    private ArrayList<Space> CompSeq = new ArrayList<>();
    /** The unfixed ArrayList of all the open spaces, which are Pairs. 
     This is an ArrayList because it is dynamic. */
    private ArrayList<Pair> openSpaces = new ArrayList<>();
    /** The number of turns so far. */
    private int countOfTurns = 1;
    // // countOfTurns starts at 1
    /** Tells whether it is the computer's turn. */
    private boolean isCompTurn;
    /** Tells whether the computer started. */
    private final boolean isCompStarted;
    /** A level object, which tells which level is being played. */
    private Level level;
    /** Constructor given level and starter. */
    Board(boolean isCompStarted, Level level) {
        this.isCompTurn = isCompStarted;
        this.isCompStarted = isCompStarted;
        this.level = level;
        Pair[] arrax = {
            Pair.getPair(-1,-1), Pair.getPair(-1,0), Pair.getPair(-1,1),
            Pair.getPair(0,-1), Pair.getPair(0,0), Pair.getPair(0,1),
            Pair.getPair(1,-1), Pair.getPair(1,0), Pair.getPair(1,1)
        };
        openSpaces.addAll(Arrays.asList(arrax));
    }
    // Accessors:
    /** @return level of board. */
    Level getLevel() { return level; }
    /** @return if the computer started. */
    boolean getIsCompStarted() { return isCompStarted; }
    // Methods:
    /** Printable version of board. */
    @Override public String toString() {
        return (
          "\n\t\t "+refer(-1,1)+" | "+refer(0,1)+" | "+refer(1,1)
          +"\n\t\t-----------"
          +"\n\t\t "+refer(-1,0)+" | "+refer(0,0)+" | "+refer(1,0)
          +"\n\t\t-----------"
          +"\n\t\t "+refer(-1,-1)+" | "+refer(0,-1)+" | "+refer(1,-1)
        );
    }
    /** Get an 'X' or 'O' character given coordinate pair. */
    private char refer(Pair pair) {
        return getSpace(pair).getRepr();
    }
    /** Get an 'X' and 'O' from coordinates. */
    private char refer(int x, int y) {
        return board[x+1][y+1].getRepr();
    }
    /** Activate procedures when a move is made. */
    void makeMove(Pair moveLoc, char XO) {
        ///* 
        if (isCompTurn)
            UserSeq.add(getSpace(moveLoc));
        else
            CompSeq.add(getSpace(moveLoc));
        //*/
        isCompTurn = !isCompTurn;
        getSpace(moveLoc).setRepr(XO);
        openSpaces.remove(moveLoc);
        countOfTurns++;
    }
    /** Get a space reference from a pair. */
    Space getSpace(Pair pair) {
        return board[pair.getX()+1][pair.getY()+1];
    }
    /** 
     * Check if anyone is one step from winning.
     * @return The move to block or to win. 
     * @return null is returned if there is nothing to block.
     */
    private Pair almost() {
        // First, look at CPU's winnability
        for (Pair[] group : winGroups) {
            if (refer(group[0])==refer(group[1]) &&
                refer(group[0])=='X' && 
                openSpaces.contains(group[2]))
                    // If 0 and 1 are filled
                return group[2]; // make sure to block 2
            if (refer(group[0])==refer(group[2]) &&
                refer(group[0])=='X' && 
                openSpaces.contains(group[1]))
                // If 0 and 2 are filled
                return group[1]; // make sure to block 1 
            if (refer(group[1])==refer(group[2]) &&
                refer(group[1])=='X' && 
                openSpaces.contains(group[0]))
                // If 1 and 2 are filled
                return group[0]; // make sure to block 0
        }
        // Then look at user's winnability
        for (Pair[] group : winGroups) {
            if (refer(group[0])==refer(group[1]) &&
                refer(group[0])=='O' && 
                openSpaces.contains(group[2]))
                    // If 0 and 1 are filled
               return group[2]; // make sure to block 2
            if (refer(group[0])==refer(group[2]) &&
                refer(group[0])=='O' && 
                openSpaces.contains(group[1]))
                // If 0 and 2 are filled
                return group[1]; // make sure to block 1 
            if (refer(group[1])==refer(group[2]) &&
                refer(group[1])=='O' && 
                openSpaces.contains(group[0]))
                // If 1 and 2 are filled
                return group[0]; // make sure to block 0
        }
        return null; // Otherwise, return null (don't try to block anything)
    }
    /** 
     * Checks if either comp or user has two in a row.
     * If either does, @return Pair that move.
     * Otherwise, it just returns the inputted pair.
     * 
     * Essentially, its like a safety check for the inputted move.
     */
    private Pair safetyCheck(Pair pair) {
        // If someone has almost won,
        if (almost() != null) return almost(); // just return the block
        return pair; // or, return the intended move
    }
    /** Regular move sequence: Center -> Corners -> Sides. */
    private Pair regSequence() {
        if (openSpaces.contains(center)) // If center is open
            return center; // play there
        // If a corner is open
        if (Utility.intersection(corners, openSpaces).length != 0)
            return Utility.intersection(corners, openSpaces)[0]; // play there
        return levelEasy(); // Or else choose a random move (now at a side)
    }
    /** The easy level's move. Easy chooses a random open space. */
    Pair levelEasy() {
        // Return random open space   
        return openSpaces.get(rand.nextInt(openSpaces.size()));
    }
    /** 
     * The medium level's move.
     * Medium plays like a normal human. Block a three, or play randomly.
     */
    Pair levelMedium() { return safetyCheck(levelEasy()); }
    Pair levelHard() {
        // The hard level's move
        // Add code here
        return levelEasy();
    }
    Pair levelWizard() {
        // Bring it on
        // Add code here
        return levelEasy();
    }
    /** Is it already a draw? */
    boolean isFastDraw() {
        int cancels = 0; // The number of cancelled lines
        for (Pair[] array : winGroups) { // For ever winGroup
            char[] line = { refer(array[0]), refer(array[1]),
                            refer(array[2]) }; // create a line array
            if (('X'==line[0] || 'X'==line[1] || 'X'==line[2]) && // And if it
                ('O'==line[0] || 'O'==line[1] || 'O'==line[2])) // is blocked
                cancels++; // then one more row has been cancelled
        }
        return cancels == 8;
    }
    /**
     * @return whatever character of the winner. 
     * @return 'X' means the comp won. 
     * @return 'O' means the user won. 
     * @return 'D' means the game is a draw. 
     * @return 'P' means the game is still in progress.
     */
    char winner() {
        if (isFastDraw()) return 'D'; // If its a draw, no one won (aka D)
        for (Pair[] array : winGroups) // Else, check the winGroups
            if (refer(array[0]) == refer(array[1]) && // If the char in each
                refer(array[0]) == refer(array[2]) && // position is equal to
                refer(array[0]) != ' ') // either 'X' or 'O'
                return refer(array[0]); // return that character
        return 'P'; // Otherwise the game is still in progress
    }
}
/**
 * Main class.
 * Instantiated in main static method
 * and play method of TicTacToe object called.
 * Program runs from there.
 */
public class TicTacToe {
    // Fields:
    /** The static TicTacToe object that comes in the main class. Its play 
     * method is called to start the game. */
    private static TicTacToe ttt = new TicTacToe();
    /** Each tic-tac-toe object has its own Board. */
    private Board board;
    /** Main method. */
    public static void main(String[] args) {
        // Program starts here
        System.out.print(ttt.instructions()); // Print instructions
        Level level = ttt.gfpLevel(); // Get level
        boolean isCompFirst = ttt.gfpCompFirstTurn(); // See who goes first
        ttt.board = new Board(isCompFirst, level); // Set board object
        ttt.play(); // and play!    
    }
    // Tic-Tac-Toe main game methods:
    /** @return a long string stating the instructions. */
    private String instructions() {
        return ("Welcome human, to the greatest intellectual challenge of all "
                + "time: Tic-Tac-Toe.\nHere, you will challenge and face my "
                + "silicon processor.\nYou will enter your move as a number "
                + "from 0 through 8 which will correspond\nto one the following "
                + "positions:\n\n"
                + "\t\t 0 | 1 | 2\n"
                + "\t\t-----------\n"
                + "\t\t 3 | 4 | 5\n"
                + "\t\t-----------\n"
                + "\t\t 6 | 7 | 8\n\n"
                + "You will be player 'O', I will be 'X.'\n"
                + "Prepare yourself, human. The worst is yet to come!\n"
                + "Select which level you would like to play\n"
                + "\tEasy\n\tMedium\n\tHard\n\tWizard\n"
               ); // Return instructions String
    }
    /** 
     * Note: method does exception checking.
     * @return a reference to a level inputted by the user.
     */
    private Level gfpLevel() {
        String level; // Make reference to string
        while (true) { // Forever, if the user plays dirty
            System.out.print("Level: "); // Ask for level
            Scanner scan = new Scanner(System.in); // Make scanner
            level = scan.next(); // and get input
            try {// Try to interpret and return it
                return Level.valueOf(level.toUpperCase());
            } catch(Exception exc) { // but if its invalid
                System.err.println("That is invalid input."); // say so
            }
        }
    }
    /** 
     * Choice of return value is based on what user inputs.
     * Method does exception checking.
     * @return a boolean stating whether the computer should go first.
     */
    private boolean gfpCompFirstTurn() {
        System.out.println("Would you like to go first?"); // Ask for priority
        System.out.print("Type Y to go first, N to not, "
                + "or R to choose randomly: "); // Give him choices
        Scanner scan = new Scanner(System.in); // Make scanner object
        char input = scan.next().charAt(0); // and get input
        while (!((input == 'Y')||(input=='N')||(input=='R') ||
                 (input == 'y')||(input=='n')||(input=='r'))) { // If invalid
            // Print error message
            System.err.print("That is invalid input. Please re-enter: ");
            scan = new Scanner(System.in); // Make new scanner
            input = scan.next().charAt(0); // and get new input
        }
        if (input=='Y' || input == 'y') return false; // If they want to go, let 'em
        if (input=='N' || input == 'n') return true; // or don't, if they don't want to
        Random rand = new Random(); // If they want it to be random
        return rand.nextBoolean(); // return a random choice
    }
    /**
     * Method gets a move from the user. The method does the
     * exception handling. The move is a numerical coordinate.
     * The numerical coordinate is then converted to a Pair.
     * @see TicTacToe.convert
     * @return an integer representing user's move.
     */
    private int gfpMove() {
        int userMove; // Allocate 32-bits before-hand
        while (true) // Forever, in case the user plays dirty
            try {
                System.out.print("\nEnter your move: "); // Ask for move
                Scanner scan = new Scanner(System.in); // Make Scanner
                userMove = scan.nextInt(); // Get input
                if (userMove < 9 && userMove >= 0) // If its valid
                    if (board.getSpace(convert(userMove)).getRepr() == ' ')
                        break; // exit loop
                // Or else...
                throw new InputMismatchException(); // Throw new exception
            } catch (InputMismatchException exc) {
                // If an exception gets thrown
                System.err.println("That is invalid input."); // tell the user
            }
        return userMove; // Return value
    }
    /**
     * Take user's input on move and apply procedures from there.
     * @see TicTacToe.gfpMove
     * @see Board.makeMove
     */
    private void takeInput() {
        int userMove = gfpMove(); // Get user's move
        board.makeMove(convert(userMove), 'O'); // Procedures
        System.out.println("\n"+board+"\n"); // Print board
    }
    /** Play game! */
    private void play() {
        if (!board.getIsCompStarted()) // If the user starts
            takeInput(); // let him
        while (board.winner() == 'P') { // While the game is on
            System.out.println("Now it's my turn..."); // now its comp's turn
            if (board.getLevel() == Level.EASY) // If the level is easy
                board.makeMove(board.levelEasy(), 'X'); // play easy
            else if (board.getLevel() == Level.MEDIUM) // If the level is medium
                board.makeMove(board.levelMedium(), 'X'); // play medium
            else if (board.getLevel() == Level.HARD) // If the level is hard
                board.makeMove(board.levelHard(), 'X'); // play hard
            else // Or if the level is wizard otherwise
                board.makeMove(board.levelWizard(), 'X'); // bring it on
            System.out.println(board);
            if (board.winner()=='D' || board.winner()=='X' || 
                    board.winner()=='O') // If someone has won now
                break; // end game
            takeInput(); // Now ask for user's input again
        }
        if (board.winner() == 'X') // If comp won
            // Rub it in user's face
            System.out.println("\nHa, I won! Your puny mind is no match for me!");
        else if (board.winner() == 'O')// If the impossible - a user victory -
            // play sore loser
            System.out.println("No, no! You have won! But I swear I shall "
                    + "have vengeance!");
        else // Otherwise, it must be a draw
            System.out.println("It's a draw. Consider yourself lucky, human.");
    }
    /** Static conversion method. Converts numerical coordinate to pair.
     * @return the corresponding pair reference. */
    private static Pair convert(int n) {
        // Convert a numerical (0-8) coordinate
        // into a pair, and return it
        int x = (n % 3)-1; // Get x-coordinate
        int y = 1-((n-x-1)/3); // Get y-coordinate
        return Pair.getPair(x, y); // and return the Pair
    }
}