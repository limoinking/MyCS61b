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
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer> buggyAList = new BuggyAList<Integer>();

        // three add
        for (int i = 4; i < 7; i++){
            buggyAList.addLast(i);
        }
        // three remove one time
        for (int expected  = 6 ; expected > 3 ; expected--){
            int actual = buggyAList.removeLast();
            assertEquals(expected, actual);
        }
    }

    @Test
    p@Test
    public void randomizedTest(){
        AListNoResizing<Integer> LA = new AListNoResizing<Integer>();
        BuggyAList<Integer> LB = new BuggyAList<Integer>();
        int N = 5000;

        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                // AListNoResizing
                LA.addLast(randVal);
                // BuggyAList
                LB.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                // AListNoResizing
                LA.size();
                // BuggyAList
                LB.size();
            }else if (operationNumber == 2){
                // getLast
                if (LA.size() != 0){
                    LA.getLast();
                }
                if (LB.size() != 0){
                    LB.getLast();
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (LA.size() != 0){
                    LA.removeLast();
                }
                if (LB.size() != 0){
                    LB.removeLast();
                }
            }
        }
    }
}
