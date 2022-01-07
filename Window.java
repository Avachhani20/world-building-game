package byow.Core;

import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import byow.TileEngine.TERenderer;
import byow.TileEngine.Tileset;
import byow.TileEngine.TETile;

import static byow.Core.Utils.writeContents;
import static byow.Core.Utils.readContentsAsString;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Window {

    private static int height = 43;
    private static int width = 80;
    public static Font fontBig = new Font("Monaco", Font.BOLD, 30);
    public static Font fontSmall = new Font("Monaco", Font.BOLD, 15);
    public static String see = "";
    public static TERenderer ter = new TERenderer();

    public static String currTile = "";

    // Initializes our window
    public Window() {
        StdDraw.setCanvasSize(width * 16, height * 16);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    // Displays the main menu with options for the user --> ACTIVE ONLY
    public static void menu(){
        StdDraw.setFont(fontBig);
        StdDraw.text(40,24,"CS 61B B&A GAME");
        StdDraw.setFont(fontSmall);
        StdDraw.text(40, 19, "Collect all the coins to win the game!");
        StdDraw.text(40, 17, "Important: you may need to hit a wall at the beginning of the game IF motion functionality is not working.");
        StdDraw.text(40,13, "New Game (N)");
        StdDraw.text(40,11, "Load Game (L)");
        StdDraw.text(40,9, "Quit Game (Q)");
        StdDraw.show();
    }

    // Displays the window asking for the user to input a seed
    public static void newGame(){
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(fontBig);
        StdDraw.text(40,30,"ENTER SEED");
        StdDraw.show();
        StdDraw.text(40,23,see);
        StdDraw.show();

    }

    // Displays the window for the end of the game
    public static void endGame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(fontBig);
        StdDraw.text(40,20,"YOU WIN");
        StdDraw.show();
    }

    // Continues displaying the user input in the seed window until an 's' is typed
    public static boolean solicitNCharsInput() {
        while(!see.contains("s") && !see.contains("S")) {
            if (StdDraw.hasNextKeyTyped()) {
                see += StdDraw.nextKeyTyped();
                newGame();
            }
        }
        return false;
    }

    // Creates the game board with the appropriate world for the inputted seed
    public static TETile[][] gameBoard() {
        String seed = see.substring(1,see.length()-1);
        TETile[][] world = World.init(Long.parseLong(seed));
        ter.renderFrame(world);
        return world;
    }

    // Displays the current score
    public static void scoreDisplay() {
        StdDraw.setFont(fontSmall);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width / 2, height - 1.5, "Score: " + Game.score);
    }

    // Displays the current time and date
    public static void timeDisplay() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        StdDraw.textRight(width - 1, height - 1.5, "Currently: " + sdf.format(date));
    }

    // Creates and initializes the timer
    public static void timer() {

    }

    public static void save() {
        writeContents(World.GAME_DIR, see);
    }

    public static String read() {
        return readContentsAsString(World.GAME_DIR);
    }

    public static void handleInput(char key, TETile[][] tils) {
        see +=  key;
        if (see.contains(":q") || see.contains(":Q")) {
            save();
            System.exit(0);
        } else if (StdDraw.isKeyPressed(87)) {
            Avatar.moveU(tils);
        } else if (StdDraw.isKeyPressed(83)) {
            Avatar.moveD(tils);
        } else if (StdDraw.isKeyPressed(65)) {
            Avatar.moveL(tils);
        } else if (StdDraw.isKeyPressed(68)) {
            Avatar.moveR(tils);
        }
    }

    // Adds and shows different elements in our heads up display
    public static void headsUpDisplay(TETile[][] world) {
        int mouseX = (int) (StdDraw.mouseX());
        int mouseY = (int) (StdDraw.mouseY());

        String tileType;
        if (mouseX > width - 1 || mouseY > height - 4 || mouseX < 1 || mouseY < 1) {
            tileType = "void";
        } else if (world[mouseX][mouseY] == Tileset.WALL) {
            tileType = "wall";
        } else if (world[mouseX][mouseY] == Tileset.FLOOR) {
            tileType = "floor";
        } else if (world[mouseX][mouseY] == Tileset.AVATAR) {
            tileType = "avatar";
        } else if (world[mouseX][mouseY] == Tileset.TREE) {
            tileType = "coin";
        } else {
            tileType = "void";
        }

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(width / 2, height - 1.5, width / 2, 1.5);
        StdDraw.show();
        StdDraw.setFont(fontSmall);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, height - 1.5, tileType);
        scoreDisplay();
        timeDisplay();
        StdDraw.show();
        currTile = tileType;
    }

    public static void main(String[] args) {
        Engine.interactWithKeyboard();
    }
}