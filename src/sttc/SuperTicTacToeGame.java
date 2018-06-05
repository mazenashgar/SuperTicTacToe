package sttc;

import java.util.Random;

import javax.swing.JOptionPane;

public class SuperTicTacToeGame {
    private CellStatus[][] board;
    private int [] undoRows;
    private int [] undoCol;
    private int undoIndex;

    private boolean xTurn;
    private boolean rememberChoice;

    private int [] highlightRows;
    private int [] highlightCol;
    private int highlightIndex;
    private boolean highlightNeeded;


    private GameStatus status;
    private String sizeSelect;
    private String connections;
    private int connecter;
    private int boardLength;
    private int turn;
    private int positionRow [];
    private int [] positionCol;
    private Random randMove;

    public SuperTicTacToeGame() {

        setupGame();

    }

    public void setupGame(){

        //do this loop, if input invalid, repeat
        do {

            try {
                //String to select board size
                this.sizeSelect = JOptionPane.showInputDialog(null,
                        "Enter a board size between 3 and 9: ", "Board Size", 3);

                //if the user clicked ok
                if(sizeSelect != null) {

                    //cast string into an integer
                    boardLength = Integer.parseInt(sizeSelect);

                    //if input out of bounds, show error message
                    if (boardLength < 3 || boardLength > 9) {
                        JOptionPane.showMessageDialog(null, "Invalid input \nTry again",
                                "ERROR", 0);
                    }
                }else{  //if the user clicked cancel
                    System.exit(0);
                }
            } catch (Exception error){  //if the user entered something other than a single number with the range
                JOptionPane.showMessageDialog(null, "Invalid input \nTry again",
                        "ERROR", 0);
                continue;   //show the input dialog again
            }

        } while (boardLength < 3 || boardLength > 9) ;


        //set board size from user input
        SuperTicTacToePanel.setBoardSize(boardLength);

        //do this loop, if input invalid, repeat
        do{
            try {
                //enter the amount of connections needed to win between 3 and boardSize
                this.connections = JOptionPane.showInputDialog(null, "Enter a connections to win " +
                        "between 2 and " + boardLength + " : ", "Connections", 3);

                //if the user clicked ok
                if(connections != null) {

                    //cast string into an integer
                    this.connecter = Integer.parseInt(connections);

                    //if input out of bounds, show error message
                    if (connecter < 2 || connecter > boardLength) {
                        JOptionPane.showMessageDialog(null, "Invalid input \nTry again",
                                "ERROR", 0);
                    }
                }else{  //if the user clicked cancel
                    System.exit(0);
                }
            }catch (Exception error){   //if the user entered something other than a single number with the range
                JOptionPane.showMessageDialog(null, "Invalid input \nTry again",
                        "ERROR", 0);
                continue;   //show the input dialog again
            }
        }while(connecter < 2 || connecter > boardLength);

        //set connections required from user input
        SuperTicTacToePanel.setCountToWin(this.connecter);


        //Ask if X should go first (player select)
        turn = JOptionPane.showConfirmDialog(null, "Do you want X to start?",
                "SELECT PLAYER", JOptionPane.YES_NO_OPTION);

        //Save user choice
        if (turn == JOptionPane.YES_OPTION) {

            //if user chose yes
            this.xTurn = true;

            //Saves choice
            this.rememberChoice = true;
        } else {

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
        undoCol = new int [boardLength*boardLength];
        undoRows = new int [boardLength*boardLength];
        undoIndex = 0;

        //instantiate 2 arrays to highlight the winning sequence and a counter
        highlightCol = new int [boardLength * boardLength];
        highlightRows = new int [boardLength * boardLength];
        highlightIndex = 0;
        highlightNeeded = false;
    }

    public void select(int row, int col) {

        //sets status based on the users turn
        if (this.xTurn) {

            board[row][col] = CellStatus.X;
            setUndoRows(row);
            setUndoCol(col);
            incUndoIndex();
            this.xTurn = false;
        } else{

            board[row][col] = CellStatus.O;
            setUndoRows(row);
            setUndoCol(col);
            incUndoIndex();
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
        undoIndex = 0;
        highlightNeeded = false;
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
                    //I set the highlight Index to 1 immediately

                    int countX=1;
                    int firstRow = 0;
                    //but then I don't use the HighLight Index. This is so I don't have to use increment it outside the next for loop.
                    highlightRows[0] = rows;
                    highlightCol[0]= cols;


                    for(int i=1; i<countToWin; i++ ) {
                        //This is where Increment the loop. It starts at 1.

                        if (rows + i < boardSize) {

                            if (getCell(rows+i, cols) == CellStatus.X) {

                                countX++;
                                highlightRows[i] = rows+i;
                                highlightCol[i]= cols;

                            }


                        }else if (rows+i >= boardSize) {
                            if (getCell(firstRow, cols) == CellStatus.X) {

                                countX++;
                                highlightRows[i] = firstRow;
                                highlightCol[i]= cols;

                            }
                            firstRow++;
                        }

                    }
                    if(countX == countToWin) {
                        status = GameStatus.X_WON;
                        highlightNeeded = true;
                        //I return that X wins.
                        return status;


                    }


                }
            }
        }



