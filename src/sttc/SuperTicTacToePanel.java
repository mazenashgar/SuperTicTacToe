package sttc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**********************************************************************
 * Description: This class is for the window that contains that
 * Super Tic Tac Toe Game. The panel also highlights the winning
 * sequence in green.
 *
 * @author Mazen Ashgar
 * @author Max Carson
 *
 * @version 6/13/2018
 *********************************************************************/
public class SuperTicTacToePanel extends JPanel {

    /** A two dimensional array to track the Jbuttons */
    private static JButton[][] board;

    /** Quit button */
    private JButton quitButton;

    /** Reset button */
    private JButton resetButton;

    /** Undo button */
    private JButton undoButton;

    /** an object of the game logic */
    private SuperTicTacToeGame game;

    /** X icon image */
    private ImageIcon xIcon;

    /** O icon image */
    private ImageIcon oIcon;

    /** empty icon image */
    private ImageIcon emptyIcon;

    /** pop up icon image */
    private ImageIcon panelIcon;

    /** an object of the JPanel to contain all panels */
    private JPanel superPanel;

    /** integer to keep track of board size */
    private static int boardSize;

    /** integer to keep track of connections to win */
    private static int countToWin;

    /** an option panel button listener */
    private optionsListener oListen;

    /** a button listener for the board */
    private ButtonListener Listen;

    /** an object of the JPanel for the option buttons */
    private JPanel optionsPanel;

    /** A label to keep track of whose turn it is */
    private JLabel turn;

    /** A label to keep track of wins for X */
    private JLabel xWins;

    /** A label to keep track of wins for O */
    private JLabel oWins;

    /** an integer to keep track of the number of wins for X */
    private int xWinsCount;

    /** an integer to keep track of the number of wins for O */
    private int oWinsCount;

    /** integer to keep track of the minimum board size */
    final private int boardMin = 3;

    /** an integer to keep track of the maximum board size */
    final private int boardMax = 9;

    /******************************************************************
     * Constructor for the panel
     *****************************************************************/
    public SuperTicTacToePanel() {

        //Instantiate the game logic object
        game = new SuperTicTacToeGame();

        //Ask the user questions to set up the game
        setupGame();

        //Instantiate the two dimensional buttons array
        board = new JButton[boardSize][boardSize];

        //Instantiate the icons needed to be used (from the class)
        xIcon = new ImageIcon(getClass().getResource("x.jpg"));
        oIcon = new ImageIcon(getClass().getResource("o.jpg"));
        emptyIcon = new
                ImageIcon(getClass().getResource("e.jpg"));
        panelIcon = new ImageIcon(getClass().
                getResource("images.png"));

        //The game panel setup
        superPanel = new JPanel();
        superPanel.setLayout(new GridLayout(boardSize, boardSize));
        add(superPanel, BorderLayout.CENTER);
        Listen = new ButtonListener();

        //Adds the buttons, icons and labels to the board
        displayOptionPanel();
        drawBoard();
        displayBoard();
    }

