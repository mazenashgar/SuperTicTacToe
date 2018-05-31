package sttc;

import javax.swing.JOptionPane;

public class SuperTicTacToeGame {
    private CellStatus[][] board;
    protected int [] undoRows;
    protected int [] undoCol;
    protected int undoIndex;

    private boolean xTurn;
    private boolean rememberChoice;
    private int delete;
    private GameStatus status;
    private String sizeSelect;
    private String connections;
    private int connecter;
    private int boardLength;
    private int turn;


    public SuperTicTacToeGame() {

        setupGame();

    }

    public void setupGame(){

        do{
            //String to select board size
            this.sizeSelect = JOptionPane.showInputDialog(null, "Enter a board size between 3 and 9: ");

            //if the user hits cancel, quit
            if(sizeSelect == null){
                System.exit(0);
            }

            //check if input string contains a character or a white space, if so make string empty
            for(int i = 0; i < sizeSelect.length(); i++){
                char c = sizeSelect.charAt(i);
                if(Character.isLetter(c) || Character.isWhitespace(c)){
                    sizeSelect = "";
                    break;
                }
            }

            //check if string is empty, if so make input = -1
            if("".equals(sizeSelect)){
                sizeSelect = "-1";
            }

            //cast string into an integer
            boardLength = Integer.parseInt(sizeSelect);

            //if input out of bounds, show error message
            if(boardLength < 3 || boardLength > 9){
                JOptionPane.showMessageDialog(null, "Invalid input, try again");
            }

        }while(boardLength < 3 || boardLength > 9);

        //set board size from user input
        SuperTicTacToePanel.setBoardSize(boardLength);

        do{

            //enter the amount of connections needed to win between 3 and boardSize
            this.connections =JOptionPane.showInputDialog(null, "Enter a connections to win between 2 and " + boardLength + " : ");

            //if the user hits cancel, quit
            if(connections == null){
                System.exit(0);
            }

            //check if input string contains a character or a white space, if so make string empty
            for(int i = 0; i < connections.length(); i++){
                char c = connections.charAt(i);
                if(Character.isLetter(c) || Character.isWhitespace(c)){
                    connections = "";
                    break;
                }
            }

            //check if string is empty, if so make input = -1
            if("".equals(connections)){
                connections = "-1";
            }

            //cast string into an integer
            this.connecter = Integer.parseInt(connections);

            //if input out of bounds, show error message
            if(connecter < 3 || connecter > boardLength){
                JOptionPane.showMessageDialog(null, "Invalid input, try again");
            }


        }while(connecter < 2 || connecter > boardLength);

        //set connections required from user input
        SuperTicTacToePanel.setCountToWin(this.connecter);


        //Ask if X should go first (player select)
        turn = JOptionPane.showConfirmDialog(null, "Do you want X to start?", "SELECT PLAYER", JOptionPane.YES_NO_OPTION);

        //Save user choice
        if (turn == JOptionPane.YES_OPTION) {

            //if user chose yes
            this.xTurn = true;

            //Saves choice
            this.rememberChoice = true;
        }else {

            //if user chose no
            this.xTurn = false;

            //Saves choice
            this.rememberChoice = false;
        }

        //Sets default Status (See GameStatus.Java)
        status = GameStatus.IN_PROGRESS;

        //sets size of cell status and sets everything to empty
        //Status can contain (X,O, or empty) (See CellStatus.java)
        board = new CellStatus[SuperTicTacToePanel.getBoardSize()][SuperTicTacToePanel.getBoardSize()];

        //make all cells empty
        for (int row = 0; row < SuperTicTacToePanel.getBoardSize();  row++) {
            for (int col = 0; col < SuperTicTacToePanel.getBoardSize(); col++) {
                board[row][col] = CellStatus.EMPTY;

            }
        }

        //instantiate 2 arrays to keep track of the moves and a counter
        undoCol = new int[boardLength*boardLength];
        undoRows = new int [boardLength*boardLength];
        undoIndex = 0;
    }

    public void select(int row, int col) {

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

    public void undo(){
        if(undoIndex > 0) {
            undoIndex--;
            board[getUndoRows()][getUndoCol()] = CellStatus.EMPTY;
            if (this.xTurn) {
                this.xTurn = false;
            } else if (!this.xTurn) {
                this.xTurn = true;
            }
        }
    }

    public int getUndoRows() {
        return undoRows[getUndoIndex()];
    }

    public void setUndoRows(int index) {
        this.undoRows[getUndoIndex()] = index;
    }

    public int getUndoCol() {
        return undoCol[getUndoIndex()];
    }

    public void setUndoCol(int index) {
        this.undoCol[getUndoIndex()] = index;
    }

    public int getUndoIndex() {
        return undoIndex;
    }

    public void setUndoIndex(int undoIndex) {
        this.undoIndex = undoIndex;
    }

    public CellStatus getCell(int row, int col) {

        return board[row][col];
    }

    public CellStatus getCellStatus(int row, int col) {

        return board[row][col];
    }
}

