package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest
{
    @Test
    //because comparators are very different
    //so,when we initialize it
    //we should choose correct property
    //to satisfy our needs
    public void maxWithIntComparatorTest() {
        // construct an instance of class by using "new ClassName()"
        // when the class not pass argument to constructor
        IntComparator intComparator = new IntComparator();
        MaxArrayDeque<Integer> maxArrayDeque= new MaxArrayDeque<>(intComparator);
        for (int i = 0; i <= 100000; i++){
            maxArrayDeque.addFirst(i);
        }
        int actual = maxArrayDeque.max();
        assertEquals(100000, actual);

        int actual1 = maxArrayDeque.max(intComparator);
        assertEquals(100000, actual1);
    }
    //we know how to compare which number is bigger,right?
    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer i1, Integer i2){
            return i1 - i2;
        }
    }
    //type double is the same principle
    @Test

    public void maxWithDoubleComparatorTest() {
        // construct an instance of class by using "new ClassName()"
        // when the class not pass argument to constructor
        DoubleComparator doubleComparator = new DoubleComparator();
        MaxArrayDeque<Double> maxArrayDeque= new MaxArrayDeque<>(doubleComparator);
        for (double i = 0; i <= 100000; i++){
            maxArrayDeque.addFirst(i);
        }
        double actual = maxArrayDeque.max();//here,we need to set the parameter
        //delta,which can determine the number after the small point
        assertEquals(100000, actual,0.00);

        double actual1 = maxArrayDeque.max(doubleComparator);
        assertEquals(100000, actual1,0.00);
    }

    private static class DoubleComparator implements Comparator<Double> {

        @Override
        public int compare(Double o1, Double o2) {
            return (int)(o1-o2);
        }
    }
    //OK so if we want to compare String
    //not so hard,we can assume that the length of the
    //String is different from each other
    //so,we can just compare the lengths of them to compare
    private static class StringComparator implements Comparator<String> {



        @Override
        public int compare(String o1, String o2)
        {
            //o1.charAt(int index)
            //this function can make dictionary sort come true
            //and randomly visit the String
            return o1.compareTo(o2);//original function is perfect
        }
    }
}