    /******************************************************************
     * This method asks the user to setup the board and the rules
     * to win, when the user enters an invalid input, the program
     * ask for that input again.
     *****************************************************************/
    public void setupGame(){

        //variables needed for initializing the board
        String sizeSelect;
        int connector = 0;
        String connections;
        int turn;

        //do this loop, if input invalid, repeat
        do {

            try {

                //String to select board size
                sizeSelect = (String) JOptionPane.showInputDialog
                        (null,
                                ("Enter a board size between "
                                        + boardMin + " and "
                                        + boardMax
                                        + ":"), "Board Size",
                                JOptionPane.QUESTION_MESSAGE,
                                panelIcon,null,null);

                //if the user clicked ok
                if(sizeSelect != null) {

                    //cast string into an integer
                    game.boardLength = Integer.parseInt(sizeSelect);

                    //if input out of bounds, show error message
                    if (game.boardLength < boardMin
                            || game.boardLength > boardMax) {
                        JOptionPane.showMessageDialog(null,
                                "Invalid input \nTry again",
                                "ERROR", JOptionPane.ERROR_MESSAGE,
                                panelIcon);
                    }
                }else{  //if the user clicked cancel
                    System.exit(0);
                }

                /*
                if the user entered something other than a single
                number with the range
                */
            }catch (Exception error){
                JOptionPane.showMessageDialog(null,
                        "Invalid input \nTry again",
                        "ERROR", JOptionPane.ERROR_MESSAGE,
                        panelIcon);
                continue;   //show the input dialog again
            }

        } while (game.boardLength < boardMin
                || game.boardLength > boardMax) ;

        //set board size from user input
        setBoardSize(game.boardLength);

        //do this loop, if input invalid, repeat
        do{
            try {

                /*
                enter the amount of connections needed to win
                between boardMin and boardSize
                */
                connections = (String) JOptionPane.showInputDialog
                        (null,
                                "Enter a connections to win "+
                                        "between " + boardMin + " and " +
                                        game.boardLength + " : ",
                                "Connections", 3,
                                panelIcon, null, null);

                //if the user clicked ok
                if(connections != null) {

                    //cast string into an integer
                    connector = Integer.parseInt(connections);

                    //if input out of bounds, show error message
                    if (connector < boardMin
                            || connector > game.boardLength) {
                        JOptionPane.showMessageDialog(null,
                                "Invalid input \nTry again",
                                "ERROR", JOptionPane.ERROR_MESSAGE,
                                panelIcon);
                    }
                }else{  //if the user clicked cancel
                    System.exit(0);
                }

                /*
                if the user entered something other than
                a single number with the range
                */
            }catch (Exception error){
                JOptionPane.showMessageDialog(null, "Invalid input \nTry again",
                        "ERROR", JOptionPane.ERROR_MESSAGE, panelIcon);
                continue;   //show the input dialog again
            }
        }while(connector < boardMin || connector > game.boardLength);

        //set connections required from user input
        setCountToWin(connector);

        //Ask if X should go first (player select)
        turn = JOptionPane.showConfirmDialog(null,
                "Do you want X to start?",
                "SELECT PLAYER", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, panelIcon);

        //Save user choice
        if (turn == JOptionPane.YES_OPTION) {

            //if user chose yes
            game.xTurn = true;

            //Saves choice
            game.rememberChoice = true;
        } else {

            //if user chose no
            game.xTurn = false;

            //Saves choice
            game.rememberChoice = false;
        }

        //Sets default Status (See GameStatus.Java)
        game.status = GameStatus.IN_PROGRESS;

        //sets size of cell status and sets everything to empty
        //Status can contain (X,O, or empty) (See CellStatus.java)
        game.board = new CellStatus[getBoardSize()][getBoardSize()];

        //make all cells empty
        for (int row = 0; row < getBoardSize();  row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                game.board[row][col] = CellStatus.EMPTY;

            }
        }

        /*
        instantiate 2 arrays to keep track of
        the moves and a counter
        */
        game.undoCol = new int
                [game.boardLength * game.boardLength];
        game.undoRows = new int
                [game.boardLength * game.boardLength];
        game.undoIndex = 0;

