package sttc;

import javax.swing.JFrame;

public class SuperTicTacToe {

    static JFrame TicTacToe = new JFrame("Super Tic Tac Toe");
    // Launches game
    public static void main(String[] args) {

        setupPanel();

    }

    public static void setupPanel(){

        TicTacToe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SuperTicTacToePanel panel = new SuperTicTacToePanel();
        TicTacToe.getContentPane().add(panel);

        TicTacToe.pack();
        TicTacToe.setVisible(true);
    }
}