        // Checks if Os in columns have connections needed to win
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                if (getCell(rows, cols) == CellStatus.O) {
                    int countO=1;
                    int firstRow = 0;

                    highlightRows[0] = rows;
                    highlightCol[0]= cols;

                    for(int i=1; i<countToWin; i++ ) {
                        if (rows + i < boardSize) {

                            if (getCell(rows+i, cols) == CellStatus.O) {
                                highlightRows[i] = rows+i;
                                highlightCol[i]= cols;
                                countO++;

                            }
                        }else if (rows+i >= boardSize) {
                            if (getCell(firstRow, cols) == CellStatus.O) {
                                countO++;
                                highlightRows[i] = firstRow;
                                highlightCol[i]= cols;
                            }

                            firstRow++;
                        }

                    }
                    if(countO == countToWin) {
                        status = GameStatus.O_WON;
                        highlightNeeded = true;
                        return status;
                    }


                }
            }
        }

        // Checks for connections in O rows
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                if (getCell(rows, cols) == CellStatus.O) {
                    int countO=1;
                    int firstCol = 0;
                    highlightRows[0] = rows;
                    highlightCol[0]= cols;
                    for(int i=1; i<countToWin; i++ ) {
                        if (cols + i < boardSize) {
                            if (getCell(rows, cols+i) == CellStatus.O) {
                                countO++;
                                highlightRows[i] = rows;
                                highlightCol[i]= cols+i;
                            }
                        }else if (cols+i >= boardSize) {
                            if (getCell(rows, firstCol) == CellStatus.O) {
                                countO++;
                                highlightRows[i] = rows;
                                highlightCol[i]= firstCol;

                            }
                            firstCol++;
                        }

                    }
                    if(countO == countToWin) {
                        status = GameStatus.O_WON;
                        highlightNeeded = true;
                        return status;
                    }


                }
            }
        }

        // Checks for connections in x rows
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                if (getCell(rows, cols) == CellStatus.X) {
                    int countX=1;
                    int firstCol = 0;
                    highlightRows[0] = rows;
                    highlightCol[0]= cols;

                    for(int i=1; i<countToWin; i++ ) {
                        if (cols + i < boardSize) {
                            if (getCell(rows, cols+i) == CellStatus.X) {
                                highlightRows[i] = rows;
                                highlightCol[i]= cols+i;
                                countX++;
                            }
                        }else if (cols+i >= boardSize) {
                            if (getCell(rows, firstCol) == CellStatus.X) {
                                highlightRows[i] = rows;
                                highlightCol[i]= firstCol;
                                countX++;
                            }
                            firstCol++;
                        }

                    }
                    if(countX == countToWin) {
                        status = GameStatus.X_WON;
                        highlightNeeded = true;
                        return status;
                    }


                }
            }
        }
        return status;
    }

    public boolean undo(){

        //if there is still turns to undo
        if(undoIndex > 1) {

            //decrement moves (AI's move)
            undoIndex--;

            //clear that move, make cell empty (AI's move)
            board[getUndoRows()][getUndoCol()] = CellStatus.EMPTY;

            //decrement moves (user's move)
            undoIndex--;

            //clear that move, make cell empty (user's move)
            board[getUndoRows()][getUndoCol()] = CellStatus.EMPTY;

            return true;
        }
        return false;
    }

    public boolean isHighlightNeeded() {
        return highlightNeeded;
    }

    public int getHighlightCol(int index) {
        return highlightCol[index];
    }

    public int getHighlightRows(int index) {
        return highlightRows[index];
    }

    public boolean isxTurn() {
        return xTurn;
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

    public void incUndoIndex() {
        this.undoIndex++;
    }

    public CellStatus getCell(int row, int col) {
        return board[row][col];
    }

    //A placeholder method till I design a strategy
    // Probably still useful afterwards
    public void randomMove() {

        //stops the game from making a move if the game is over
        if(getGameStatus() != GameStatus.X_WON) {

            //save positions of empty coordinates on tic tac toe board into array
            positionRow = new int [boardLength*boardLength];
            positionCol = new int [boardLength*boardLength];
            randMove = new Random(boardLength*boardLength-1);
            int countEmpty=0;
            int index = 0;

            //counts the empty spots and saves to array
            for (int row = 0; row < SuperTicTacToePanel.getBoardSize(); row++) {
                for (int col = 0; col < SuperTicTacToePanel.getBoardSize(); col++) {
                    if (board[row][col] == CellStatus.EMPTY) {
                        positionRow[index] = row;
                        positionCol[index] = col;
                        index++;
                        countEmpty++;
                    } else {
                        //This prevents array from having empty spaces
                        positionRow[index] = 99;
                        positionCol[index] = 99;
                        index++;
                    }
                }
            }

            //does not select a space if the board is full. This allows for a cats game
            if(countEmpty != 0) {

                //Selects an empty spot to move to
                for(int i=0; i<1;) {

                    int moveIndex = randMove.nextInt(boardLength*boardLength-1);

                    //repeats if slot selected is not empty
                    if(positionCol[moveIndex] != 99) {

                        select( positionRow[moveIndex], positionCol[moveIndex]);
                        i++;
                    }
                }
            }
        }
    }

    // Checks if AI player can win
    //Right now the AI player is O
    public boolean canIwin() {

        int countToWin = SuperTicTacToePanel.getCountToWin();
        int boardSize = SuperTicTacToePanel.getBoardSize();

        //prevents the AI from moving after the game ends
        if(getGameStatus() == GameStatus.X_WON) {
            return false;
        }
        //checks for 2 connections in row when the first cellstatus is O
        if(countToWin == 2) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.O) {
                        //checks for rows
                        if (cols + 1 < boardSize) {
                            if (getCell(rows, cols+1) == CellStatus.EMPTY) {
                                select(rows, cols+1);
                                return true;
                            }

                        }else if(cols + 1 == boardSize) {
                            if(getCell(rows, 0) == CellStatus.EMPTY) {
                                select(rows, 0 );
                                return true;
                            }
                        }
                        //checks for collumns
                        if (rows + 1 < boardSize) {
                            if (getCell(rows +1, cols) == CellStatus.EMPTY) {
                                select(rows+1, cols);
                                return true;
                            }

                        }else if(rows + 1 == boardSize) {
                            if(getCell(0, cols) == CellStatus.EMPTY) {
                                select(0, cols);
                            }
                        }
                    }

                }
            }
        }
        //Checks for 2 connections in rows and columns consecutive starting with an empty space
        if(countToWin == 2) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.EMPTY) {
                        if (cols + 1 < boardSize) {
                            if (getCell(rows, cols+1) == CellStatus.O) {
                                select(rows, cols);
                                return true;
                            }

                        }else if(cols + 1 == boardSize) {
                            if(getCell(rows, 0) == CellStatus.O) {
                                select(rows, cols);
                                return true;
                            }
                        }
                        if (rows + 1 < boardSize) {
                            if (getCell(rows +1, cols) == CellStatus.O) {
                                select(rows, cols);
                                return true;
                            }

                        }else if(rows + 1 == boardSize) {
                            if(getCell(0, cols) == CellStatus.O) {
                                select(rows, cols);
                            }
                        }
                    }

                }
            }
        }
        //Checks consecutive rows with 3 or more starting with cellStatus O
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.O) {
                        int countO=1;
                        int firstCol = 0;
                        for(int i=1; i<countToWin-1; i++ ) {
                            if (cols + i < boardSize) {
                                if (getCell(rows, cols+i) == CellStatus.O) {
                                    countO++;

                                }

                            }else if (cols+i >= boardSize) {
                                if (getCell(rows, firstCol) == CellStatus.O) {
                                    countO++;
                                }
                                firstCol++;
                            }
                            //If the connection is one less then the connections needed to win it checks that the last space is empty and then selects it
                            if(countO==countToWin-1) {
                                if (cols + i+1 < boardSize) {
                                    if (getCell(rows, cols+i+1) == CellStatus.EMPTY) {
                                        select(rows, cols+i+1);
                                        return true;
                                    }
                                }else{
                                    if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                        select(rows, firstCol);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //Checks consecutive Column with 3 or more starting with cellStatus O
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.O) {
                        int countO=1;
                        int firstRow = 0;
                        for(int i=1; i<countToWin-1; i++ ) {
                            if (rows + i < boardSize) {
                                if (getCell(rows+i, cols) == CellStatus.O) {
                                    countO++;

                                }
                            }else if (rows+i >= boardSize) {
                                if (getCell(firstRow, cols) == CellStatus.O) {
                                    countO++;
                                }
                                firstRow++;
                            }
                            //If the connection is one less then the connections needed to win it checks that the last space is empty and then selects it
                            if(countO==countToWin-1) {
                                if (rows + i+1 < boardSize) {
                                    if (getCell(rows+ i+1, cols) == CellStatus.EMPTY) {
                                        select(rows+i+1, cols);
                                        return true;
                                    }
                                }else{
                                    if (getCell(firstRow, cols) == CellStatus.EMPTY) {
                                        select(firstRow, cols);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //checks for columns with 3 or more connections starting with an empty cell status then consecutive O cell statuses
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.EMPTY) {
                        int countO=0;
                        int firstRow = 0;
                        //saves the empty cell location
                        int empRow = rows;
                        int empCol = cols;



                        for(int i=1; i<countToWin; i++ ) {
                            if (rows + i < boardSize) {
                                if (getCell(rows+i, cols) == CellStatus.O) {
                                    countO++;

                                }
                            }else if (rows+i >= boardSize) {
                                if (getCell(firstRow, cols ) == CellStatus.O) {
                                    countO++;
                                }
                                firstRow++;
                            }
                            //checks the final cell status and selects the empty cell
                            if(countO==countToWin-1) {
                                if (rows + i < boardSize) {
                                    if (getCell(rows + i, cols) == CellStatus.O) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }else{
                                    if (getCell(firstRow-1, cols) == CellStatus.O) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        //checks for rows with 3 or more connections starting with an empty cellstatus then consecutive O cellStatuses
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.EMPTY) {
                        int countO=0;
                        int firstCol = 0;
                        //saves the location of the empty cell
                        int empRow = rows;
                        int empCol = cols;



                        for(int i=1; i<countToWin; i++ ) {
                            if (cols + i < boardSize) {
                                if (getCell(rows, cols+i) == CellStatus.O) {
                                    countO++;

                                }
                            }else if (cols+i >= boardSize) {
                                if (getCell(rows, firstCol) == CellStatus.O) {
                                    countO++;
                                }
                                firstCol++;
                            }
                            //checks the final cell status and selects the empty cell
                            if(countO==countToWin-1) {
                                if (cols + i < boardSize) {
                                    if (getCell(rows, cols+i) == CellStatus.O) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }else{
                                    if (getCell(rows, firstCol-1) == CellStatus.O) {
                                        select(empRow, empCol );
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }
        //checks for unconsecutive rows (where there is a gap between Os) for 3 or more connections
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.O) {
                        int countO=1;
                        int firstCol = 0;
                        int countEmpty = 0;
                        int empRow=99;
                        int empCol=99;
                        //checks for an empty spot
                        for(int i=1; i<countToWin-1; i++ ) {
                            if (cols + i < boardSize) {
                                if (getCell(rows, cols+i) == CellStatus.O) {
                                    countO++;

                                }
                                if (getCell(rows, cols+i) == CellStatus.EMPTY) {
                                    countEmpty++;
                                    empRow = rows;
                                    empCol = cols+i;
                                }
                            }else if (cols+i >= boardSize) {
                                if (getCell(rows, firstCol) == CellStatus.O) {
                                    countO++;
                                }
                                if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                    countEmpty++;
                                    empRow = rows;
                                    empCol = firstCol;
                                }
                                firstCol++;
                            }
                            //checks the final spots cell status and selects the empty spot
                            //If statement checks if conditions are met
                            if(countO==countToWin-2 && countEmpty == 1 && empRow !=99) {
                                if (cols + i+1 < boardSize) {
                                    if (getCell(rows, cols+i+1) == CellStatus.O) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }
                                else{
                                    if (getCell(rows, firstCol) == CellStatus.O) {
                                        select(empRow, empCol);
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }
        //checks for unconsecutive rows (where there is a gap between Os) for 3 or more connections
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.O) {
                        int countO=1;
                        int firstRow = 0;
                        int countEmpty = 0;
                        int empRow=99;
                        int empCol=99;
                        for(int i=1; i<countToWin -1; i++ ) {
                            if (rows + i < boardSize) {
                                if (getCell(rows+i, cols) == CellStatus.O) {
                                    countO++;

                                }
                                if (getCell(rows+i, cols) == CellStatus.EMPTY) {
                                    //saves location of empty space
                                    countEmpty++;
                                    empRow = rows+i;
                                    empCol = cols;
                                }
                            }else if (rows+i >= boardSize) {
                                if (getCell(firstRow, cols) == CellStatus.O) {
                                    countO++;
                                }
                                if (getCell(firstRow, cols) == CellStatus.EMPTY) {
                                    countEmpty++;
                                    empRow = firstRow;
                                    empCol = cols;
                                }
                                firstRow++;
                            }
                            //checks that final space is an O and selects empty spot
                            if(countO ==countToWin-2 && countEmpty == 1 && empRow !=99) {
                                if (rows + i+1 < boardSize) {
                                    if (getCell(rows +i+1 , cols) == CellStatus.O) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }
                                else{
                                    if (getCell(firstRow, cols) == CellStatus.O) {
                                        select(empRow, empCol);
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }
        return false;
    }
    public boolean canIBlock() {

        int countToWin = SuperTicTacToePanel.getCountToWin();
        int boardSize = SuperTicTacToePanel.getBoardSize();

        if(getGameStatus() == GameStatus.X_WON) {
            return false;
        }

        //can I block for 2 checks rows and columns consecutive
        if(countToWin == 2) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.X) {
                        if (cols + 1 < boardSize) {
                            if (getCell(rows, cols+1) == CellStatus.EMPTY) {
                                select(rows, cols+1 );
                                return true;
                            }

                        }else if(cols + 1 == boardSize) {
                            if(getCell(rows, 0) == CellStatus.EMPTY) {
                                select(rows, 0 );
                                return true;
                            }
                        }
                        if (rows + 1 < boardSize) {
                            if (getCell(rows +1, cols) == CellStatus.EMPTY) {
                                select(rows+1, cols);
                                return true;
                            }

                        }else if(rows + 1 == boardSize) {
                            if(getCell(0, cols) == CellStatus.EMPTY) {
                                select(0, cols);
                            }
                        }
                    }

                }
            }
        }
        //can I win for 2 checks rows and columns consecutive
        if(countToWin == 2) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.EMPTY) {
                        if (cols + 1 < boardSize) {
                            if (getCell(rows, cols+1) == CellStatus.X) {
                                select(rows, cols );
                                return true;
                            }

                        }else if(cols + 1 == boardSize) {
                            if(getCell(rows, 0) == CellStatus.X) {
                                select(rows, cols );
                                return true;
                            }
                        }
                        if (rows + 1 < boardSize) {
                            if (getCell(rows +1, cols) == CellStatus.X) {
                                select(rows, cols);
                                return true;
                            }

                        }else if(rows + 1 == boardSize) {
                            if(getCell(0, cols) == CellStatus.X) {
                                select(rows, cols);
                            }
                        }
                    }

                }
            }
        }
        //blocks consecutive rows
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.X) {
                        int countX=1;
                        int firstCol = 0;
                        for(int i=1; i<countToWin-1; i++ ) {
                            if (cols + i < boardSize) {
                                if (getCell(rows, cols+i) == CellStatus.X) {
                                    countX++;

                                }
                            }else if (cols+i >= boardSize) {
                                if (getCell(rows, firstCol) == CellStatus.X) {
                                    countX++;
                                }
                                firstCol++;
                            }
                            if(countX==countToWin-1) {
                                if (cols + i+1 < boardSize) {
                                    if (getCell(rows, cols+i+1) == CellStatus.EMPTY) {
                                        select(rows, cols+i+1);
                                        return true;
                                    }
                                }else{
                                    if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                        select(rows, firstCol );
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }
        //blocks  rows starting with an empty then consecutive
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.EMPTY) {
                        int countX=0;
                        int firstCol = 0;
                        int empRow = rows;
                        int empCol = cols;



                        for(int i=1; i<countToWin; i++ ) {
                            if (cols + i < boardSize) {
                                if (getCell(rows, cols+i) == CellStatus.X) {
                                    countX++;

                                }
                            }else if (cols+i >= boardSize) {
                                if (getCell(rows, firstCol) == CellStatus.X) {
                                    countX++;
                                }
                                firstCol++;
                            }
                            if(countX==countToWin-1) {
                                if (cols + i < boardSize) {
                                    if (getCell(rows, cols+i) == CellStatus.X) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }else{
                                    if (getCell(rows, firstCol-1) == CellStatus.X) {
                                        select(empRow, empCol );
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }
        //blocks cols starting with an empty then consecutive
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.EMPTY) {
                        int countX=0;
                        int firstRow = 0;
                        int empRow = rows;
                        int empCol = cols;



                        for(int i=1; i<countToWin; i++ ) {
                            if (rows + i < boardSize) {
                                if (getCell(rows+i, cols) == CellStatus.X) {
                                    countX++;

                                }
                            }else if (rows+i >= boardSize) {
                                if (getCell(firstRow, cols ) == CellStatus.X) {
                                    countX++;
                                }
                                firstRow++;
                            }
                            if(countX==countToWin-1) {
                                if (rows + i < boardSize) {
                                    if (getCell(rows + i, cols) == CellStatus.X) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }else{
                                    if (getCell(firstRow-1, cols) == CellStatus.X) {
                                        select(empRow, empCol );
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }
        //blocks unconsecutive rows
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.X) {
                        int countX=1;
                        int firstCol = 0;
                        int countEmpty = 0;
                        int empRow=99;
                        int empCol=99;
                        for(int i=1; i<countToWin-1; i++ ) {
                            if (cols + i < boardSize) {
                                if (getCell(rows, cols+i) == CellStatus.X) {
                                    countX++;

                                }
                                if (getCell(rows, cols+i) == CellStatus.EMPTY) {
                                    countEmpty++;
                                    empRow = rows;
                                    empCol = cols+i;
                                }
                            }else if (cols+i >= boardSize) {
                                if (getCell(rows, firstCol) == CellStatus.X) {
                                    countX++;
                                }
                                if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                    countEmpty++;
                                    empRow = rows;
                                    empCol = firstCol;
                                }
                                firstCol++;
                            }

                            if(countX==countToWin-2 && countEmpty == 1 && empRow !=99) {
                                if (cols + i+1 < boardSize) {
                                    if (getCell(rows, cols+i+1) == CellStatus.X) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }
                                else{
                                    if (getCell(rows, firstCol) == CellStatus.X) {
                                        select(empRow, empCol);
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }

        //blocks consecutive columns
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.X) {
                        int countO = 1;
                        int firstRow = 0;
                        for(int i=1; i<countToWin-1; i++ ) {
                            if (rows + i < boardSize) {
                                if (getCell(rows+i, cols) == CellStatus.X) {
                                    countO++;

                                }
                            }else if (rows+i >= boardSize) {
                                if (getCell(firstRow, cols) == CellStatus.X) {
                                    countO++;
                                }
                                firstRow++;
                            }
                            if(countO==countToWin-1) {
                                if (rows + i+1 < boardSize) {
                                    if (getCell(rows+ i+1, cols) == CellStatus.EMPTY) {
                                        select(rows+i+1, cols);
                                        return true;
                                    }
                                }else{
                                    if (getCell(firstRow, cols) == CellStatus.EMPTY) {
                                        select( firstRow, cols );
                                        return true;
                                    }

                                }


                            }
                        }
                    }
                }

            }

        }
        //blocks unconsecutive columns
        if(countToWin >=3) {
            for (int rows = 0; rows < boardSize; rows++) {
                for (int cols = 0; cols < boardSize; cols++) {
                    if (getCell(rows, cols) == CellStatus.X) {
                        int countX=1;
                        int firstCol = 0;
                        int countEmpty = 0;
                        int empRow=99;
                        int empCol=99;
                        for(int i=1; i<countToWin -1; i++ ) {
                            if (rows + i < boardSize) {
                                if (getCell(rows+i, cols) == CellStatus.X) {
                                    countX++;

                                }
                                if (getCell(rows+i, cols) == CellStatus.EMPTY) {
                                    countEmpty++;
                                    empRow = rows+i;
                                    empCol = cols;
                                }
                            }else if (rows+i >= boardSize) {
                                if (getCell(firstCol, cols) == CellStatus.X) {
                                    countX++;
                                }
                                if (getCell(firstCol, cols) == CellStatus.EMPTY) {
                                    countEmpty++;
                                    empRow = firstCol;
                                    empCol = cols;
                                }
                                firstCol++;
                            }

                            if(countX==countToWin-2 && countEmpty == 1 && empRow !=99) {
                                if (rows + i+1 < boardSize) {
                                    if (getCell(rows +i+1 , cols) == CellStatus.X) {
                                        select(empRow, empCol);
                                        return true;
                                    }
                                }
                                else{
                                    if (getCell(firstCol, cols) == CellStatus.X) {
                                        select(empRow, empCol);
                                        return true;
                                    }

                                }
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    public boolean isRememberChoice() {
        return rememberChoice;
    }
}
