package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.TETileWrapper;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class WorldGenerator implements Serializable
{
    private static final int RoomNum = 35;
    private final int N;

    private Random RANDOM;
    private Long seed;

    private TETileWrapper[][] worldWrappers;

    private final int width;

    private final int height;


    //这里使用A* 算法搭建的结构，我看看它是咋搭建的
    private int source;
    private int target;
    private final int[] edgeTo;
    private final int[] distTo;

    private boolean targetFound;

    private boolean isFirst;

    private boolean alwaysCenterTarget;

    private TETileWrapper avatar;

    private LinkedList<Room> rooms = new LinkedList<>();

    private boolean turn = true;
    private LinkedList<Room> randomRooms = new LinkedList<>();

    public WorldGenerator(Long seed,TETile[][] world,boolean alwaysCenterTarget)
    {
        this.seed = seed;
        this.RANDOM = new Random(seed);
        this.width = world.length;
        this.height = world[0].length;
        this.edgeTo = new int[V()];
        this.distTo = new int[V()];
        this.targetFound = false;
        this.N = Math.max(width,height);
        this.worldWrappers = new TETileWrapper[width][height];
        for(int x = 0;x < width;x++)
        {
            for(int y = 0;y < height;y++)
            {
                worldWrappers[x][y] = new TETileWrapper(Tileset.NOTHING,x,y);
            }
        }
        reset();
        this.source = 0;
        setTarget(width/2,height/2);
        this.isFirst = true;
        this.alwaysCenterTarget = alwaysCenterTarget;
    }



    public TETile[][] generateWorld()
    {
        connnectRooms();//将房间连起来
        fillSomeWalls();//加一些额外的墙
        randomAvatar();//创立主角
        createRandomLightInRooms();//在房间中随机的创立一些灯光
        return getWorldByWorldWrappers();
    }

    public TETile[][] moveAvatarThenGenerateWorld(String direction)
    {
        moveAvatar(direction);//移动主人公的位置
        return getWorldByWorldWrappers();
    }
    private TETile[][] getWorldByWorldWrappers()//仅仅生成一个有瓷砖的世界
            //相当于重新初始化砖块
    {
        TETile[][] world = new TETile[width][height];
        for(int x = 0;x < width;x++)
        {
            for(int y = 0;y < height;y++)
            {
                world[x][y] = worldWrappers[x][y].getTile();
            }
        }
        return world;
    }
    private void connnectRooms()
    {
        //到底是咋将随机生成的房间连接起来的
        for(int i = 0;i < RoomNum;i++)
        {
            //一共35个房间看看咋弄得
            Room room = new Room(worldWrappers,i,seed);
            rooms.add(room);
            room.makeRoom();
            reset();
            connectRoomToTarget(room);
        }
    }
    private void connectRoomToTarget(Room room) {//两种不同的连接方式
        //一种是直接朝着中心
        if (alwaysCenterTarget) {
            connectRoomToTargetByCenterTarget(room);
        } else {//另一种为不直接朝着中心
            connectRoomToTargetByRandomTarget(room);
        }
    }
    private void connectRoomToTargetByCenterTarget(Room room) {
        targetFound = false;
        TETileWrapper randomExit = room.getRandomExitByDoorsInRoom();//随机找一个出口
        setSource(randomExit.getX(), randomExit.getY());
        astar();//astar算法寻路
        buildHallwayByShortestPath(target);//获得最短路
    }

    private void connectRoomToTargetByRandomTarget(Room room) {
        if (isFirst) {
            setFirstTargetAndSource(room);
            astar();
        } else {
            targetFound = false;
            setRandomTargetAndSource(room);
            astar();
        }
        buildHallwayByShortestPath(target);
    }

    private void setFirstTargetAndSource(Room room) {
        // set first target
        int randomNum = RANDOM.nextInt((width - 2) * (height - 2));
        int num = 0;
        // don't choose target as floor in limbo
        for (int x = 1; x < width - 1; x += 1) {
            for (int y = 1; y < height - 1; y += 1 ) {
                if (num == randomNum) {
                    setTarget(x, y);
                    // you can't mark target by markTile() in this method
                    worldWrappers[x][y].setTile(Tileset.FLOOR);
                    isFirst = false;
                    // set first source
                    TETileWrapper randomExit = room.getRandomExitByDoorsInRoom();
                    setSource(randomExit.getX(), randomExit.getY());
                    return;
                }
                num += 1;
            }
        }
    }

    private void setRandomTargetAndSource(Room room){
        LinkedList<TETileWrapper> notRoomButFloors = notRoomButFloors();
        LinkedList<TETileWrapper> exits = room.getExitsInRoom();
        int randomNum1, randomNum2;
        // set random target
        randomNum1 = RANDOM.nextInt(notRoomButFloors.size());//随机选一块
        TETileWrapper tileWrapper = notRoomButFloors.get(randomNum1);
        setTarget(tileWrapper.getX(), tileWrapper.getY());
        // set random source
        randomNum2 = RANDOM.nextInt(exits.size());//在路上随便找一块作为起点
        TETileWrapper randomExit = exits.get(randomNum2);
        int x = randomExit.getX();
        int y = randomExit.getY();
        setSource(x, y);
        // set door in room as floor by exit
        room.setDoorAsFloorByExitInRoom(x, y);
    }

    private LinkedList<TETileWrapper> notRoomButFloors() {//取出那些个不是房间内部的地板
        LinkedList<TETileWrapper> notRoomButFloors = new LinkedList<>();
        for (int x = 1; x < width - 1; x += 1) {
            for (int y = 1; y < height - 1; y += 1 ) {
                if (!worldWrappers[x][y].isRoom() && worldWrappers[x][y].getTile().equals(Tileset.FLOOR)) {
                    notRoomButFloors.add(worldWrappers[x][y]);
                }
            }
        }
        return notRoomButFloors;
    }

    private int h(int v) {
        return Math.abs(toX(v) - toX(target)) + Math.abs(toY(v) - toY(target));
        //计算曼哈顿距离
    }

    private int findMinimumUnmarked(Queue<Integer> queue) {
        int minimumVertex = queue.peek();
        int minimumPath = distTo[minimumVertex] + h(minimumVertex);
        for (int vertex : queue) {
            if (distTo[vertex] + h(vertex) < minimumPath) {
                minimumVertex = vertex;
            }
        }
        return minimumVertex;//当前出发点到目标点之间的的距离之和来计算
    }
    private void astar() {
        Queue<Integer> fringe = new ArrayDeque<>();
        fringe.add(source);
        setMarkInWorldWrappers(source, true);
        while (!fringe.isEmpty()){
            int v = findMinimumUnmarked(fringe);//寻找到最小的那个估价
            fringe.remove(v);//移去这个
            for (TETileWrapper tileWrapper : tileNeighbors(v)){//寻找周围四个方向情况
                if (!tileWrapper.isMarked()) {//不要反复搜索一个点，在没有标记情况下
                    int w = xyTo1D(tileWrapper.getX(), tileWrapper.getY());
                    fringe.add(w);//将这个方向加入其中
                    setMarkInWorldWrappers(w, true);
                    edgeTo[w] = v;//回来反向的边朝着这里，具体可以见知乎的文章
                    distTo[w] = distTo[v] + 1;//起点处
                    if (w == target) {
                        targetFound = true;
                    }
                    if (targetFound) {
                        return;
                    }
                }
            }
        }
    }
    private LinkedList<TETileWrapper> tileNeighbors(int v) {
        LinkedList<TETileWrapper> neighbors = new LinkedList<>();
        int x = toX(v);
        int y = toY(v);
        // north (x, y + 1)
        if (isNeighbor(x, y + 1)) {
            neighbors.add(worldWrappers[x][y + 1]);
        }
        // south (x, y - 1)
        if (isNeighbor(x, y - 1)) {
            neighbors.add(worldWrappers[x][y - 1]);
        }
        // west (x - 1, y)
        if (isNeighbor(x - 1, y)) {
            neighbors.add(worldWrappers[x - 1][y]);
        }
        // east (x + 1, y)
        if (isNeighbor(x + 1, y)) {
            neighbors.add(worldWrappers[x + 1][y]);
        }
        return neighbors;
    }

    private boolean isNeighbor(int x, int y) {
        // can't choose side of limbo and room as floor
        return x < width - 1 && x > 0
                && y < height - 1 && y > 0
                && !worldWrappers[x][y].isRoom();
    }
    private void reset()
    {
        for(int i = 0;i < V();i++)
        {
            edgeTo[i] = Integer.MAX_VALUE;
            distTo[i] = Integer.MAX_VALUE;
        }
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                if (!worldWrappers[x][y].isRoom()){
                    worldWrappers[x][y].markTile(false);//将visit的情况去掉
                }
            }
        }
    }
    private void buildHallwayByShortestPath(int v) {
        // center as floor
        int x = toX(v);
        int y = toY(v);
        worldWrappers[x][y].setTile(Tileset.FLOOR);
        // four directions as wall
        // north (x, y + 1)
        if (isHallwayWall(x, y + 1)) {
            worldWrappers[x][y + 1].setTile(Tileset.WALL);
        }
        // south (x, y - 1)
        if (isHallwayWall(x, y - 1)) {
            worldWrappers[x][y - 1].setTile(Tileset.WALL);
        }
        // west (x - 1, y)
        if (isHallwayWall(x - 1, y)) {
            worldWrappers[x - 1][y].setTile(Tileset.WALL);
        }
        // east (x + 1, y)
        if (isHallwayWall(x + 1, y)) {
            worldWrappers[x + 1][y].setTile(Tileset.WALL);
        }
        if (v == source) {
            return;
        }
        buildHallwayByShortestPath(edgeTo[v]);
    }

    private boolean isHallwayWall(int x, int y) {
        //保证不能越界，保证不会在室内，保证不会弄到别的路上去
        //意思就是，可以穿过去，但是不能破坏原有的路
        //就是能将墙变成路
        // can choose side of limbo and room as wall of hallway
        // but can't choose floor in already build hallway as wall of hallway
        return x < width  && x >= 0
                && y < height && y >= 0
                && !worldWrappers[x][y].isRoom()
                && !worldWrappers[x][y].getTile().equals(Tileset.FLOOR);
    }

    private void setSource(int x, int y) {
        this.source = xyTo1D(x, y);
    }

    private void setTarget(int x, int y) {
        this.target = xyTo1D(x, y);//一维化坐标信息
    }

    // you may meet some hallways with width of bigger than 2 (i.e. 3*3 floors or more)
    // so, we add a wall in the center of hallways with 3*3 floors
    private void fillSomeWalls() {//这就是增加难度的，就是不让空间那么宽了
        for (int x = 0; x <= width - 3; x += 1) {
            for (int y = 2; y <= height - 1; y += 1) {
                if (!worldWrappers[x][y].isRoom()
                        && worldWrappers[x][y].getTile().equals(Tileset.FLOOR)) {
                    int floorCount = 0;
                    // note: we should reset loop in each loop (i.e. int i = x; int j = y;)
                    for (int i = x; i <= x + 2; i += 1) {
                        for (int j = y; j >= y - 2; j -= 1) {
                            if (worldWrappers[i][j].getTile().equals(Tileset.FLOOR)) {
                                floorCount += 1;
                            }
                        }
                    }
                    // if it is 3*3 floors, then center set as wall
                    if (floorCount == 9) {
                        worldWrappers[x + 1][y - 1].setTile(Tileset.WALL);
                    }
                }
            }
        }
    }

    // create random avatar
    private void randomAvatar() {//随机生成主角，并且记录相应的信息并进行替换
        LinkedList<TETileWrapper> floors = new LinkedList<>();
        for (int x = 0; x <= width - 1; x += 1) {
            for (int y = 0; y <= height - 1; y += 1) {
                if (worldWrappers[x][y].getTile().equals(Tileset.FLOOR)) {
                    floors.add(worldWrappers[x][y]);
                }
            }
        }
        int randomNum = RANDOM.nextInt(floors.size());
        TETileWrapper avatarTemp = floors.get(randomNum);
        worldWrappers[avatarTemp.getX()][avatarTemp.getY()].setTile(Tileset.AVATAR);
        this.avatar = worldWrappers[avatarTemp.getX()][avatarTemp.getY()];
    }

    // move avatar
    private void moveAvatar(String direction) {
        // W, up
        if (direction.equals("W") && validDirection("W")) {//确定去向，并且检验是否合理
            moveTo("W");
        }
        // S, down
        if (direction.equals("S") && validDirection("S")) {
            moveTo("S");
        }
        // A, left
        if (direction.equals("A") && validDirection("A")) {
            moveTo("A");
        }
        // D, right
        if (direction.equals("D") && validDirection("D")) {
            moveTo("D");
        }
        keepLightingWithAvatarInRoom();
    }

    private boolean validDirection(String direction) {//鉴定当前能不能走
        int x = avatar.getX();
        int y = avatar.getY();
        // W,S,A,D
        TETileWrapper tileWrapper = switch (direction) {
            case "W" -> worldWrappers[x][y + 1];
            case "S" -> worldWrappers[x][y - 1];
            case "A" -> worldWrappers[x - 1][y];
            case "D" -> worldWrappers[x + 1][y];
            default -> null;
        };
        return tileWrapper.getTile().equals(Tileset.FLOOR)
                || tileWrapper.getTile().description().equals(Tileset.LIGHTS[0].description());
    }

    private void moveTo(String direction) {
        int x = avatar.getX();
        int y = avatar.getY();
        // W,S,A,D
        switch (direction) {
            case "W":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x][y + 1].setTile(Tileset.AVATAR);
                // note: you should reset this.avatar
                this.avatar = worldWrappers[x][y + 1];
                break;
            case "S":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x][y - 1].setTile(Tileset.AVATAR);
                this.avatar = worldWrappers[x][y - 1];
                break;
            case "A":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x - 1][y].setTile(Tileset.AVATAR);
                this.avatar = worldWrappers[x - 1][y];
                break;
            case "D":
                worldWrappers[x][y].setTile(Tileset.FLOOR);
                worldWrappers[x + 1][y].setTile(Tileset.AVATAR);
                this.avatar = worldWrappers[x + 1][y];
                break;
        }
    }

    // create random light in room
    //接下来就是在房间中创建光源的情况了
    private void createRandomLightInRooms() {
        // lighten in rooms
        for (Room room : rooms) {
            int x = room.getX();
            int y = room.getY();
            int wight = room.getWidth();
            int height = room.getHeight();
            int randomNum = RANDOM.nextInt((wight - 2) * (height - 2));
            int count = 0;
            for (int i = x + 1; i <= x + wight - 2; i += 1) {
                for(int j = y - 1; j >= y - height + 2; j -= 1) {
                    if (count == randomNum) {
                        // set x with light and y with light in room
                        room.setXWithLight(i);
                        room.setYWithLight(j);
                        turnOnOrOffLightInRoom(i, j, room.getRoomNum(), room.getTurnOn());
                    }
                    count += 1;
                }
            }
        }
    }

    private void keepLightingWithAvatarInRoom() {
        // lighten with avatar in room
        if (avatar.isRoom()) {
            Room room = rooms.get(avatar.getRoomNum());
            //  if turn == false but room is lighting(i.e. turn on), we should keep lighting
            //  if turn == false but room is not lighting(i.e. turn off), we should keep not lighting
            turnOnOrOffLightInRoom(room.getXWithLight(), room.getYWithLight(), room.getRoomNum(), room.getTurnOn());
        }
    }

    // randomly number of room to turn on/off light
    public TETile[][] turnOnOrOffLightInRooms() {
        // 0. turn on -> 1. turn off -> 2. turn on -> 3. turn off -> 4. turn on -> ...
        // i.e. 0. turn = true -> 1. turn = false -> 2. turn = true -> 3. turn = false -> 4. turn = true -> ...
        // flip turn by typed "P"
        turn = !turn;
        // if turn off, we should reset random rooms to next to turn on/off
        if (!turn) {
            resetRandomRooms();
        }
        // turn on/off light in rooms
        for (Room room : randomRooms) {
            room.setTurnOn(turn);
            turnOnOrOffLightInRoom(room.getXWithLight(), room.getYWithLight(), room.getRoomNum(), room.getTurnOn());
        }
        return getWorldByWorldWrappers();
    }

    // reset random rooms to turn on/off
    private void resetRandomRooms() {
        // get total of rooms to turn on/off but not 0
        int turnTotal = RANDOM.nextInt(RoomNum + 1);
        while (turnTotal == 0) {
            turnTotal = RANDOM.nextInt(RoomNum + 1);
        }
        int randomRoomNum = RANDOM.nextInt(turnTotal);
        // randomly get roomNum to turn on/off but not same
        LinkedList<Integer> turnRoomNumbers = new LinkedList<>();
        while(turnRoomNumbers.size() != turnTotal) {
            if (!turnRoomNumbers.contains(randomRoomNum)) {
                turnRoomNumbers.add(randomRoomNum);
            }
            randomRoomNum = RANDOM.nextInt(turnTotal);
        }
        // set rooms to turn on/off
        randomRooms.clear();
        for (int roomNum : turnRoomNumbers) {
            randomRooms.add(rooms.get(roomNum));
        }
    }

    private void turnOnOrOffLightInRoom(int xWithLight, int yWithLight, int roomNum, boolean turn) {
        // if turn on, we should generate light with background(blue/yellow) in rooms
        // otherwise, we should generate light without background(blue/yellow) in rooms
        if (turn) {
            Tileset.generateLightWithBlue();//开灯
        } else {
            Tileset.generateLightWithoutBackground();//不然就是关灯
        }
        // length of side: 1, 3, 5, 7, 9, 11...
        for (int x = xWithLight, y = yWithLight, levelWithLight = 0, sideLength = 1;
             y < yWithLight + Tileset.levelWithLights;
             x -= 1, y += 1, levelWithLight += 1, sideLength += 2) {
            TETileWrapper tileWrapper;
            // bottom
            int level = sideLength - 1;
            for (int i = x; i < x + sideLength; i += 1) {
                setFloorToLightInRoom(i, y - level, roomNum, levelWithLight);
            }
            // middle
            level -= 1;
            while (level > 0){
                for (int i = x; i < x + sideLength; i += 1 ) {
                    if (i == x || i == x + sideLength - 1) {
                        setFloorToLightInRoom(i, y - level, roomNum, levelWithLight);
                    }
                }
                level -= 1;
            }
            // top, level = 0
            for (int i = x; i < x + sideLength; i += 1) {
                setFloorToLightInRoom(i, y - level, roomNum, levelWithLight);
            }
        }
    }

    // In room, tile must acquire some conditions below, then we can set the floor to the light
    // 1. tile is floor or light(light maybe not background, so we should use description())
    // 2. tile in room
    // 3. tile is not in around of room(i.e. not walls, door and four corners in room)
    // 4. tile has same roomNum(i.e. we must ensure tile in current room)
    private void setFloorToLightInRoom(int x, int y, int roomNum, int levelWithLight) {
        if (validTileInWorld(x, y)) {
            TETileWrapper tileWrapper = worldWrappers[x][y];
            if ((tileWrapper.getTile().equals(Tileset.FLOOR)
                    || (tileWrapper.getTile().description().equals("light")))
                    && !tileWrapper.getTile().equals(Tileset.AVATAR)
                    && tileWrapper.isRoom()
                    && !tileWrapper.getIsAround()
                    && tileWrapper.getRoomNum() == roomNum) {
                tileWrapper.setTile(Tileset.LIGHTS[levelWithLight]);
            }
        }
    }

    // it is valid tile in world?
    private boolean validTileInWorld(int x, int y) {
        return  x >= 0 && x < width && y >= 0 && y < height;
    }

    // set mark in worldWrapper
    private void setMarkInWorldWrappers(int v, boolean markedValue) {
        worldWrappers[toX(v)][toY(v)].markTile(markedValue);
    }

    // when we use toX(), toY() in the limbo that maybe lead to some *error*
    // you can see reason in the end

    /**
     * Returns x coordinate for given vertex.
     * For example if N = 10, and V = 12, returns 2.
     */
    private int toX(int v) {
        return v % N + 1;
    }
    //toX,toY为什么这么取值，可以参考 xyTo1D的模板
    //return (y - 1) * N + (x - 1)
    //其中,/就能很好的取出y-1的值1，得到y
    // % 能够取出x - 1的值，得到x，这样就可以得到x,y的合集坐标
    /**
     * Returns y coordinate for given vertex.
     * For example if N = 10, and V = 12, returns 1.
     */
    private int toY(int v) {
        return v / N + 1;
    }

    /**
     * Returns one dimensional coordinate for vertex in position x, y.
     */
    private int xyTo1D(int x, int y) {
        return (y - 1) * N + (x - 1);
    }
    private int V()
    {
        return N * N;
    }

}
