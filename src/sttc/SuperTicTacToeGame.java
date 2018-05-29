package sttc;

import javax.swing.JOptionPane;

public class SuperTicTacToeGame {
    private CellStatus[][] board;
    private CellStatus[][] previousBoard;
    private boolean xTurn;
    private boolean rememberChoice;
    private int delete;
    private GameStatus status;
    protected String sizeSelect;
    protected String connections;
    protected int connecter;
    protected int boardLength;
    int turn;


    public SuperTicTacToeGame() {

        setupGame();

    }

    public void setupGame(){
        //String to select board size
        this.sizeSelect =JOptionPane.showInputDialog(null, "Enter a board size between 3 and 9: ");
        boardLength = Integer.parseInt(sizeSelect);
        while(boardLength < 3 || boardLength > 9){
            JOptionPane.showMessageDialog(null, "Invalid input");
            this.sizeSelect =JOptionPane.showInputDialog(null,"Enter a board size between 3 and 9: ");
            boardLength = Integer.parseInt(sizeSelect);
        }
        SuperTicTacToePanel.setBoardSize(boardLength);

        //enter the amount of connections needed to win between 3 and boardSize
        this.connections =JOptionPane.showInputDialog(null, "Enter a connections to win between 2 and " + boardLength + " : ");
        this.connecter = Integer.parseInt(connections);
        SuperTicTacToePanel.setCountToWin(this.connecter);


        //Ask if X should go first (player select)
        turn = JOptionPane.showConfirmDialog(null, "Do you want X to start?", "SELECTPLAYER", JOptionPane.YES_NO_OPTION);
        if (turn == JOptionPane.YES_OPTION) {
            this.xTurn = true;
            //Saves choice
            this.rememberChoice = true;
        }else {
            this.xTurn = false;
            //Saves choice
            this.rememberChoice = false;
        }
        //Sets default Status (See GameStatus.Java)
        status = GameStatus.IN_PROGRESS;

        //sets size of cell status and sets everything to empty
        //Status can contain (X,O, or empty) (See CellStatus.java)
        board = new CellStatus[SuperTicTacToePanel.getBoardSize()][SuperTicTacToePanel.getBoardSize()];
        previousBoard = new CellStatus[SuperTicTacToePanel.getBoardSize()][SuperTicTacToePanel.getBoardSize()];
        for (int row = 0; row < SuperTicTacToePanel.getBoardSize();  row++) {
            for (int col = 0; col < SuperTicTacToePanel.getBoardSize(); col++) {
                board[row][col] = CellStatus.EMPTY;
                previousBoard[row][col] = CellStatus.EMPTY;
            }
        }
    }

    public void select(int row, int col) {

//        // copy the previous display before se
//        for (int rowIn = 0; rowIn < SuperTicTacToePanel.getBoardSize(); rowIn++) {
//            for (int column = 0; column < SuperTicTacToePanel.getBoardSize(); column++) {
//                previousBoard[rowIn][column] = board[rowIn][column];
//            }
//        }

        //sets status based on the users turn
        if (this.xTurn) {
            board[row][col] = CellStatus.X;
            this.xTurn = false;
        } else if (!this.xTurn) {
            board[row][col] = CellStatus.O;
            this.xTurn = true;
        }
    }

    public void Reset() {
        //Sets all to the cell status of empty
        for (int row = 0; row < SuperTicTacToePanel.getBoardSize(); row++) {
            for (int col = 0; col < SuperTicTacToePanel.getBoardSize(); col++) {
                board[row][col] = CellStatus.EMPTY;
                this.xTurn = this.rememberChoice;
            }
        }
    }

    public GameStatus getGameStatus() {
        // checks game status

        status = GameStatus.IN_PROGRESS;
        // gets integers from STTPanel
        int countToWin = SuperTicTacToePanel.getCountToWin();
        int boardSize = SuperTicTacToePanel.getBoardSize();
        int count = 0;

        // Checks for CATS game by seeing if every button has been selected
        for (int rowz = 0; rowz < boardSize; rowz++) {
            for (int colz = 0; colz < boardSize; colz++) {

                int totalSize = boardSize * boardSize;
                if ((getCell(rowz, colz) == CellStatus.O) || (getCell(rowz, colz) == CellStatus.X)) {
                    count++;
                }
                if (count == totalSize) {
                    status = GameStatus.CATS;
                }
            }
        }

        // Checks if columns in Xs have connections needed to win
        // This calculation only checks for the connections needed
        for (int rows = 0; rows < boardSize; rows++) {

            for (int cols = 0; cols < boardSize; cols++) {

                if (getCell(rows, cols) == CellStatus.X) {
                    int countX = 1;
                    for (int i = 1; i < countToWin; i++) {
                        if (rows + countToWin <= boardSize) {
                            if (getCell(rows + i, cols) == CellStatus.X) {
                                countX++;
                            }

                        }

                        if (countX == countToWin) {
                            status = GameStatus.X_WON;
                        }
                    }
                }
            }
        }
        // Checks if Os in columns have connections needed to win
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {

                if (getCell(rows, cols) == CellStatus.O) {
                    int countO = 1;
                    for (int i = 1; i < countToWin; i++) {
                        if (rows + countToWin <= boardSize) {
                            if (getCell(rows + i, cols) == CellStatus.O) {
                                countO++;

                            }
                        }
                    }
                    if (countO == countToWin) {
                        status = GameStatus.O_WON;
                    }
                }

            }
        }
        // Checks for connections in O rows
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {

                if (getCell(rows, cols) == CellStatus.O) {
                    int countO = 1;
                    for (int i = 1; i < countToWin; i++) {
                        if (cols + countToWin <= boardSize) {
                            if (getCell(rows, cols + i) == CellStatus.O) {
                                countO++;

                            }
                        }
                    }
                    if (countO == countToWin) {
                        status = GameStatus.O_WON;
                    }
                }

            }
        }

        // Checks for connections in x rows
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {

                if (getCell(rows, cols) == CellStatus.X) {
                    int countX = 1;
                    for (int i = 1; i < countToWin; i++) {
                        if (cols + countToWin <= boardSize) {
                            if (getCell(rows, cols + i) == CellStatus.X) {
                                countX++;
                            }
                        }
                    }
                    if (countX == countToWin) {
                        status = GameStatus.X_WON;
                    }
                }

            }
        }

        return status;
    }

//    public void undo(){
//        board = previousBoard;
//    }


    public CellStatus getCell(int row, int col) {

        return board[row][col];
    }

    public CellStatus getCellStatus(int row, int col) {

        return board[row][col];
    }
}

