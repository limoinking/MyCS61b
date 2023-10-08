package gh2;


import deque.ArrayDeque;
import deque.Deque;
import deque.LinkedListDeque;


//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /**
     * Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday.
     */
    private static final int SR = 44100;      // Sampling Rate
    private static double DECAY = .996; // energy decay factor


    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        buffer = new ArrayDeque<>();
        for (int i = 0; i < Math.round(SR / frequency); i++) {
            buffer.addFirst(0.0);//initialize the deque with zero
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        //
        //       Make sure that your random numbers are different from each
        //       other. This does not mean that you need to check that the numbers
        //       are different from each other. It means you should repeatedly call
        //       Math.random() - 0.5 to generate new random numbers for each array index.
        int ss = buffer.size();
        for (int i = 0; i < ss; i++) {
            buffer.addLast(Math.random() - 0.5);
            buffer.removeFirst();
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double sample = buffer.removeFirst();
        double newFront = buffer.get(0);
        sample = ((sample + newFront) / 2) * DECAY;
        buffer.addLast(sample);//only once a time,not too many times
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }

    public void up()
    {
        DECAY = 0.999;
    }
    public void down()
    {
        DECAY = 0.93;
    }
}
