package sttc;

import java.util.Random;

/**********************************************************************
 * This is the class that runs the logic of the Super Tic Tac Toe game.
 * The game only considers horizontal or vertical wins, and the board
 * wraps around like a ball shape.
 *
 * @author Max Carson
 * @author Mazen Ashgar
 *
 * @version 6/13/2018
 *********************************************************************/
public class SuperTicTacToeGame {

    /** A two dimensional array to keep track of cell status*/
    protected CellStatus[][] board;

    /** An integer array to keep track of undo row moves*/
    protected int [] undoRows;

    /** An integer array to keep track of undo column moves*/
    protected int [] undoCol;

    /** An integer to keep track of the undo index for array elements*/
    protected int undoIndex;

    /** A variable to keep track of whose turn X or O's */
    protected boolean xTurn;

    /** A variable to store the users choice of who goes first X or O*/
    protected boolean rememberChoice;

    /** An integer array to keep track of highlight row elements*/
    protected int [] highlightRows;

    /** An integer array to keep track of highlight column elements*/
    protected int [] highlightCol;

    /** A boolean to decied if the board needs highlighting*/
    protected boolean highlightNeeded;

    /** An integer array to keep track of smartMove row moves*/
    private int [] smartMoveRow;

    /** An integer array to keep track of smartMove column moves*/
    private int [] smartMoveCol;

    /** An object of type GameStatus to keep track of Game status*/
    protected GameStatus status;

    /** An integer variable to keep track of the board size*/
    protected int boardLength;

    /**An integer array to keep track of position row of empty spaces*/
    private int [] positionRow ;

    /**An integer array to keep track of position col of empty spaces*/
    private int [] positionCol;

    /** An object of type Random to generate random moves*/
    private Random randMove;

    /******************************************************************
     * Constructor
     *****************************************************************/
    public SuperTicTacToeGame() {}

    /******************************************************************
     * This method is called when the user selects a cell on the board.
     * the cell is changed to a status depending on the turn, and also
     * keeping up with the undo arrays so those moves can be undone.
     * Then switch the players turn
     *
     * @param row the row number of the cell
     * @param col the column number of the cell
     *****************************************************************/
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

    /******************************************************************
     * This method reset the game by making all the cell status' empty
     * Also, reseting the undo index back to 0 so it starts over,
     * also, highlighting is not needed so highlighNeeded
     * is set to false
     *****************************************************************/
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

