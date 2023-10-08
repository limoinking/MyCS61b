package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
public class GuitarHero
{
    public static final double CONCERT_A = 440.0;
    public static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static void main(String[] args)
    {
        GuitarString[] strings = new GuitarString[keyboard.length()];

        for(int i = 0;i < keyboard.length();i++)
        {
            double Concert = CONCERT_A * Math.pow(2, ((i - 24.0) / 12.0));
            strings[i] = new GuitarString(Concert);//initialize the string
        }

        while(true)
        {
            if(StdDraw.hasNextKeyTyped())
            {
                char key = StdDraw.nextKeyTyped();
                int cur = keyboard.indexOf(key);
                if(cur == -1)
                {
                    continue;
                }
                //here,we need to find what happen
                //if we input an error alphabet
                strings[cur].pluck();
            }

            double sample = 0;
            for(int i = 0;i < keyboard.length();i++)
            {
                sample += strings[i].sample();
            }
            StdAudio.play(sample);
            for(int i = 0;i < keyboard.length();i++)
            {
                strings[i].tic();
            }
        }
    }
}
