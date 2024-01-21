package deque;

import java.util.Comparator;

public class MaxArrayDeque <Item> extends ArrayDeque<Item>
{
    private Item[] items;
    private int size;
    private int nextfirst;
    private int nextlast;
    private Comparator<Item> comparator;
    //wholely inherit functions from ArrayDeque
    public MaxArrayDeque(Comparator<Item> c)
    {
        comparator = c;//initialize?
    }

    public Item max(Comparator<Item> c)
    {
        if(isEmpty())
            return null;
        int maxIndex = 0;
        for(int i = 1;i < size;i++)
        {
            //here do not use the way before
            //we can use get function to search for
            //elements which is the undermark of the
            //target element
            if(c.compare(get(maxIndex),get(i)) < 0)
            {
                //here we use the lib compare
                //that can help us compare elements
                //with different property
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }
    public Item max()
    {
        return max(comparator);//use function we made before
    }
}