    /******************************************************************
     * This method checks the condition of the game and returns the
     * current status of the game. If X, O win or tie it will return
     * the winner. If not it will return in progress.
     *
     * @return GameStatus used to determine if the
     * game has a winner or if the game is still in progress
     *****************************************************************/
    public GameStatus getGameStatus() {

        //Sets initial status to IN_Progress
        status = GameStatus.IN_PROGRESS;

        // retrieves integers countToWin and boardSize from SuperTicTacToePanel
        int countToWin = SuperTicTacToePanel.getCountToWin();
        int boardSize = SuperTicTacToePanel.getBoardSize();
        int count = 0;

        // Checks for CATS game by checking if every button has been selected
        for (int rowz = 0; rowz < boardSize; rowz++) {
            for (int colz = 0; colz < boardSize; colz++) {
                int totalSize = boardSize * boardSize;
                if ((getCell(rowz, colz) == CellStatus.O) || (getCell(rowz, colz) == CellStatus.X)) {
                    count++;
                }
                //changes Status to CATS if every button is selected
                if (count == totalSize) {
                    status = GameStatus.CATS;
                }
            }
        }

        // Checks if columns have connections needed to win for X to win
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //counts the first X and then checks for consecutive Xs in rows
                if (getCell(rows, cols) == CellStatus.X) {
                    int countX=1;
                    //Integer used resets the row count to check the opposite side of the board
                    int firstRow = 0;
                    //Saves location of rows and columns into arrays
                    highlightRows[0] = rows;
                    highlightCol[0]= cols;

                    //for loop that goes for connections need to win.
                    for(int i=1; i<countToWin; i++ ) {

                        //Checks that the row is not out of bounds
                        if (rows + i < boardSize) {

                            //increments count and saves the column and row if the cell has a cellStatus of X
                            if (getCell(rows+i, cols) == CellStatus.X) {
                                countX++;
                                highlightRows[i] = rows+i;
                                highlightCol[i]= cols;
                            }

                            //checks the first row if next row is out of bounds
                        }else if (rows+i >= boardSize) {

                            //increments count and saves the column and row if the cell has a cellStatus of X
                            if (getCell(firstRow, cols) == CellStatus.X) {
                                countX++;
                                highlightRows[i] = firstRow;
                                highlightCol[i]= cols;

                            }
                            //Increments the count of the firstRow
                            firstRow++;
                        }

                    }
                    //checks if X has the connection needed to win then
                    //returns GameStatus.X_WON and sets highLightNeeded to true
                    if(countX == countToWin) {
                        status = GameStatus.X_WON;
                        highlightNeeded = true;
                        return status;


                    }


                }
            }
        }



        // Checks if Os in columns have connections needed to win
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //checks for initial O and then checks for consecutive Os in columns
                if (getCell(rows, cols) == CellStatus.O) {

                    int countO=1;
                    int firstRow = 0;

                    highlightRows[0] = rows;
                    highlightCol[0]= cols;
                    //Checks for consecutive Os in columns
                    for(int i=1; i<countToWin; i++ ) {
                        //Checks next row for Os as long as the array is not out of bounds
                        if (rows + i < boardSize) {
                            //Increments countO and saves the position
                            if (getCell(rows+i, cols) == CellStatus.O) {
                                highlightRows[i] = rows+i;
                                highlightCol[i]= cols;
                                countO++;

                            }
                            //Checks the first row for Os when the column number is out of bounds
                        }else if (rows+i >= boardSize) {
                            if (getCell(firstRow, cols) == CellStatus.O) {
                                countO++;
                                highlightRows[i] = firstRow;
                                highlightCol[i]= cols;
                            }
                            //increments first row
                            firstRow++;
                        }

                    }
                    //Checks if Os have won by seeing if they have connections needed to win then
                    //Sets highLightNeed to true which highLights the game Status and return the status
                    if(countO == countToWin) {
                        status = GameStatus.O_WON;
                        highlightNeeded = true;
                        return status;
                    }


                }
            }
        }

        // Checks if O has the connections needed to win
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks if O has a connection
                if (getCell(rows, cols) == CellStatus.O) {
                    int countO=1;
                    int firstCol = 0;
                    highlightRows[0] = rows;
                    highlightCol[0]= cols;
                    //checks for consecutive connection for the count needed to win in rows
                    for(int i=1; i<countToWin; i++ ) {
                        //checks that columns is not out of bounds
                        if (cols + i < boardSize) {
                            //increments the count if the cell status is O and saves location
                            if (getCell(rows, cols+i) == CellStatus.O) {
                                countO++;
                                highlightRows[i] = rows;
                                highlightCol[i]= cols+i;
                            }
                            //checks if columns is out of bounds
                        }else if (cols+i >= boardSize) {
                            //checks the first columns cell status for O and increments the count
                            if (getCell(rows, firstCol) == CellStatus.O) {
                                countO++;
                                highlightRows[i] = rows;
                                highlightCol[i]= firstCol;

                            }
                            //increments firstCol
                            firstCol++;
                        }

                    }
                    //returns the status if O won and sets highLight needed to true
                    if(countO == countToWin) {
                        status = GameStatus.O_WON;
                        highlightNeeded = true;
                        return status;
                    }


                }
            }
        }

        // Checks for connections needed to win in rows for X
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                // Checks for initial X and starts countX
                if (getCell(rows, cols) == CellStatus.X) {
                    int countX = 1;
                    int firstCol = 0;
                    highlightRows[0] = rows;
                    highlightCol[0]= cols;
                    //Checks consecutive Xs in rows for connections needed to win
                    for(int i=1; i<countToWin; i++ ) {
                        //checks that the columns is not bigger than the board
                        if (cols + i < boardSize) {
                            //Increments count and saves row and collumn
                            if (getCell(rows, cols+i) == CellStatus.X) {
                                highlightRows[i] = rows;
                                highlightCol[i]= cols+i;
                                countX++;
                            }
                            // Check if the column value is bigger than the board
                        }else if (cols+i >= boardSize) {
                            //checks the first column for X
                            if (getCell(rows, firstCol) == CellStatus.X) {
                                highlightRows[i] = rows;
                                highlightCol[i]= firstCol;
                                countX++;
                            }
                            //increments the first column
                            firstCol++;
                        }

                    }
                    //checks if X has connections needed to win
                    //returns the gamestatus and chenges highlight needed to true
                    if(countX == countToWin) {
                        status = GameStatus.X_WON;
                        highlightNeeded = true;
                        return status;
                    }


                }
            }
        }
        //returns either IN_PROGRESS or CATS
        return status;

    }

    /******************************************************************
     * This method is called when the undo button is pressed. if the
     * undo index still has turns to undo then it would undo the last
     * two moves by makes the cells status empty.
     *
     * @return boolean if the undo was successful or not
     *****************************************************************/
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

    /******************************************************************
     * This method returns if the board needs highlighting or no
     * and that only changes if there is a winner.
     *
     * @return highlightNeeded boolean if highlight is needed or no
     *****************************************************************/
    public boolean isHighlightNeeded() {
        return highlightNeeded;
    }

    /******************************************************************
     * This method returns the value in the element "index"
     * in the highlight column array.
     *
     * @param index the element we need the value of
     * @return highlightCol[index] the value of the element passed
     *****************************************************************/
    public int getHighlightCol(int index) {
        return highlightCol[index];
    }

    /******************************************************************
     * This method returns the value in the element "index"
     * in the highlight row array.
     *
     * @param index the element we the value of
     * @return highlightRows[index] the value of the element passed
     *****************************************************************/
    public int getHighlightRows(int index) {
        return highlightRows[index];
    }

    /******************************************************************
     * This method returns if it is X's turn or no
     *
     * @return xTurn true or false
     *****************************************************************/
    public boolean isxTurn() {
        return xTurn;
    }

    /******************************************************************
     * This method return the value of the current element of
     * the Undo rows array.
     *
     * @return undoRows[getUndoIndex()] the current value of the array
     *****************************************************************/
    public int getUndoRows() {
        return undoRows[getUndoIndex()];
    }

    /******************************************************************
     * This method sets the value of the current index
     * in the undo row array equal to the parameter "index"
     *
     * @param index the value to set the value of
     *              the array element equal to
     *****************************************************************/
    public void setUndoRows(int index) {
        this.undoRows[getUndoIndex()] = index;
    }

    /******************************************************************
     * This method return the value of the current element of
     * the Undo column array.
     *
     * @return undoCol[getUndoIndex()] the current value of the array
     *****************************************************************/
    public int getUndoCol() {
        return undoCol[getUndoIndex()];
    }

    /******************************************************************
     * This method sets the value of the current index
     * in the undo column array equal to the parameter "index"
     *
     * @param index the value to set the value of
     *              the array element equal to
     *****************************************************************/
    public void setUndoCol(int index) {
        this.undoCol[getUndoIndex()] = index;
    }

    /******************************************************************
     * This method return the undo index used in the undo arrays
     *
     * @return undoIndex the index of the undo arrays
     *****************************************************************/
    public int getUndoIndex() {
        return undoIndex;
    }

    /******************************************************************
     * This method increaments the undo index for the undo arrays
     *****************************************************************/
    public void incUndoIndex() {
        this.undoIndex++;
    }

    /******************************************************************
     * this method return a cell after passing the location of it
     * from the parameters row, col
     *
     * @param row the row number of the cell
     * @param col the column number of the cell
     * @return board[row][col] a cell from the board
     *****************************************************************/
    public CellStatus getCell(int row, int col) {
        return board[row][col];
    }

    /******************************************************************
     * This method randomly selects a spot for the AI to move if their
     * are no better spaces available
     *****************************************************************/
    public void randomMove() {

        //Counts Empty Spaces
        int countEmpty = 0;
        //Used as the index for the array
        int index = 0 ;

        //stops the game from making a move if the game is over
        if(getGameStatus() != GameStatus.X_WON) {

            //save positions of empty coordinates on tic tac toe board into array
            positionRow = new int [boardLength*boardLength];
            positionCol = new int [boardLength*boardLength];
            randMove = new Random(boardLength*boardLength-1);
            countEmpty=0;
            index = 0;

            //counts the empty locations and saves to empty location to an array
            for (int row = 0; row < SuperTicTacToePanel.getBoardSize(); row++) {
                for (int col = 0; col < SuperTicTacToePanel.getBoardSize(); col++) {
                    if (board[row][col] == CellStatus.EMPTY) {
                        positionRow[index] = row;
                        positionCol[index] = col;
                        index++;
                        countEmpty++;
                        //This prevents array from having empty spaces
                    } else {
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

    /******************************************************************
     * This method checks for a winning space and selects the
     * winning space or returns false otherwise
     *
     * @return returns true if the AI can win
     *****************************************************************/
    public boolean canIwin() {

        //retrieves BoardSize and CountTowin from SuperTicTacToePanel
        int countToWin = SuperTicTacToePanel.getCountToWin();
        int boardSize = SuperTicTacToePanel.getBoardSize();

        //Used when looping to the other side
        int firstRow = 0;
        int firstCol = 0;

        //Save location of empty space
        int empRow = 0;
        int empCol = 0;
        //Count O on board
        int countO = 0;
        //Counts empty spaces on the board
        int countEmpty =0;

        //Checks consecutive rows with 3 or more starting with cellStatus O
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks for a O in every cell
                if (getCell(rows, cols) == CellStatus.O) {
                    countO = 1;
                    firstCol = 0;
                    //checks for one less O than the connection to win
                    for(int i=1; i<countToWin-1; i++ ) {
                        //Checks the next column for O, unless the no column exists
                        if (cols + i < boardSize) {
                            if (getCell(rows, cols+i) == CellStatus.O) {
                                countO++;

                            }
                            //checks the first column for Os if no column exists
                        }else if (cols+i >= boardSize) {
                            if (getCell(rows, firstCol) == CellStatus.O) {
                                countO++;
                            }
                            firstCol++;
                        }
                        //If the connection is one less then the connections needed to win it checks that the last space is empty and then selects it
                        if(countO==countToWin-1) {
                            //checks if the column number is smaller than the board size
                            if (cols + i+1 < boardSize) {
                                //Selects the row and column if the space is empty and returns true
                                if (getCell(rows, cols+i+1) == CellStatus.EMPTY) {
                                    select(rows, cols+i+1);
                                    return true;
                                }
                                //If the column is larger than the board size it checks for an empty space in the first column
                            }else{
                                //Selects the row and column if the space is empty and returns true
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

        //Checks consecutive Column with 3 or more starting with cellStatus O
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks for an O in every cell
                if (getCell(rows, cols) == CellStatus.O) {
                    countO = 1;
                    firstRow = 0;
                    //checks that consecutive rows have one less O than the connection needed to win
                    for(int i=1; i<countToWin-1; i++ ) {
                        //if the next row is exist checks for Os
                        if (rows + i < boardSize) {
                            if (getCell(rows+i, cols) == CellStatus.O) {
                                countO++;

                            }
                            //if the next row does not exist checks the first row for Os
                        }else if (rows+i >= boardSize) {
                            if (getCell(firstRow, cols) == CellStatus.O) {
                                countO++;
                            }
                            //increments firstRow
                            firstRow++;
                        }
                        //If the connection is one less then the connections needed to win it checks that the last space is empty and then selects it
                        if(countO == countToWin-1) {
                            //if the next row exists checks for empty and then the selects position
                            if (rows + i+1 < boardSize) {
                                if (getCell(rows + i + 1, cols) == CellStatus.EMPTY) {
                                    select(rows + i + 1, cols);
                                    return true;
                                }
                            }else{
                                //if the next row is does not exist checks the first row for an empty and then selects the position
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


        //checks for columns with 3 or more connections starting with an empty cell status then consecutive O cell statuses
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //checks for an empty cell
                if (getCell(rows, cols) == CellStatus.EMPTY) {
                    countO=0;
                    firstRow = 0;
                    //saves the empty cell location
                    empRow = rows;
                    empCol = cols;

                    //checks for consecutive Os in columns
                    for(int i=1; i<countToWin; i++ ) {
                        //checks the next row if it exists for Os
                        if (rows + i < boardSize) {
                            if (getCell(rows+i, cols) == CellStatus.O) {
                                countO++;

                            }
                            //checks the first row for Os if the next row does not exist
                        }else if (rows+i >= boardSize) {
                            if (getCell(firstRow, cols ) == CellStatus.O) {
                                countO++;
                            }
                            firstRow++;
                        }
                        //checks the final cell status and selects the empty cell
                        if(countO==countToWin-1) {
                            //checks the last column for O and selects the empty cell
                            if (rows + i < boardSize) {
                                if (getCell(rows + i, cols) == CellStatus.O) {
                                    select(empRow, empCol);
                                    return true;
                                }
                            }else{
                                //checks the first column for O and selects the empty cell
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
        //check rows for connections starting with an empty cell status then consecutive O cellStatuses
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks for empty cellStatus
                if (getCell(rows, cols) == CellStatus.EMPTY) {
                    countO = 0;
                    firstCol = 0;
                    //saves the location of the empty cell
                    empRow = rows;
                    empCol = cols;
                    //checks for consecutive Os in rows
                    for(int i=1; i<countToWin; i++ ) {
                        //checks the next column for O
                        if (cols + i < boardSize) {
                            if (getCell(rows, cols+i) == CellStatus.O) {
                                countO++;

                            }
                            //checks the first column for O if the next column does not exist
                        }else if (cols+i >= boardSize) {
                            if (getCell(rows, firstCol) == CellStatus.O) {
                                countO++;
                            }
                            firstCol++;
                        }
                        //checks the final cell status and selects the empty cell
                        if(countO==countToWin-1) {
                            //checks the next column for O and selects the empty column
                            if (cols + i < boardSize) {
                                if (getCell(rows, cols+i) == CellStatus.O) {
                                    select(empRow, empCol);
                                    return true;
                                }

                            }else{
                                //If the first column is O then it selects the empty cell
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

        //checks for connection need to win, where there is a gap between Os
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //checks for an O
                if (getCell(rows, cols) == CellStatus.O) {
                    countO=1;
                    firstCol = 0;
                    countEmpty = 0;
                    empRow=99;
                    empCol=99;
                    //checks for an empty spot or an O
                    for(int i=1; i<countToWin-1; i++ ) {
                        //checks the next column
                        if (cols + i < boardSize) {
                            //checks for O
                            if (getCell(rows, cols+i) == CellStatus.O) {
                                countO++;

                            }
                            //checks for empty
                            if (getCell(rows, cols+i) == CellStatus.EMPTY) {
                                countEmpty++;
                                empRow = rows;
                                empCol = cols+i;
                            }
                            //checks the first column if the next column does not exist
                        }else if (cols+i >= boardSize) {
                            //checks for O
                            if (getCell(rows, firstCol) == CellStatus.O) {
                                countO++;
                            }
                            //checks for empty
                            if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                countEmpty++;
                                empRow = rows;
                                empCol = firstCol;
                            }
                            firstCol++;
                        }
                        //Checks if the AI has 2 less connection than needed to win and one empty space
                        if(countO == countToWin-2 && countEmpty == 1) {
                            //checks the next row for final connection and selects the empty space
                            if (cols + i+1 < boardSize) {
                                if (getCell(rows, cols+i+1) == CellStatus.O) {
                                    select(empRow, empCol);
                                    return true;
                                }
                            }
                            //checks the next row for final connection and selects the empty space
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

        //checks for rows for unconsecutive connections needed to win
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //checks for an O
                if (getCell(rows, cols) == CellStatus.O) {
                    countO = 1;
                    firstRow = 0;
                    countEmpty = 0;
                    empRow = 99;
                    empCol = 99;
                    //Checks for empty spaces and connections
                    for(int i=1; i<countToWin -1; i++ ) {
                        //checks the next row
                        if (rows + i < boardSize) {
                            //counts Os
                            if (getCell(rows+i, cols) == CellStatus.O) {
                                countO++;
                            }
                            //Counts empty space and save location
                            if (getCell(rows+i, cols) == CellStatus.EMPTY) {
                                //saves location of empty space
                                countEmpty++;
                                empRow = rows+i;
                                empCol = cols;
                            }
                            //checks the first row if the next row does not exist
                        }else if (rows+i >= boardSize) {
                            //counts Os
                            if (getCell(firstRow, cols) == CellStatus.O) {
                                countO++;
                            }
                            //Counts empty space and save location
                            if (getCell(firstRow, cols) == CellStatus.EMPTY) {
                                countEmpty++;
                                empRow = firstRow;
                                empCol = cols;
                            }
                            firstRow++;
                        }
                        //Checks that count O is 2 less the than the connections to win and county empty is one
                        if(countO == countToWin-2 && countEmpty == 1 ) {
                            //checks the next row for the final connection
                            if (rows + i+1 < boardSize) {
                                //checks that final space is an O and selects empty spot
                                if (getCell(rows +i+1 , cols) == CellStatus.O) {
                                    select(empRow, empCol);
                                    return true;
                                }
                            }
                            //checks that first row for the final connection
                            else{
                                //checks that final space is an O and selects empty spot
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
        //Returns false if the AI does not have a winning move
        return false;
    }

    /******************************************************************
     * This method checks if the player is going to win and selects a
     * space for the AI to block
     *
     * @return returns true if the AI can block
     *****************************************************************/
    public boolean canIBlock() {

        //Saves Empty Space column and row
        int empRow = 0;
        int empCol = 0;
        //counts empty spaces on the board
        int countEmpty = 0;
        //Used if when the board loops
        int firstCol = 0;
        int firstRow = 0;
        //Counts the number of Xs on the board
        int countX = 0;


        //Retrieves countToWin and boardSize from SuperTicTacToePanel
        int countToWin = SuperTicTacToePanel.getCountToWin() ;
        int boardSize = SuperTicTacToePanel.getBoardSize();

        //blocks consecutive Xs in rows
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Looks for first x
                if (getCell(rows, cols) == CellStatus.X) {
                    countX = 1;
                    firstCol = 0;
                    //counts Xs to see if player has one less than connections needed to win
                    for(int i=1; i<countToWin-1; i++ ) {
                        //checks the next column
                        if (cols + i < boardSize) {
                            if (getCell(rows, cols+i) == CellStatus.X) {
                                countX++;

                            }
                            //checks the first column if the next column does not exist
                        }else if (cols+i >= boardSize) {
                            if (getCell(rows, firstCol) == CellStatus.X) {
                                countX++;
                            }
                            firstCol++;
                        }
                        //If X has 1 less than the connection needed to win the AI will select a space to block
                        if(countX==countToWin-1) {
                            //checks the next column for an empty space
                            if (cols + i+1 < boardSize) {
                                //Selects a space to block if the cell is empty
                                if (getCell(rows, cols+i+1) == CellStatus.EMPTY) {
                                    select(rows, cols+i+1);
                                    return true;
                                }
                                //checks the first column if the next column does not exist
                            }else{
                                //Selects a space to block if the cell is empty
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
        //Blocks X in rows starting with an empty space
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks for empty spaces
                if (getCell(rows, cols) == CellStatus.EMPTY) {
                    countX=0;
                    firstCol = 0;
                    empRow = rows;
                    empCol = cols;
                    //Checks for consecutive X in rows.
                    for(int i=1; i<countToWin; i++ ) {
                        //checks to see the next row exists
                        if (cols + i < boardSize) {
                            // Increment countX if there is X in cell
                            if (getCell(rows, cols+i) == CellStatus.X) {
                                countX++;
                            }
                            //checks to see the next row exists
                        }else if (cols+i >= boardSize) {
                            // Increment countX if there is X in cell
                            if (getCell(rows, firstCol) == CellStatus.X) {
                                countX++;
                            }
                            // Increment firstCol if there is X in cell
                            firstCol++;
                        }
                        //Checks X has one less than the count needed to win
                        if(countX==countToWin-1) {
                            //checks to see the next row exists
                            if (cols + i < boardSize) {
                                //checks the final space for X and selects the empty space
                                if (getCell(rows, cols+i) == CellStatus.X) {
                                    select(empRow, empCol);
                                    return true;
                                }
                                //checks the first row if the next row does not exist
                            }else{
                                if (getCell(rows, firstCol-1) == CellStatus.X) {
                                    //checks the final space for X and selects the empty space
                                    select(empRow, empCol );
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        //blocks X in columns starting with an empty cell
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks for an empty cell
                if (getCell(rows, cols) == CellStatus.EMPTY) {
                    countX = 0;
                    firstRow = 0;
                    //Saves empty cell coordinates
                    empRow = rows;
                    empCol = cols;
                    //Checks for an consecutive Xs in columns
                    for(int i=1; i<countToWin; i++ ) {
                        //Checks if next row exists
                        if (rows + i < boardSize) {
                            //Increment count if cellStatus equals X
                            if (getCell(rows+i, cols) == CellStatus.X) {
                                countX++;
                            }
                            //If the next row does not exist goes to the first row
                        }else if (rows+i >= boardSize) {
                            //Increment count if cellStatus equals X
                            if (getCell(firstRow, cols ) == CellStatus.X) {
                                countX++;
                            }
                            firstRow++;
                        }
                        //Checks that X is one space from winning
                        if(countX==countToWin-1) {
                            //Checks if next row exists
                            if (rows + i < boardSize) {
                                //Selects the empty space if the cell status is X
                                if (getCell(rows + i, cols) == CellStatus.X) {
                                    select(empRow, empCol);
                                    return true;
                                }
                                //If the next row does not exist goes to the first row
                            }else{
                                //Selects the empty space if the cell status is X
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

        //blocks unconsecutive rows
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks the for X for all rows and columns
                if (getCell(rows, cols) == CellStatus.X) {
                    countX = 1;
                    firstCol = 0;
                    countEmpty = 0;
                    empRow=99;
                    empCol=99;
                    //checks for empty spaces and Xs
                    for(int i=1; i<countToWin-1; i++ ) {
                        //checks the next column if it exists
                        if (cols + i < boardSize) {
                            //increments the count for the number of Xs
                            if (getCell(rows, cols+i) == CellStatus.X) {
                                countX++;
                            }
                            //increments the count for the number of empty and save the position of the empty
                            if (getCell(rows, cols+i) == CellStatus.EMPTY) {
                                countEmpty++;
                                empRow = rows;
                                empCol = cols+i;
                            }
                            //checks the first column if the next column does not exist
                        }else if (cols+i >= boardSize) {
                            //increments the count for the number of Xs
                            if (getCell(rows, firstCol) == CellStatus.X) {
                                countX++;
                            }
                            //increments the count for the number of empty and save the position of the empty
                            if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                countEmpty++;
                                empRow = rows;
                                empCol = firstCol;
                            }
                            firstCol++;
                        }
                        //Checks that x has 2 less than the count needed to win and there is one empty spot
                        if(countX==countToWin-2 && countEmpty == 1) {
                            //Checks the next column if it exists
                            if (cols + i+1 < boardSize) {
                                //Selects the empty space if the final space is X
                                if (getCell(rows, cols+i+1) == CellStatus.X) {
                                    select(empRow, empCol);
                                    return true;
                                }
                            }
                            //Checks the fist column if the next collumn does not exist
                            else{
                                if (getCell(rows, firstCol) == CellStatus.X) {
                                    //Selects the empty space if the final space is X
                                    select(empRow, empCol);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }


        //blocks Xs for consecutive Xs in columns
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //Checks for an individual X for every row and column
                if (getCell(rows, cols) == CellStatus.X) {
                    countX = 1;
                    firstRow = 0;
                    //Checks that X has 1 less than the count needed to win
                    for(int i = 1; i<countToWin-1; i++ ) {
                        //checks the next row exists
                        if (rows + i < boardSize) {
                            //Increments countX if the cellStatus is X
                            if (getCell(rows+i, cols) == CellStatus.X) {
                                countX++;
                            }
                            //checks the first row, when the next row does not exist
                        }else if (rows+i >= boardSize) {
                            //Increments countX if the cellStatus is X
                            if (getCell(firstRow, cols) == CellStatus.X) {
                                countX++;
                            }
                            firstRow++;
                        }
                        //Checks that X has one less than the connections needed
                        if(countX==countToWin-1) {
                            //Checks the next row
                            if (rows + i+1 < boardSize) {
                                //select the cell if it is empty
                                if (getCell(rows+ i+1, cols) == CellStatus.EMPTY) {
                                    select(rows+i+1, cols);
                                    return true;
                                }
                                //Checks the first row if the next row does not exist
                            }else{
                                //select the cell if it is empty
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

        //blocks X in unconsecutive columns
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //checks all cells for X
                if (getCell(rows, cols) == CellStatus.X) {
                    countX = 1;
                    firstRow = 0;
                    countEmpty = 0;
                    empRow=99;
                    empCol=99;
                    //Checks for empty cells and Xs
                    for(int i=1; i<countToWin -1; i++ ) {
                        //checks the next row
                        if (rows + i < boardSize) {
                            //If x, increments the countX
                            if (getCell(rows+i, cols) == CellStatus.X) {
                                countX++;
                            }
                            //If empty, increments the count empty and saves location of empty cell
                            if (getCell(rows+i, cols) == CellStatus.EMPTY) {
                                countEmpty++;
                                empRow = rows+i;
                                empCol = cols;
                            }
                            //Checks the first row if the next row does not exist
                        }else if (rows+i >= boardSize) {
                            //If x, increments the countX
                            if (getCell(firstRow, cols) == CellStatus.X) {
                                countX++;
                            }
                            //If empty, increments the count empty and saves location of empty cell
                            if (getCell(firstRow, cols) == CellStatus.EMPTY) {
                                countEmpty++;
                                empRow = firstRow;
                                empCol = cols;
                            }
                            //Increments firstCol
                            firstRow++;
                        }
                        //If there is one empty space and 2 less connections than the count needed to win it checks the final space for X
                        if(countX == countToWin-2 && countEmpty == 1) {
                            //Checks the next row
                            if (rows + i+1 < boardSize) {
                                //Selects the empty location if the last cell is X
                                if (getCell(rows +i+1 , cols) == CellStatus.X) {
                                    select(empRow, empCol);
                                    return true;
                                }
                            }
                            //Checks the first row
                            else{
                                //Selects the empty location if the last cell is X
                                if (getCell(firstRow, cols) == CellStatus.X) {
                                    select(empRow, empCol);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        //Returns false if no move can be found
        return false;
    }

    /******************************************************************
     * This method checks for a move that could result in a possible
     * win for the AI.
     *
     * @return returns true if the AI can move to a beneficial
     * location
     *****************************************************************/
    public boolean smartMoveX() {

        //Gets countToWin and BoardSize from Smart Panel
        int countToWin = SuperTicTacToePanel.getCountToWin();
        int boardSize = SuperTicTacToePanel.getBoardSize();

        //Integers that help count the first row and column
        int firstCol = 0;
        int firstRow = 0;
        //Counts empty spaces and Os
        int smartCount = 0;

        //Random determines which spot the AI selects
        randMove = new Random(countToWin);

        //array that saves the coordinates of possible moves
        smartMoveRow = new int [100];
        smartMoveCol = new int [100];

        //Checks Rows for possible winning locations
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                if (getCell(rows, cols) == CellStatus.X || getCell(rows, cols) == CellStatus.EMPTY) {
                    // Save locations
                    smartMoveRow[0] = rows;
                    smartMoveCol[0] = cols;
                    firstCol = 0;

                    //Counts empty and Xs
                    smartCount = 1;

                    //Sets the array to a larger than possible amount, which prevents it from being randomly selected
                    if (getCell(rows, cols) == CellStatus.X) {
                        smartMoveRow[0] = 99;
                        smartMoveCol[0] = 99;

                    }
                    //check the for empty spaces or Xs
                    for (int i = 1; i < countToWin; i++) {
                        //Checks the next column
                        if (cols + i < boardSize) {
                            //check the for empty spaces or Xs
                            if (getCell(rows, cols + i) == CellStatus.X
                                    || getCell(rows, cols + i) == CellStatus.EMPTY) {
                                smartCount++;
                                //If the cell is X, sets the array to unreachable amount
                                if (getCell(rows, cols + i) == CellStatus.X) {

                                    smartMoveRow[i] = 99;
                                    smartMoveCol[i] = 99;

                                }
                                //If the cell is empty, records location in an array
                                if (getCell(rows, cols + i) == CellStatus.EMPTY) {

                                    smartMoveRow[i] = rows;
                                    smartMoveCol[i] = cols + i;

                                }
                            }
                            //Checks the first column if the next column does not exist
                        } else if (cols + i >= boardSize) {

                            if (getCell(rows, firstCol) == CellStatus.EMPTY
                                    || getCell(rows, firstCol) == CellStatus.X) {
                                smartCount++;
                                //If the cell is X, sets the array to unreachable amount
                                if (getCell(rows, firstCol) == CellStatus.X) {
                                    smartMoveRow[i] = 99;
                                    smartMoveCol[i] = 99;
                                }
                                //If the cell is empty, records location in an array
                                if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                    smartMoveRow[i] = rows;
                                    smartMoveCol[i] = firstCol;

                                }
                            }
                            firstCol++;
                        }
                        //If the count is equal to the connection then it selects a random empty location
                        if (smartCount == countToWin) {
                            //It tries 50 times to select a random spot
                            for (i = 0; i < 50; i++) {
                                int moveIndex = randMove.nextInt(countToWin);
                                // repeats if slot selected is not empty
                                if (smartMoveRow[moveIndex] != 99 && smartMoveCol[moveIndex] != 99) {

                                    select(smartMoveRow[moveIndex], smartMoveCol[moveIndex]);

                                    return true;
                                }

                            }
                        }
                    }
                }

            }
        }
        //Checks Columns for possible winning locations
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //check if the cell is an O or empty
                if (getCell(rows, cols) == CellStatus.X || getCell(rows, cols) == CellStatus.EMPTY) {
                    //Saves the location of empty cell
                    smartMoveRow[0] = rows;
                    smartMoveCol[0] = cols;
                    smartCount = 1;
                    firstRow = 0;
                    //Saves an unreachable location for cells with O
                    if (getCell(rows, cols) == CellStatus.X) {
                        smartMoveRow[0] = 99;
                        smartMoveCol[0] = 99;

                    }
                    //Checks consecutive rows for empty and Xs
                    for (int i = 1; i < countToWin; i++) {
                        //Checks if next row exists
                        if (rows + i < boardSize) {
                            //checks for empty and X and increments smartCount
                            if (getCell(rows + i, cols) == CellStatus.X
                                    || getCell(rows + i, cols) == CellStatus.EMPTY) {
                                smartCount++;
                                //If X ,Saves a unreachable location
                                if (getCell(rows + i, cols) == CellStatus.X) {
                                    smartMoveRow[i] = 99;
                                    smartMoveCol[i] = 99;
                                }
                                //If empty ,Saves the location of the empty
                                if (getCell(rows + i, cols) == CellStatus.EMPTY) {
                                    smartMoveRow[i] = rows + i;
                                    smartMoveCol[i] = cols;

                                }
                                //Goes to first row if next row does not exist
                            } else if (rows + i >= boardSize) {
                                //checks for empty and X and increments smartCount
                                if (getCell(firstRow, cols) == CellStatus.EMPTY
                                        || getCell(firstRow, cols) == CellStatus.X) {
                                    smartCount++;
                                    //If X , saves a unreachable location
                                    if (getCell(firstRow, cols) == CellStatus.X) {

                                        smartMoveRow[i] = 99;
                                        smartMoveCol[i] = 99;
                                    }
                                    //If empty ,Saves the location of the empty
                                    if (getCell(firstRow, cols) == CellStatus.EMPTY) {
                                        smartMoveRow[i] = firstRow;
                                        smartMoveCol[i] = cols;

                                    }

                                }
                                //increments firstRow
                                firstRow++;
                            }
                            //If the smartCount is equal to the number of connection then it selects a random empty space
                            if (smartCount == countToWin) {
                                // repeats if slot selected is not empty
                                for (i = 0; i < 25; i++) {
                                    int moveIndex = randMove.nextInt(countToWin);
                                    // repeats if smartMoveCol[moveIndex] == 99 (not empty)
                                    if (smartMoveRow[moveIndex] != 99 && smartMoveCol[moveIndex] != 99) {
                                        select(smartMoveRow[moveIndex], smartMoveCol[moveIndex]);
                                        return true;
                                    }

                                }
                            }
                        }
                    }

                }




            }
        }
        //returns false if no move was selected
        return false;
    }

    /******************************************************************
     * This method checks for a move that could result in a possible
     * win for the AI.
     *
     * @return returns true if the AI can move to a beneficial
     * location
     *****************************************************************/
    public boolean smartMoveO() {

        //Gets countToWin and BoardSize from Smart Panel
        int countToWin = SuperTicTacToePanel.getCountToWin();
        int boardSize = SuperTicTacToePanel.getBoardSize();

        //Integers that help count the first row and column
        int firstCol = 0;
        int firstRow = 0;
        //Counts empty spaces and Os
        int smartCount = 0;

        //Random determines which spot the AI selects
        randMove = new Random(countToWin);

        //array that saves the coordinates of possible moves
        smartMoveRow= new int [100];
        smartMoveCol= new int [100];

        //Checks Rows for possible winning locations
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                if (getCell(rows, cols) == CellStatus.O || getCell(rows, cols) == CellStatus.EMPTY) {
                    // Save locations
                    smartMoveRow[0] = rows;
                    smartMoveCol[0] = cols;
                    firstCol = 0;

                    //Counts empty and Os
                    smartCount = 1;

                    //Sets the array to a larger than possible amount, which prevents it from being randomly selected
                    if (getCell(rows, cols) == CellStatus.O) {
                        smartMoveRow[0] = 99;
                        smartMoveCol[0] = 99;

                    }
                    //check the for empty spaces or Os
                    for (int i = 1; i < countToWin; i++) {
                        //Checks the next column
                        if (cols + i < boardSize) {
                            //check the for empty spaces or Os
                            if (getCell(rows, cols + i) == CellStatus.O
                                    || getCell(rows, cols + i) == CellStatus.EMPTY) {
                                smartCount++;
                                //If the cell is O, sets the array to unreachable amount
                                if (getCell(rows, cols + i) == CellStatus.O) {

                                    smartMoveRow[i] = 99;
                                    smartMoveCol[i] = 99;

                                }
                                //If the cell is empty, records location in an array
                                if (getCell(rows, cols + i) == CellStatus.EMPTY) {

                                    smartMoveRow[i] = rows;
                                    smartMoveCol[i] = cols + i;

                                }
                            }
                            //Checks the first column if the next column does not exist
                        } else if (cols + i >= boardSize) {

                            if (getCell(rows, firstCol) == CellStatus.EMPTY
                                    || getCell(rows, firstCol) == CellStatus.O) {
                                smartCount++;
                                //If the cell is O, sets the array to unreachable amount
                                if (getCell(rows, firstCol) == CellStatus.O) {
                                    smartMoveRow[i] = 99;
                                    smartMoveCol[i] = 99;
                                }
                                //If the cell is empty, records location in an array
                                if (getCell(rows, firstCol) == CellStatus.EMPTY) {
                                    smartMoveRow[i] = rows;
                                    smartMoveCol[i] = firstCol;

                                }
                            }
                            firstCol++;
                        }
                        //If the count is equal to the connection then it selects a random empty location
                        if (smartCount == countToWin) {
                            //It tries 50 times to select a spot
                            for (i = 0; i < 50; i++) {
                                int moveIndex = randMove.nextInt(countToWin);
                                // repeats if slot selected is not empty
                                if (smartMoveRow[moveIndex] != 99 && smartMoveCol[moveIndex] != 99) {

                                    select(smartMoveRow[moveIndex], smartMoveCol[moveIndex]);

                                    return true;
                                }

                            }
                        }
                    }
                }

            }
        }
        //Checks Columns for possible winning locations
        for (int rows = 0; rows < boardSize; rows++) {
            for (int cols = 0; cols < boardSize; cols++) {
                //check if the cell is an O or empty
                if (getCell(rows, cols) == CellStatus.O || getCell(rows, cols) == CellStatus.EMPTY) {
                    //Saves the location of empty cell
                    smartMoveRow[0] = rows;
                    smartMoveCol[0] = cols;
                    smartCount = 1;
                    firstRow = 0;
                    //Saves an unreachable location for cells with O
                    if (getCell(rows, cols) == CellStatus.O) {
                        smartMoveRow[0] = 99;
                        smartMoveCol[0] = 99;

                    }
                    //Checks consecutive rows for empty and Os
                    for (int i = 1; i < countToWin; i++) {
                        //Checks if next row exists
                        if (rows + i < boardSize) {
                            //checks for empty and O and increments smartCount
                            if (getCell(rows + i, cols) == CellStatus.O
                                    || getCell(rows + i, cols) == CellStatus.EMPTY) {
                                smartCount++;
                                //If O ,Saves a unreachable location
                                if (getCell(rows + i, cols) == CellStatus.O) {
                                    smartMoveRow[i] = 99;
                                    smartMoveCol[i] = 99;
                                }
                                //If empty ,Saves the location of the empty
                                if (getCell(rows + i, cols) == CellStatus.EMPTY) {
                                    smartMoveRow[i] = rows + i;
                                    smartMoveCol[i] = cols;

                                }
                                //Goes to first row if next row does not exist
                            } else if (rows + i >= boardSize) {
                                //checks for empty and O and increments smartCount
                                if (getCell(firstRow, cols) == CellStatus.EMPTY
                                        || getCell(firstRow, cols) == CellStatus.O) {
                                    smartCount++;
                                    //If O , saves a unreachable location
                                    if (getCell(firstRow, cols) == CellStatus.O) {

                                        smartMoveRow[i] = 99;
                                        smartMoveCol[i] = 99;
                                    }
                                    //If empty ,Saves the location of the empty
                                    if (getCell(firstRow, cols) == CellStatus.EMPTY) {
                                        smartMoveRow[i] = firstRow;
                                        smartMoveCol[i] = cols;

                                    }

                                }
                                //increments firstRow
                                firstRow++;
                            }
                            //If the smartCount is equal to the number of connection then it selects a random empty space
                            if (smartCount == countToWin) {
                                // repeats if slot selected is not empty
                                for (i = 0; i < 25; i++) {
                                    int moveIndex = randMove.nextInt(countToWin);
                                    // repeats if smartMoveCol[moveIndex] == 99 (not empty)
                                    if (smartMoveRow[moveIndex] != 99 && smartMoveCol[moveIndex] != 99) {
                                        select(smartMoveRow[moveIndex], smartMoveCol[moveIndex]);
                                        return true;
                                    }

                                }
                            }
                        }
                    }

                }
            }
        }
        //returns false if no move was selected
        return false;
    }

    /******************************************************************
     * This method return the value of rememberChoice
     * which is what the user chose to go first, either X or O
     * if X was chosen, then rememberChoice is true
     *
     * @return rememberChoice what the user chose to go first
     *****************************************************************/
    public boolean isRememberChoice() {
        return rememberChoice;
    }
}