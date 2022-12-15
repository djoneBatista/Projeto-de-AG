package hibrido;

public class Padrao {
    //Data 

    protected boolean grid[][];
    protected char item;
    //Constructor

    public Padrao(char item, int w, int h) {
        grid = new boolean[w][h];
        this.item = item;
    }
    //Functions

    public void setData(int x, int y, boolean v) {
        grid[x][y] = v;
    }

    public boolean getData(int x, int y) {
        return grid[x][y];
    }

    public void clear() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y] = false;
            }
        }
    }
    //Getting the size of our grid: which is 7*5

    public int getHeight() {
        return grid[0].length;
    }

    public int getWidth() {
        return grid.length;
    }

    public char getitem() {
        return item;
    }

    public void setitem(char item) {
        this.item = item;
    }
}
