package com.Logic;

import java.util.ArrayList;

// The entire state of the game at one point in the match
public class GameState {
    // row 0-7, column 0-7
    private Piece[][] board;
    // captured pieces
    private ArrayList<Piece> captured;

    // get the piece (or the lack of one) at a square on the board
    public Piece getSquare(int row, int col) {
        return board[row][col];
    }

    // displace a piece on the board to another location
    // TODO: implement this
    public void displace(int rowSrc, int colSrc, int rowDst, int colDst) {

    }

    // move a piece on the board to another location
    // TODO: implement this
    public void move(int rowSrc, int colSrc, int rowDst, int colDst) {

    }

    // capture a piece on the board
    // TODO: implement this
    public void capture(int row, int col) {

    }

    // change the color of a piece on the board
    // TODO: implement this
    public void changeColor(int row, int col) {
        
    }
}
