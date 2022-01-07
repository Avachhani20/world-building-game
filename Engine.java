package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.LinkedList;
import java.util.Random;

import static byow.Core.Utils.readContentsAsString;
import static java.lang.Character.isDigit;

public class Engine {
    public static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

    public static Window windowMain = new Window();
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public Engine() {

    }

    public static void interactWithKeyboard() {
        windowMain.menu();
        TETile[][] world;

        boolean chosen = false;
        while (!chosen) {
            if(StdDraw.isKeyPressed(78)) {
                chosen = true;
                Window.newGame();
                Window.solicitNCharsInput();
                world = Window.gameBoard();
                inter(world);
            } else if (StdDraw.isKeyPressed(76)) {
                Window.see = Window.read().substring(0, Window.read().length()-2);
                world = interactWithInputString(Window.read());
                inter(world);
                break;
            } else if (StdDraw.isKeyPressed(81)) {
                System.exit(0);
            }
        }

    }

    public static TETile[][] inter(TETile[][] world) {
        //System.out.println("yuh");
        while (Game.score < 50) {
            //System.out.println("inside score check");
            while (StdDraw.hasNextKeyTyped()) {
                char nextChar = Character.toLowerCase(StdDraw.nextKeyTyped());
                Window.handleInput(nextChar, world);
                ter.renderFrame(world);
            }
            Window.headsUpDisplay(world);
            StdDraw.pause(10);
        }
        Window.endGame();
        StdDraw.pause(200);
        return world;
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] interactWithInputString(String input) {
        return interactWithInputString(input, false);
    }

    public static TETile[][] interactWithInputString(String input, Boolean cont) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        // Converts the input string to lowercase to make checking for commands easier
        String inputLower = input.toLowerCase();

        // Initializes the seed variable
        long seed;

        // Initializes the world board
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        // Initializes the renderer
        //ter.initialize(WIDTH, HEIGHT+3);

        // Iterates through each character in the lowercase input string
        String done = "";
        for (int i = 0; i < inputLower.length(); i++) {
            char indexChar = inputLower.charAt(i);
            done += indexChar;
            switch(indexChar) {
                case 'n':
                    seed = getSeed(inputLower);
                    world = World.initInputString(seed, world);
                    String seedStr = String.valueOf(seed);
                    i += seedStr.length() + 1;
                    break;
                case 'w':
                    Avatar.moveU(world);
                    break;
                case 'a':
                    Avatar.moveL(world);
                    break;
                case 's':
                    Avatar.moveD(world);
                    break;
                case 'd':
                    Avatar.moveR(world);
                    break;
                case ':':
                    if (inputLower.charAt(i + 1) == 'q') {
                        if (cont) {
                            inter(world);
                        }
                        else {
                            if (Game.score > 25) {
                                StdDraw.pause(2000);
                                return world;
                            } else {
                                Window.save();
                            }
                        }
                    }
                    i += 1;
                    break;
            }
        }

        return world;
    }

    // Recognizes the numbers in the input string and converts the string of numbers to a Long
    public static long getSeed(String input) {
        String seed = "";
        for (int i = 0; i < input.length(); i++) {
            char command = input.charAt(i);
            if (isDigit(command)) {
                seed += command;
            }
        }

        return Long.parseLong(seed);
    }
}