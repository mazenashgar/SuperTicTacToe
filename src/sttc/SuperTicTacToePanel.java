package sttc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javafx.scene.control.Cell;

public class SuperTicTacToePanel extends JPanel {
    protected static JButton[][] board;
    private CellStatus iCell;
    private JButton quitButton;
    private JButton resetButton;
    private JButton undoButton;
    private ImageIcon oIcon;
    private SuperTicTacToeGame game;
    private ImageIcon xIcon;
    private ImageIcon emptyIcon;
    private JPanel superPanel;


    // Swear there is a reason I made these static, but I can't remember why
    private static int boardSize;
    private static int countToWin;
    private optionsListener oListen;
    private ButtonListener Listen;
    private JPanel optionsPanel;

    public SuperTicTacToePanel() {

        game = new SuperTicTacToeGame();
        board = new JButton[boardSize][boardSize];

        // gets icons from the class file(This was a pain in the ass)
        xIcon = new ImageIcon(getClass().getResource(("x.jpg")));
        oIcon = new ImageIcon(getClass().getResource(("o.jpg")));
        emptyIcon = new ImageIcon(getClass().getResource(("e.jpg")));

        // This is the game panel setup
        superPanel = new JPanel();
        superPanel.setLayout(new GridLayout(boardSize, boardSize));
        add(superPanel, BorderLayout.CENTER);
        Listen = new ButtonListener();

        // Adds the buttons and icons to the board
        drawBoard();
        displayBoard();
        displayOptionPanel();

    }

    public void displayOptionPanel() {
        // This lays out the quit button. I want to reposition the quit button.
        optionsPanel = new JPanel();
        quitButton = new JButton("EXIT");
        resetButton = new JButton("RESET");
        undoButton = new JButton("UNDO");

        oListen = new optionsListener();

        quitButton.addActionListener(oListen);
        resetButton.addActionListener(oListen);
        undoButton.addActionListener(oListen);
        optionsPanel.add(quitButton);
        optionsPanel.add(resetButton);
        optionsPanel.add(undoButton);

        add(optionsPanel, BorderLayout.PAGE_START);
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
        // copy the previous display
//        for (int row = 0; row < boardSize; row++) {
//            for (int col = 0; col < boardSize; col++) {
//                previousBoard[row][col] = board[row][col];
//            }
//        }
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
    }

    // Listens to button from tic-tac-toe board
    private class ButtonListener implements ActionListener {
        //int boardSize = SuperTicTacToePanel.getBoardSize();

        public void actionPerformed(ActionEvent event) {



            // Finds selected button
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (board[row][col] == event.getSource())
                        game.select(row, col);
                }

            }
            // refreshes board
            displayBoard();

            if (game.getGameStatus() == GameStatus.O_WON) {
                JOptionPane.showMessageDialog(null, "O won and X lost!" + "\nThe game will now reset");
                game.Reset();
                displayBoard();
            }
            if (game.getGameStatus() == GameStatus.X_WON) {
                JOptionPane.showMessageDialog(null, "X won and 0 lost!" + "\nThe game will now reset");
                game.Reset();
                displayBoard();
            }
            if (game.getGameStatus() == GameStatus.CATS) {
                JOptionPane.showMessageDialog(null, "Cats game" + "\nThe game" + " will now reset");
                game.Reset();
                displayBoard();
            }

        }
    }

    // Pop up menu for when the quit button is pressed
    // yes or no option
    private class optionsListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == quitButton) {
                int exit = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit",
                        JOptionPane.YES_NO_OPTION);

                if (exit == JOptionPane.YES_OPTION) {

                    System.exit(0);
                }

            }
            if(event.getSource() == resetButton){
                int reset = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset?", "Reset",
                        JOptionPane.YES_NO_OPTION);

                if(reset == JOptionPane.YES_OPTION){
                    SuperTicTacToe.TicTacToe.getContentPane().removeAll();
                    SuperTicTacToe.setupPanel();
                }
            }
            if(event.getSource() == undoButton){
//                game.undo();
//                displayBoard();
            }
        }
    }
}
