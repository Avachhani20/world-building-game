package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.Tileset;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;

public class Game {
    public static int numCoins;
    private static TETile[][] tiles;

    public static int score = 0;

    public Game(TETile[][] world, int num) {
        numCoins = num;
        tiles = world;
    }

    public static void altRoomDoor2() {
        Position p = randomPos();
        tiles[p.x][p.y] = Tileset.UNLOCKED_DOOR;
    }

    // Creates the new alternate room runs the game in the new room
    public static void altRoom(TETile[][] world) {
        long seed = Engine.getSeed(Engine.windowMain.see);
        TETile[][] encounterWorld = World.encounterInit(seed);

        StdDraw.clear(Color.BLACK);
        Engine.windowMain.ter.renderFrame(encounterWorld);

        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                char nextChar = Character.toLowerCase(StdDraw.nextKeyTyped());
                Engine.windowMain.handleInput(nextChar, encounterWorld);
                Engine.windowMain.ter.renderFrame(encounterWorld);
            }
            Engine.windowMain.headsUpDisplay(encounterWorld);
            StdDraw.pause(10);

            if (Avatar.backToMain) {
                Avatar.findAvatar(world);
                break;
            }
        }

        StdDraw.clear(Color.BLACK);
        Engine.windowMain.ter.renderFrame(world);
        Engine.inter(world);
    }

    // Randomly places 25 coins all over the board
    public static void generateCoins() {
        for (int i = 0; i < numCoins; i++) {
            Position p = randomPos();
            tiles[p.x][p.y] = Tileset.TREE;
        }
    }

    // Adds to score if the avatar "collects" a coin
    public static void updateScore(Position p, TETile[][] tile) {
        if (tile[p.x][p.y] == Tileset.TREE) {
            score += 1;
        }
    }

    // Generates a random position which is currently a floor tile
    public static Position randomPos() {
        Position p = new Position(0, 0);
        while (tiles[p.x][p.y] != Tileset.FLOOR) {
            p = new Position(World.RANDOM.nextInt(World.WIDTH), World.RANDOM.nextInt(World.HEIGHT));
        }
        return p;
    }
}
