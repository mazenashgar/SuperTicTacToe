package sttc;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class SuperTicTacToe {

    static JFrame TicTacToe = new JFrame("Super Tic Tac Toe");
    // Launches game
    public static void main(String[] args) {

        setupPanel();

    }

    public static void setupPanel(){

        //Object jbutton;
        TicTacToe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SuperTicTacToePanel panel = new SuperTicTacToePanel();
        TicTacToe.getContentPane().add(panel);

        TicTacToe.pack();
        TicTacToe.setVisible(true);
    }
}

