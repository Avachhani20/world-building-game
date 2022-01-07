package byow.Core;

public class Position {
    public int x;
    public int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equalTo(Position n){
        if (x == n.x && y == n.y) {
            return true;
        }
        return false;
    }
}

