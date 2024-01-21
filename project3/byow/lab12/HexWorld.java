package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;
    private static final long SEED = 1145141;
    private static final Random RANDOM = new Random(SEED);

    //简单画了一行
    public static void drawRow(TETile[][] tiles,Position p,TETile tile,int length)
    {
        for(int dx = 0;dx < length;dx++)
        {
            tiles[p.x + dx][p.y] = tile;
        }

    }
    public static void addHexagon_helper(TETile[][] tiles,Position p,TETile tile,int b,int t)
    {
        //上y
        //|
        //|-----右x
        Position startOfRow = p.shift(b,0);//偏移量
        drawRow(tiles,startOfRow,tile,t);//第一次画
        //这里和python的实现不一样的是
        //我们需要处理下一行的东西
        if(b > 0)
        {
            Position nextP = p.shift(0,-1);
            addHexagon_helper(tiles,nextP,tile,b - 1,t + 2);
        }
        //这里没有严格的位置限制，上下左右是可以随便移动的
        Position startOfReflectionRow = p.shift(b,-(2 * b + 1));
        //回溯回来画
        drawRow(tiles,startOfReflectionRow,tile,t);

    }

    //这里就画六边形得过程
    public static void addHexagon(TETile[][] tiles,Position p,TETile tile,int size)
    {
        if(size < 2)
            return;
        addHexagon_helper(tiles,p,tile,size - 1,size);
    }

    public static void addHexColumn(TETile[][] tiles,Position p,int size,int num)
    {
        if(num < 1)
            return;
        addHexagon(tiles,p,randomBiome(),size);
        if(num > 1)
        {
            Position bottomNeighbor = getBottomNeighbor(p,size);
            addHexColumn(tiles,bottomNeighbor,size,num - 1);
        }

    }

    private static class Position//定义一个位置类方便我们进行后序得操作
    {
        int x;
        int y;
        Position(int x_,int y_)
        {
            this.x = x_;
            this.y = y_;
        }
        public Position shift(int dx,int dy)
        {
            return new Position(this.x + dx,this.y + dy);
        }

    }
    public static Position getBottomNeighbor(Position p,int n)//其中n是第一行t的数目//这样就能很好的处理
    {
        return p.shift(0,-2 * n);
    }
    public static Position getTopRightNeighbor(Position p,int n)
    {
        return p.shift(2 * n - 1,n);
    }

    public static Position getBottomRightNeighbor(Position p,int n)
    {
        return p.shift(2 * n - 1,-n);
    }

    public static void fillWithNothing(TETile[][] tiles)
    {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static TETile randomBiome() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            case 5: return Tileset.WATER;
            case 6: return Tileset.GRASS;
            default: return Tileset.NOTHING;
        }
    }
    public static void drawWorld(TETile[][] world,Position p,int hexSize,int tessSize)
    {
        addHexColumn(world,p,hexSize,tessSize);
        for(int i = 1;i < tessSize;i++)
        {
            p = getTopRightNeighbor(p,hexSize);
            addHexColumn(world,p,hexSize,tessSize + i);
        }
        for(int i = tessSize - 2;i >= 0;i--)
        {
            p = getBottomRightNeighbor(p,hexSize);
            addHexColumn(world,p,hexSize,tessSize + i);
        }
    }
    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        Position anchor = new Position(10,21);
        drawWorld(world,anchor,2,3);
        // fills in a block 14 tiles wide by 4 tiles tall

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
