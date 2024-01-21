package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hug.
 */


public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        correct.addLast(5);
        correct.addLast(10);
        correct.addLast(15);

        broken.addLast(5);
        broken.addLast(10);
        broken.addLast(15);

        assertEquals(correct.size(), broken.size());

        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
    }

    @Test
    public void randomizedTest()
    {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 500;
        for(int i = 0;i < 500;i += 1)
        {
            int optNumber = StdRandom.uniform(0,4);
            if(optNumber == 0)
            {
                int randVal = StdRandom.uniform(0,100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            }
            else
            {
                if(correct.size() == 0)
                {
                    continue;
                }
                else
                {
                    System.out.println(correct.get(correct.size()-1));
                    System.out.println(broken.get(broken.size()-1));
                }
            }

        }
    }

}
