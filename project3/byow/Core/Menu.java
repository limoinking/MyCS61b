package byow.Core;
import edu.princeton.cs.algs4.StdDraw;
import java.io.Serializable;
import java.awt.*;
public class Menu implements Serializable
{
    private int width;
    private int height;

    private final String titleContent = "My World";
    private final String[] menuContents = {"New Game (N)", "Load Game (L)", "Quit (Q)", "Primary Features (P)"};

    public Menu(int width,int height)
    {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16,this.height * 16);//实际展示的和
        //标准是不一样的
        //这是是指间隔扩大了16倍
        StdDraw.setXscale(0,this.width);
        StdDraw.setYscale(0,this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawMenu()
    {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco",Font.BOLD,50);
        StdDraw.setFont(font);
        StdDraw.text(this.width/2.0,this.height*(2.0/3.0),titleContent);
        font = new Font("Monaco",Font.BOLD,30);
        int distance = 0;
        for(String name : menuContents)
        {
            StdDraw.text(this.width/2,this.height - distance,name);
            distance += 2;
        }
        StdDraw.show();
    }
}
