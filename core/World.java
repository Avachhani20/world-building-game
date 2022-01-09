package byow.Core;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class World implements Serializable {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GAME_DIR = new File(CWD + "DaGame");

    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

    public static TETile[][] tiles;

    public static Random RANDOM;

    private static LinkedList<Room> roomList;

    // Initializes our seed to be a random number and renders the board
    public static TETile[][] init(long SEED) {
        RANDOM = new Random(SEED);

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 3);

        roomList = new LinkedList<>();

        tiles = new TETile[WIDTH][HEIGHT];
        fillWithNothing(tiles);

        createWorld(tiles);
        Avatar.Avatar(placeAvatar(), tiles);

        Game game = new Game(tiles, 30);
        game.generateCoins();
        altRoomDoor();

        ter.renderFrame(tiles);

        return tiles;
    }

    // Initializes the board that will be used for the interactWithInputString command (does not render the board)
    public static TETile[][] initInputString(long SEED, TETile[][] world) {
        RANDOM = new Random(SEED);

        roomList = new LinkedList<>();

        fillWithNothing(world);

        createWorld(world);
        Avatar.Avatar(placeAvatar(), world);


        Game game = new Game(world, 25);
        game.generateCoins();

        return world;
    }

    // Initializes our new tileset for the "encounter" situation
    public static TETile[][] encounterInit(long SEED) {
        RANDOM = new Random(SEED);

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 3);

        roomList = new LinkedList<>();

        tiles = new TETile[WIDTH][HEIGHT];
        fillWithNothing(tiles);

        createEncounterWorld(tiles);
        Avatar.Avatar(placeAvatar(), tiles);

        Game game = new Game(tiles, 20);
        game.generateCoins();
        game.altRoomDoor2();

        ter.renderFrame(tiles);

        return tiles;
    }

    // Randomly places a "trap door" that will take the user to an alternate room
    public static void altRoomDoor() {
        //int r = Random(0, roomList.size());
        Room rr = roomList.get(1);
        Position p = new Position(rr.bottomLeft.x + 2, rr.bottomLeft.y + 2);
        tiles[p.x][p.y] = Tileset.LOCKED_DOOR;
    }

    // Creates our world for the encounter situation
    public static void createEncounterWorld(TETile[][] tiles) {
        createRoom(tiles, 10, 14, new Position(44, 20));
        //connectNewRoom(tiles, roomList.getLast());
    }

    // Places the avatar at a random position within any room in our world
    public static Position placeAvatar(){
        int r = Random(0, roomList.size());
        Room rr = roomList.get(r);
        return new Position(rr.bottomLeft.x + 1, rr.bottomLeft.y +1);
    }

    // Fills the world board with nothing
    public static void fillWithNothing(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    // Private helper class to keep track of rooms
    private static class Room {
        private final Position bottomLeft;

        private final int width;
        private final int height;

        Room(Position p, int w, int h) {
            this.width = w;
            this.height = h;
            this.bottomLeft = p;
        }
    }

    // Creates a random position
    public static Position randPos() {
        return new Position(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    }

    /** Main logic for connecting rooms
     * 1. Generate a randomly placed/sized room
     * 2. Generate a random number (to represent the number of rooms)
     * 3. Loop through the number of rooms: for each room, add a hallway that connects the new room to either another room or another hallway
     */
    public static void createWorld(TETile[][] tiles) {
        // Creates our first starting room at a random position
        createRandomRoom(tiles, randPos());

        // Initializes our random number representing the number of rooms we will eventually have
        int randNum = Random(10, 25);

        // Loops through our random number
        for (int i = 1; i < randNum; i++) {

            // Creates our next room
            createRandomRoom(tiles, randPos());
            connectNewRoom(tiles, roomList.getLast());
        }

        // Checks for unnecessary walls blocking off hallways
        checkWorld(tiles);
    }

    // Catch-all case to get rid of overlapping hallways
    public static void checkWorld(TETile[][] tiles) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {

                if (tiles[x][y] == Tileset.WALL && tiles[x+1][y] == Tileset.FLOOR && tiles[x-1][y] == Tileset.FLOOR) {
                    tiles[x][y] = Tileset.FLOOR;
                }
                else if (tiles[x][y] == Tileset.WALL && tiles[x][y+1] == Tileset.FLOOR && tiles[x][y-1] == Tileset.FLOOR) {
                    tiles[x][y] = Tileset.FLOOR;
                }

            }
        }
    }

    // Creates a room based off the given width, height, and position
    public static void createRoom(TETile[][] tiles, int width, int height, Position p) {
        for (int x = p.x; x < p.x + width; x++) {
            for (int y = p.y; y < p.y + height; y++) {
                if (x == p.x || x == p.x + width - 1 || y == p.y || y == p.y + height - 1) {
                    tiles[x][y] = Tileset.WALL;
                } else {
                    tiles[x][y] = Tileset.FLOOR;
                }
            }
        }
        // Adds the newly created room to our list of rooms
        Room room = new Room(p, width, height);
        roomList.addLast(room);
    }

    // Connects the most recently built room to an existing wall in our world
    public static void connectNewRoom(TETile[][] tiles, Room room) {
        if ((boolean) closestWall(tiles, room).get(0)) {
            placeHallway(tiles, room);
        } else {
            attachHall(tiles, room);
        }
    }

    // Creates the hallway that connects our new room to an existing wall
    public static void placeHallway(TETile[][] tiles, Room room) {
        // Checks to see whether it is a horizontal hallway or not
        if ((boolean) closestWall(tiles, room).get(1)) {
            Position startPos = (Position) distanceFromRoomY(tiles, room).get(1);

            // Checks to see whether the connection hallway is to the left of the room
            if ((boolean) distanceFromRoomY(tiles, room).get(2)) {
                buildHorizHall(tiles, startPos, (int) distanceFromRoomY(tiles, room).get(3));
            } else {
                buildHorizHall(tiles, new Position(room.bottomLeft.x + room.width, startPos.y), (int) distanceFromRoomY(tiles, room).get(3));
            }
        } else {
            Position startPos = (Position) distanceFromRoomX(tiles, room).get(1);
            if ((boolean) distanceFromRoomX(tiles, room).get(2)) {
                buildVertHall(tiles, startPos, (int) distanceFromRoomX(tiles, room).get(3));
            } else {
                buildVertHall(tiles, new Position(startPos.x, room.bottomLeft.y + room.height), (int) distanceFromRoomX(tiles, room).get(3));
            }
        }

    }

    /** Determines the closest vertically parallel wall from the room
     * Return value: ArrayList
     *      0) Whether there is a parallel wall or not
     *      1) The position of the closest wall
     *      2) Whether the closest wall is to the left of the room or not
     *      3) The distance between the closest wall and the room
     */
    public static ArrayList distanceFromRoomY(TETile[][] tiles, Room room) {
        ArrayList<Object> returnArray = new ArrayList<>();

        // Determines the closest parallel wall to the left of the room
        Position closestPosLeft = new Position(0, 0);
        for (int x = 0; x < room.bottomLeft.x; x++) {
            int count = 0;
            for (int y = room.bottomLeft.y; y < room.bottomLeft.y + room.height; y++) {
                if (tiles[x][y] == Tileset.WALL) {
                    count++;
                    if (count == 3) {
                        closestPosLeft = new Position(x, y - 2);
                    }
                } else {
                    count = 0;
                }
            }
        }

        // Determines the closest parallel wall to the right of the room
        Position closestPosRight = new Position(0, 0);
        for (int x = WIDTH - 1; x > room.bottomLeft.x + room.width - 1; x--) {
            int count = 0;
            for (int y = room.bottomLeft.y; y < room.bottomLeft.y + room.height; y++) {
                if (tiles[x][y] == Tileset.WALL) {
                    count++;
                    if (count == 3) {
                        closestPosRight = new Position(x, y - 2);
                    }
                } else {
                    count = 0;
                }
            }
        }

        if (closestPosLeft.equalTo(new Position(0, 0)) && closestPosRight.equalTo(new Position(0, 0))) {
            returnArray.add(false);
        } else if (closestPosLeft.equalTo(new Position(0, 0))) {
            returnArray.add(true);
            returnArray.add(closestPosRight);
            returnArray.add(false);
            returnArray.add(closestPosRight.x - (room.bottomLeft.x + room.width - 1));
        } else if (closestPosRight.equalTo(new Position(0, 0))) {
            returnArray.add(true);
            returnArray.add(closestPosLeft);
            returnArray.add(true);
            returnArray.add(room.bottomLeft.x - closestPosLeft.x);
        } else if (room.bottomLeft.x - closestPosLeft.x < closestPosRight.x - (room.bottomLeft.x + room.width)) {
            returnArray.add(true);
            returnArray.add(closestPosLeft);
            returnArray.add(true);
            returnArray.add(room.bottomLeft.x - closestPosLeft.x);
        } else {
            returnArray.add(true);
            returnArray.add(closestPosRight);
            returnArray.add(false);
            returnArray.add(closestPosRight.x - (room.bottomLeft.x + room.width - 1));
        }

        return returnArray;
    }

    /** Determines the closest horizontally parallel wall from the room
     * Return value: ArrayList
     *      0) Whether there is a parallel wall or not
     *      1) The position of the closest wall
     *      2) Whether the closest wall is below the room or not
     *      3) The distance between the closest wall and the room
     *
     */
    public static ArrayList distanceFromRoomX(TETile[][] tiles, Room room) {
        ArrayList<Object> returnArray = new ArrayList<>();

        // Determines the closest parallel wall below the room
        Position closestPosBottom = new Position(0, 0);
        for (int y = 0; y < room.bottomLeft.y; y++) {
            int count = 0;
            for (int x = room.bottomLeft.x; x < room.bottomLeft.x + room.width; x++) {
                if (tiles[x][y] == Tileset.WALL) {
                    count++;
                    if (count == 3) {
                        closestPosBottom = new Position(x - 2, y);
                    }
                } else {
                    count = 0;
                }
            }
        }

        // Determines the closest parallel wall above the room
        Position closestPosTop = new Position(0, 0);
        for (int y = HEIGHT - 1; y > room.bottomLeft.y + room.height - 1; y--) {
            int count = 0;
            for (int x = room.bottomLeft.x; x < room.bottomLeft.x + room.width; x++) {
                if (tiles[x][y] == Tileset.WALL) {
                    count++;
                    if (count == 3) {
                        closestPosTop = new Position(x - 2, y);
                    }
                } else {
                    count = 0;
                }
            }
        }

        if (closestPosBottom.equalTo(new Position(0, 0)) && closestPosTop.equalTo(new Position(0, 0))) {
            returnArray.add(false);
        } else if (closestPosBottom.equalTo(new Position(0, 0))) {
            returnArray.add(true);
            returnArray.add(closestPosTop);
            returnArray.add(false);
            returnArray.add(closestPosTop.y - (room.bottomLeft.y + room.height - 1));
        } else if (closestPosTop.equalTo(new Position(0, 0))) {
            returnArray.add(true);
            returnArray.add(closestPosBottom);
            returnArray.add(true);
            returnArray.add(room.bottomLeft.y - closestPosBottom.y);
        } else if (room.bottomLeft.y - closestPosBottom.y < closestPosTop.y - (room.bottomLeft.y + room.height)) {
            returnArray.add(true);
            returnArray.add(closestPosBottom);
            returnArray.add(true);
            returnArray.add(room.bottomLeft.y - closestPosBottom.y);
        } else {
            returnArray.add(true);
            returnArray.add(closestPosTop);
            returnArray.add(false);
            returnArray.add(closestPosTop.y - (room.bottomLeft.y + room.height - 1));
        }

        return returnArray;
    }

    /** Determines which parallel wall in the world to connect to
     * Return value: ArrayList
     *      0) Boolean: whether or not the room is parallel to a wall
     *      1) Boolean: whether or not we need to build a horizontal wall
     */
    public static ArrayList closestWall(TETile[][] tiles, Room room) {
        ArrayList<Boolean> returnArray = new ArrayList<>();

        // Checks to see if there are no horizontally and/or vertically parallel walls to the room
        if ((boolean) distanceFromRoomX(tiles, room).get(0) == false && (boolean) distanceFromRoomY(tiles, room).get(0) == false) {
            returnArray.add(false);
            return returnArray;
        } else if ((boolean) distanceFromRoomX(tiles, room).get(0) == false) {
            returnArray.add(true);
            returnArray.add(true);
            return returnArray;
        } else if ((boolean) distanceFromRoomY(tiles, room).get(0) == false) {
            returnArray.add(true);
            returnArray.add(false);
            return returnArray;
        }

        returnArray.add(true);

        // Checks to see if the shortest connection will happen with a horizontal or vertical hallway
        if ((int) distanceFromRoomY(tiles, room).get(3) > (int) distanceFromRoomX(tiles, room).get(3)) {
            returnArray.add(false);
        } else {
            returnArray.add(true);
        }

        return returnArray;
    }

    public static void createRandomRoom(TETile[][] tiles, Position p){
        createRandomRoom(tiles, p, 0);
    }

    // Creates a room with a given position but random width and height
    public static void createRandomRoom(TETile[][] tiles, Position p, int count) {
        int height = Random(7,10);
        int width = Random(7,10);
        if (count > 100){

        }
        else if (width + p.x < WIDTH && height + p.y < HEIGHT && isClean(tiles, width, height, p)) {
            createRoom(tiles, width, height, p);
        } else {
            createRandomRoom(tiles, randPos(), count + 1);
        }
    }

    // Check to see if the board at where you would like to place the room/hallway is occupied
    public static boolean isClean(TETile[][] tiles, int width, int height, Position p) {
        for (int i = p.x; i < p.x + width; i++) {
            for (int j = p.y; j < p.y + height; j++) {
                if (tiles[i][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int xdiff(Room roomLeft, Room roomRight) {
        return roomRight.bottomLeft.x - roomLeft.width - roomLeft.bottomLeft.x;
    }

    public static int ydiff(Room roomTop, Room roomBottom) {
        return roomTop.bottomLeft.y - roomBottom.height - roomBottom.bottomLeft.y;
    }

    public static void topRight(TETile[][] tiles, Position p) {
        tiles[p.x][p.y+1] = Tileset.FLOOR;
        tiles[p.x + 1][p.y] = Tileset.FLOOR;
        tiles[p.x+2][p.y+1] = Tileset.WALL;
    }

    public static void topLeft(TETile[][] tiles, Position p) {
        tiles[p.x + 1][p.y] = Tileset.FLOOR;
        tiles[p.x][p.y + 1] = Tileset.WALL;
    }

    public static void attachHall(TETile[][] tiles, Room newRoom){
        int ind = RANDOM.nextInt(roomList.size()-1);
        turns(tiles, newRoom, roomList.get(ind));
    }

    public static int Random(int lower, int upper) {
        int temp = RANDOM.nextInt(upper);
        if (temp >= lower) {
            return temp;
        }
        else {
            return Random(lower, upper);
        }

    }

    public static void turns(TETile[][] tiles, Room roomNew, Room roomRand) {
        //new to the left
        if (roomNew.bottomLeft.x - roomRand.bottomLeft.x < 0) {
            int hallLength = xdiff(roomNew, roomRand) + Random(2, roomRand.width - 2) + 1;
            //new on bottom
            if (roomNew.bottomLeft.y - roomRand.bottomLeft.y < 0) {
                //case 3
                int hallHeight = ydiff(roomRand, roomNew) + Random(2,roomRand.height - 2);
                Position vs = new Position(roomRand.bottomLeft.x - hallLength, roomNew.bottomLeft.y + roomNew.height - 1);
                buildVertHall(tiles, vs, hallHeight);
                Position hs = new Position(vs.x, vs.y + hallHeight);
                buildHorizHall(tiles, hs, hallLength);
                topLeft(tiles, hs);

            }
            //new on tip
            else {
                //case 1
                int hallHeight = ydiff(roomNew, roomRand) + Random(2,roomNew.height - 2) + 2;
                Position hs = new Position(roomNew.bottomLeft.x + roomNew.width, roomRand.bottomLeft.y + roomRand.height + hallHeight - 2);
                buildHorizHall(tiles, hs, hallLength);
                Position vs = new Position(hs.x + hallLength - 3, roomRand.bottomLeft.y + roomRand.height - 1);
                buildVertHall(tiles, vs, hallHeight);
                topRight(tiles, new Position(vs.x, hs.y));

            }
        }
        //new to the right
        else {
            int hallLength = xdiff(roomRand, roomNew) + Random(2,roomNew.width - 2) + 2;
            //new on bottom
            if (roomNew.bottomLeft.y-roomRand.bottomLeft.y < 0) {
                //case 2
                int hallHeight = ydiff(roomRand, roomNew) + Random(2,roomRand.height-2) +1;
                Position hs = new Position(roomRand.bottomLeft.x + roomRand.width - 1, roomNew.bottomLeft.y + roomNew.height +hallHeight - 1);
                buildHorizHall(tiles, hs, hallLength);
                Position vs = new Position(hs.x + hallLength - 3, roomNew.bottomLeft.y + roomNew.height - 1);
                buildVertHall(tiles, vs, hallHeight);

                topRight(tiles, new Position(vs.x, hs.y));

            }
            //new on tip
            else{
                //case 4
                int hallHeight = ydiff(roomNew, roomRand) + Random(2,roomNew.height - 2) + 2;
                Position vs = new Position(roomNew.bottomLeft.x - hallLength + 1, roomRand.bottomLeft.y + roomRand.height - 1);
                buildVertHall(tiles, vs, hallHeight);
                Position hs = new Position(vs.x, vs.y + hallHeight - 3);
                buildHorizHall(tiles, hs, hallLength);
                topLeft(tiles, hs);

            }
        }
    }

    // Builds a non-random horizontal wall
    public static void buildHorizHall(TETile[][] tiles, Position p, int width) {
        int height = 3;

        for (int x = p.x; x < p.x + width; x++) {
            for (int y = p.y; y < p.y + height; y++) {
                if (y == p.y || y == p.y + height - 1) {
                    tiles[x][y] = Tileset.WALL;
                } else {
                    tiles[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    // Builds a non-random vertical wall
    public static void buildVertHall(TETile[][] tiles, Position p, int height) {
        int width = 3;

        for (int x = p.x; x < p.x + width; x++) {
            for (int y = p.y; y < p.y + height; y++) {
                if (x == p.x || x == p.x + width - 1) {
                    tiles[x][y] = Tileset.WALL;
                } else {
                    tiles[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    // Main function that calls the interact function in Engine
    public static void main(String[] args) {
        Engine.interactWithInputString(args[0]);
    }
}
