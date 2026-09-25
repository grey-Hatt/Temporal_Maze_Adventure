package oneLevel;

public class Player {
    private int row, col;
    public Player(int r, int c) { row = r; col = c; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setPosition(int r, int c) { row = r; col = c; }
}