        /*
        instantiate 2 arrays to highlight the winning
        sequence and a counter
        */
        game.highlightCol = new int
                [game.boardLength * game.boardLength];
        game.highlightRows = new int
                [game.boardLength * game.boardLength];
        game.highlightNeeded = false;
    }

    /******************************************************************
     * This method sets up the options panel buttons and labels
     *****************************************************************/
    public void displayOptionPanel() {

        //This lays out the options buttons.
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(6, 1));

        //set the initial labels and values for them
        xWinsCount = 0;
        oWinsCount = 0;
        turn = new JLabel("'s Turn");
        xWins = new JLabel("X wins: " + xWinsCount);
        oWins = new JLabel("O wins: " + oWinsCount);

        //add the labels to the optionPanel
        optionsPanel.add(turn);
        optionsPanel.add(xWins);
        optionsPanel.add(oWins);

        //instantiate the buttons and name them
        quitButton = new JButton("EXIT");
        resetButton = new JButton("RESET");
        undoButton = new JButton("UNDO");

        //instantiate a listener for the buttons
        oListen = new optionsListener();

        //add the listeners to the panel
        quitButton.addActionListener(oListen);
        resetButton.addActionListener(oListen);
        undoButton.addActionListener(oListen);

        //add the buttons to the panel
        optionsPanel.add(undoButton);
        optionsPanel.add(resetButton);
        optionsPanel.add(quitButton);
        add(optionsPanel, BorderLayout.EAST);
    }

    /******************************************************************
     * This method returns the connections number needed to win
     * specified by the user.
     *
     * @return countToWin number of connections
     *****************************************************************/
    public static int getCountToWin() {
        return countToWin;
    }

    /******************************************************************
     * This method sets the connections number needed to win
     *
     * @param cntToWin the number of connections to be set
     *****************************************************************/
    public static void setCountToWin(int cntToWin) {
        countToWin = cntToWin;
    }

    /******************************************************************
     * This method sets the board size specified by the user
     * @param brdSize the board size to be set to
     *****************************************************************/
    public static void setBoardSize(int brdSize) {
        boardSize = brdSize;
    }

    /******************************************************************
     * This method returns the board size
     * @return boardSize the board size
     *****************************************************************/
    public static int getBoardSize() {
        return boardSize;
    }

    /******************************************************************
     * This method draws the board by adding the cells and the
     * buttons, then adding the empty Icon, then add those to
     * superPanel.
     *****************************************************************/
    public void drawBoard() {

        /*
        Depending on the board size, create the buttons,
        and add them to the board panel
        */
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                board[rows][cols] =
                        new JButton("", emptyIcon);
                board[rows][cols].addActionListener(Listen);
                superPanel.add(board[rows][cols]);

            }
        }

        //Change the turn label according to the player's turn
        setTurnText();
    }

    /******************************************************************
     * This method updates the board and displays it according
     * to the cell status. Also, the highlighting will be set
     * off if it is not needed. The undo button will only be
     * enabled if there is a move to undo.
     *****************************************************************/
    public void displayBoard() {

        /*
        loop through the board, set the icon of the button
        according to the cell status, and if it is either
        X or O, set the button so it can't be pressed anymore
        */
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {

                if (game.getCell(row, col) == CellStatus.X) {
                    board[row][col].setIcon(xIcon);
                    board[row][col].setEnabled(false);
                } else if (game.getCell(row, col)
                        == CellStatus.O) {
                    board[row][col].setIcon(oIcon);
                    board[row][col].setEnabled(false);
                }

                else {
                    board[row][col].setEnabled(true);
                    board[row][col].setIcon(emptyIcon);
                }
            }
        }

        //loop through the board, and turn off all the highlighting
        for(int i = 0; i < boardSize + 1; i++){
            for(int j = 0; j <boardSize + 1; j++) {
                board[game.getHighlightRows(i)][game.getHighlightCol(j)].
                        setOpaque(false);
            }
        }

        /*
        if there is a move that can be undone, then
        enable the undo button, otherwise it is disabled
        */
        if(game.getUndoIndex() > 1){
            undoButton.setEnabled(true);
        }else{
            undoButton.setEnabled(false);
        }
    }

    /******************************************************************
     * This is a button listener class, every time one of the
     * cells is pressed, this method finds which button, then
     * deal with a winning if there is one.
     *****************************************************************/
    private class ButtonListener implements ActionListener {


        public void actionPerformed(ActionEvent event) {

            // Finds selected button
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (board[row][col] == event.getSource()) {
                        game.select(row, col);

                        if (game.getGameStatus()
                                == GameStatus.IN_PROGRESS) {
                            //If O is AI
                            if (game.isRememberChoice()) {
                                if (!game.canIwin()) {
                                    if (!game.canIBlock()) {
                                        if(!game.smartMoveO()){
                                            game.randomMove();
                                        }
                                    }
                                }
                            } else {
                                //If X is AI
                                if (!game.canIBlock()) {
                                    if (!game.canIwin()) {
                                        if(!game.smartMoveX()) {
                                            //game.randomMove();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // refreshes board
            displayBoard();
            setTurnText();

            //if there is a win, then highlight is needed
            if(game.isHighlightNeeded()) {

                //highlight the winning sequence
                for (int i = 0; i < countToWin - 3; i++) {
                    board[game.getHighlightRows(i)]
                            [game.getHighlightCol(i)].
                            setBackground(Color.green);
                    board[game.getHighlightRows(i)]
                            [game.getHighlightCol(i)].
                            setOpaque(true);
                }
            }

            /*
            if O won, highlight, show popup, update score, reset,
            and update board
            */
            if (game.getGameStatus() == GameStatus.O_WON) {
                highlightBoard();
                JOptionPane.showMessageDialog(null,
                        "O won and X lost!" +
                                "\nThe game will now reset",
                        "WINNER", 3, panelIcon);
                oWinsCount++;
                updateScore();
                game.Reset();
                displayBoard();
            }

            /*
            else if X won, highlight, show popup, update score, reset,
            and update board
            */
            else if (game.getGameStatus() == GameStatus.X_WON) {
                highlightBoard();
                JOptionPane.showMessageDialog(null,
                        "X won and 0 lost!" +
                                "\nThe game will now reset",
                        "WINNER", 3, panelIcon);
                xWinsCount++;
                updateScore();
                game.Reset();
                displayBoard();
            }

            /*
            else if O won, highlight, show popup, update score, reset,
            and update board
            */
            else if (game.getGameStatus() == GameStatus.CATS) {
                JOptionPane.showMessageDialog(null,
                        "Cats game" + "\nThe game" +
                                " will now reset",
                        "DRAW", 3, panelIcon);
                updateScore();
                game.Reset();
                displayBoard();
            }

            updateScore();
        }
    }

    /******************************************************************
     * This is an option buttons listener, if one of the buttons
     * on the options panel is pressed, the program will
     * preform one of the following "if statements"
     ******************************************************************/
    private class optionsListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            /*
            if the user pressed "Quit",
            make sure that they want to quit
            */
            if (event.getSource() == quitButton) {
                int exit = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to quit?",
                        "Exit", JOptionPane.YES_NO_OPTION,
                        3, panelIcon);

                if (exit == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }

            /*
            if the user pressed "Reset", make sure they want to reset,
            if yes, start a new panel
            */
            else if(event.getSource() == resetButton){
                int reset = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to reset?",
                        "Reset", JOptionPane.YES_NO_OPTION,
                        3, panelIcon);

                if(reset == JOptionPane.YES_OPTION){

                    //remove the old panel and start a new one
                    SuperTicTacToe.TicTacToe.getContentPane().removeAll();
                    SuperTicTacToe.setupPanel();
                }
            }

            //if the user pressed "Undo", if yes then update the panel
            else if(event.getSource() == undoButton){

                if(game.undo()) setTurnText();
                displayBoard();
            }
        }
    }

    /******************************************************************
     * This method sets the turn label depending on whose turn it is
     *****************************************************************/
    public void setTurnText(){

        if(game.isxTurn()) turn.setText("X's Turn");

        else turn.setText("O's Turn");
    }

    /******************************************************************
     * This method updates the scores and sets the labels accordingly
     *****************************************************************/
    public void updateScore(){

        xWins.setText("X wins: " + xWinsCount);
        oWins.setText("O Wins: " + oWinsCount);
    }

    /******************************************************************
     * This method highlights the winning sequence
     *****************************************************************/
    public void highlightBoard(){

        //highlight is needed if one of the players wins the round
        if(game.isHighlightNeeded()) {

            //highlight the board cells from the highlight arrays
            for (int i = 0; i < countToWin; i++) {
                for(int j = 0; j < countToWin ; j++) {
                    board[game.getHighlightRows(i)]
                            [game.getHighlightCol(j)].
                            setBackground(Color.green);
                    board[game.getHighlightRows(i)]
                            [game.getHighlightCol(j)].
                            setOpaque(true);
                }
            }
        }
    }
}