package sttc;

import javax.swing.JFrame;

/**********************************************************************
 * This class set-ups and launches the Super Tic-Tac-Toe Game.
 *
 * @author Mazen Ashgar
 * @author Max Carson
 *
 * @version 6/13/2018
 *********************************************************************/
public class SuperTicTacToe {

    /** A JFrame that contains SuperTicTacToePanel panel */
    static JFrame TicTacToe = new JFrame("Super Tic Tac Toe");

    /** A SuperTicTacToePanel object used to run the game */
    static SuperTicTacToePanel panel;

    /******************************************************************
     * The main method that Launches the super Tic-Tac-Toe game
     *****************************************************************/
    public static void main(String[] args) {

        setupPanel();

    }

    /******************************************************************
     * A method that creates an instance of the superTicTacToePanel and
     * set-ups the panel.
     *
     *****************************************************************/
    public static void setupPanel() {

        panel = new SuperTicTacToePanel();
        TicTacToe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TicTacToe.getContentPane().add(panel);
        TicTacToe.pack();
        TicTacToe.setVisible(true);

    }
}

