package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {


        long seed = 114514;
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        Random random = new Random();
        String TargetString = "";
        for(int i = 0;i < n;i++)//返回一个长度为n的位串
        {
            int RandomIndex = random.nextInt(26);
            TargetString += CHARACTERS[RandomIndex];
        }
        return TargetString;
    }

    private void drawUIHelper()
    {
        if(!gameOver)
        {
            Font font = new Font("Monaco",Font.BOLD,20);
            StdDraw.setFont(font);//设置字体
            StdDraw.textLeft(0,this.height - 1,"Round" + this.round);
            if(playerTurn)
            {
                StdDraw.text(this.width / 2.0, this.height - 1, "Typed!");
            }
            else
            {
                StdDraw.text(this.width / 2.0, this.height - 1, "Watch!");
            }
            String text = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
            StdDraw.textRight(this.width, this.height - 1, text);
            StdDraw.setPenColor(Color.white);
            StdDraw.line(0, this.height - 2, this.width, this.height - 2);//这个是划一条线
            font = new Font("Monaco",Font.BOLD,20);
            StdDraw.setFont(font);
        }
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(this.width/2.0,this.height/2.0,s);
        drawUIHelper();
        StdDraw.show();
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    private void displayOneSecond(String s) {
        drawFrame(s);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void breakZeroDotFiveSecond(String s) {
        drawFrame(s);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void flashSequence(String letters) {
        int index = 0;
        while(index < letters.length())
        {
            String s = Character.toString(letters.charAt(index));
            displayOneSecond(s);//这个就很好的刷新了屏幕
            breakZeroDotFiveSecond("");
            index += 1;
        }
        //TODO: Display each character in letters, making sure to blank the screen between letters
    }

    public String solicitNCharsInput(int n) {
        playerTurn = true;
        String playerInputString = "";
        drawFrame(playerInputString);//用"来刷新"
        while(n  > 0)
        {
            if(StdDraw.hasNextKeyTyped())
            {
                n -= 1;
                playerInputString += StdDraw.nextKeyTyped();
                drawFrame(playerInputString);
            }
        }
        displayOneSecond(playerInputString);

        playerTurn = false;
        return playerInputString;
    }
    private void checkCorrectOfInput(String randomString,String playerInputString)
    {
        gameOver = !randomString.equals(playerInputString);
    }
    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        playerTurn = false;
        while(true)
        {
            displayOneSecond("Round" + round);
            String randomString = generateRandomString(round);
            flashSequence(randomString);
            String playerInputString = solicitNCharsInput(round);
            checkCorrectOfInput(randomString,playerInputString);
            if(gameOver)
            {
                displayOneSecond("Game over!You made it to round" + round);
                return;
            }
            displayOneSecond("you are correct!");
            round += 1;
        }
        //TODO: Establish Engine loop
    }

}
