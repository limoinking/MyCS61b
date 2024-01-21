package deque;

public class ArrayDequeTest {
    public static void main(String args[])
    {

        ArrayDeque<Integer> a = new ArrayDeque<Integer>();
        for(int i = 1;i <= 20;i++)
        {
            a.addLast(i);
        }
        for(int i = 1;i < 18;i++)
        {
            a.removeLast();
        }
        for(Integer T : a)
        {
            System.out.print(T + " ");
        }
        System.out.println(a.get(3));
        System.out.println(a.contains(5));
    }

}
