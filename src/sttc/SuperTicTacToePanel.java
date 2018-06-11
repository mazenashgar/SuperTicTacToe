package sttc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class SuperTicTacToePanel extends JPanel {

    private static JButton[][] board;

    private JButton quitButton;
    private JButton resetButton;
    private JButton undoButton;

    private SuperTicTacToeGame game;
    private ImageIcon xIcon;
    private ImageIcon oIcon;
    private ImageIcon emptyIcon;
    private ImageIcon panelIcon;
    private JPanel superPanel;


    private static int boardSize;
    private static int countToWin;
    private optionsListener oListen;
    private ButtonListener Listen;
    private JPanel optionsPanel;

    private JLabel turn;
    private JLabel xWins;
    private JLabel oWins;
    private int xWinsCount;
    private int oWinsCount;

    public SuperTicTacToePanel() {

        game = new SuperTicTacToeGame();
        board = new JButton[boardSize][boardSize];

        // gets icons from the class file(This was a pain in the ass)
        xIcon = new ImageIcon(getClass().getResource(("x.jpg")));
        oIcon = new ImageIcon(getClass().getResource(("o.jpg")));
        emptyIcon = new ImageIcon(getClass().getResource(("e.jpg")));
        panelIcon = new ImageIcon(getClass().getResource("images.png"));

        // This is the game panel setup
        superPanel = new JPanel();
        superPanel.setLayout(new GridLayout(boardSize, boardSize));
        add(superPanel, BorderLayout.CENTER);
        Listen = new ButtonListener();

        // Adds the buttons, icons and labels to the board
        displayOptionPanel();
        drawBoard();
        displayBoard();
    }

    public void displayOptionPanel() {

        // This lays out the options buttons.
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(6, 1));


        xWinsCount = 0;
        oWinsCount = 0;
        turn = new JLabel("'s Turn");
        xWins = new JLabel("X wins: " + xWinsCount);
        oWins = new JLabel("O wins: " + oWinsCount);

        optionsPanel.add(turn);
        optionsPanel.add(xWins);
        optionsPanel.add(oWins);

        quitButton = new JButton("EXIT");
        resetButton = new JButton("RESET");
        undoButton = new JButton("UNDO");

        oListen = new optionsListener();

        quitButton.addActionListener(oListen);
        resetButton.addActionListener(oListen);
        undoButton.addActionListener(oListen);

        optionsPanel.add(undoButton);
        optionsPanel.add(resetButton);
        optionsPanel.add(quitButton);


        add(optionsPanel, BorderLayout.EAST);
    }

    public static int getCountToWin() {
        return countToWin;
    }

    public static void setCountToWin(int countToWin) {
        SuperTicTacToePanel.countToWin = countToWin;
    }

    public static void setBoardSize(int boardSize) {
        SuperTicTacToePanel.boardSize = boardSize;
    }

    public static int getBoardSize() {
        return boardSize;
    }

    public void drawBoard() {
        // Adds the buttons and button listener to board

        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                board[rows][cols] = new JButton("", emptyIcon);
                board[rows][cols].addActionListener(Listen);
                superPanel.add(board[rows][cols]);

            }
        }
        setTurnText();
    }

    // adds/sets the icons on the board
    public void displayBoard() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {

                if (game.getCell(row, col) == CellStatus.X) {
                    board[row][col].setIcon(xIcon);
                    board[row][col].setEnabled(false);
                } else if (game.getCell(row, col) == CellStatus.O) {
                    board[row][col].setIcon(oIcon);
                    board[row][col].setEnabled(false);
                }

                else {
                    board[row][col].setEnabled(true);
                    board[row][col].setIcon(emptyIcon);
                }
            }
        }

        for(int i = 0; i < boardSize + 1; i++){
            for(int j = 0; j <boardSize + 1; j++) {
                board[game.getHighlightRows(i)][game.getHighlightCol(j)].setOpaque(false);
            }
        }

        if(game.getUndoIndex() > 1){
            undoButton.setEnabled(true);
        }else{
            undoButton.setEnabled(false);
        }
    }

    // Listens to button from tic-tac-toe board
    private class ButtonListener implements ActionListener {


        public void actionPerformed(ActionEvent event) {

            // Finds selected button
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (board[row][col] == event.getSource()) {
                        game.select(row, col);

                        if (game.getGameStatus() == GameStatus.IN_PROGRESS) {
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

            if(game.isHighlightNeeded()) {
                for (int i = 0; i < countToWin-3; i++) {

                    board[game.getHighlightRows(i)][game.getHighlightCol(i)].setBackground(Color.green);
                    board[game.getHighlightRows(i)][game.getHighlightCol(i)].setOpaque(true);
                }
            }

            if (game.getGameStatus() == GameStatus.O_WON) {
                highlightBoard();
                JOptionPane.showMessageDialog(null, "O won and X lost!" + "\nThe game will now reset", "WINNER", 3, panelIcon);
                oWinsCount++;
                updateScore();
                game.Reset();
                displayBoard();
            }
            else if (game.getGameStatus() == GameStatus.X_WON) {
                highlightBoard();
                JOptionPane.showMessageDialog(null, "X won and 0 lost!" + "\nThe game will now reset", "WINNER", 3, panelIcon);
                xWinsCount++;
                updateScore();
                game.Reset();
                displayBoard();
            }
            else if (game.getGameStatus() == GameStatus.CATS) {
                JOptionPane.showMessageDialog(null, "Cats game" + "\nThe game" + " will now reset", "DRAW", 3, panelIcon);
                updateScore();
                game.Reset();
                displayBoard();
            }

            updateScore();
        }
    }

    // Pop up menu for when the one of the buttons is pressed
    // yes or no option
    private class optionsListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == quitButton) {
                int exit = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit",
                        JOptionPane.YES_NO_OPTION, 3, panelIcon);

                if (exit == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
            if(event.getSource() == resetButton){
                int reset = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset?", "Reset",
                        JOptionPane.YES_NO_OPTION, 3, panelIcon);

                if(reset == JOptionPane.YES_OPTION){
                    SuperTicTacToe.TicTacToe.getContentPane().removeAll();
                    SuperTicTacToe.setupPanel();
                }
            }
            if(event.getSource() == undoButton){

                if(game.undo()) setTurnText();
                displayBoard();
            }
        }
    }

    public void setTurnText(){

        if(game.isxTurn()) turn.setText("X's Turn");

        else turn.setText("O's Turn");
    }

    public void updateScore(){

        xWins.setText("X wins: " + xWinsCount);
        oWins.setText("O Wins: " + oWinsCount);
    }

    public void highlightBoard(){
        if(game.isHighlightNeeded()) {
            for (int i = 0; i < countToWin; i++) {
                for(int j = 0; j < countToWin ; j++) {
                    board[game.getHighlightRows(i)][game.getHighlightCol(j)].setBackground(Color.green);
                    board[game.getHighlightRows(i)][game.getHighlightCol(j)].setOpaque(true);
                }
            }
        }
    }
}

