package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar {
    public static Position p;
    private static TETile[][] tiles;

    public static boolean backToMain = false;

    public static void Avatar(Position pos, TETile[][] tiless){
        p = pos;
        tiles = tiless;

        tiles[p.x][p.y] = Tileset.AVATAR;
    }

    public static void moveR(TETile[][] tiless){
        isSafeMove(new Position(p.x+1,p.y), tiless);
    }

    public static void moveL(TETile[][] tiless){
        isSafeMove(new Position(p.x-1,p.y), tiless);
    }

    public static void moveD(TETile[][] tiless){
        isSafeMove(new Position(p.x,p.y-1), tiless);
    }

    public static void moveU(TETile[][] tiless){
        isSafeMove(new Position(p.x,p.y+1), tiless);
    }

    public static void findAvatar(TETile[][] tiles) {
        for (int x = 0; x < Engine.WIDTH; x++) {
            for (int y = 0; y < Engine.HEIGHT; y++) {
                if (tiles[x][y] == Tileset.AVATAR) {
                    p = new Position(x, y);
                }
            }
        }
    }

    public static void isSafeMove(Position propP, TETile[][] tiles) {
        if(propP.y < 39 && propP.y > 0 && propP.x < 79 && propP.x > 0) {
            if (tiles[propP.x][propP.y] == Tileset.FLOOR || tiles[propP.x][propP.y] == Tileset.TREE) {
                Game.updateScore(propP, tiles);
                tiles[propP.x][propP.y] = Tileset.AVATAR;
                tiles[p.x][p.y] = Tileset.FLOOR;
                p = propP;
            } else if (tiles[propP.x][propP.y] == Tileset.LOCKED_DOOR) {
                Game.altRoom(tiles);
            } else if (tiles[propP.x][propP.y] == Tileset.UNLOCKED_DOOR) {
                backToMain = true;
            } else if (tiles[propP.x][propP.y]== Tileset.NOTHING) {
            }
        }
    }
}
