package deque;

import java.util.Iterator;

public class ArrayDeque<Item> implements Iterable<Item>,Deque<Item>
{
    private Item[] items;
    private int size;
    private int nextfirst;
    private int nextlast;

    public ArrayDeque()
    {
        items = (Item[])new Object[8];//这里其实无所谓最大的数目是多少
        size = 0;
        nextfirst = 8/4;

        nextlast = 8/4 + 1;
    }

    private void resize(int capacity,boolean added)
    {
        Item[] newitems = (Item[]) new Object[capacity];
        if(added)
        {
            System.arraycopy(items,nextlast,newitems,size/2,size-nextlast);
            System.arraycopy(items,0,newitems,(size - nextlast) + size/2,nextlast);
        }
        else
        {
            System.arraycopy(items,(nextfirst + 1)% items.length,newitems,0,size);
        }
        items = newitems;
    }


    public void addFirst(Item x)
    {
        if(size == items.length)
        {
            resize(size * 2,true);//specially designed for this function
            nextfirst = (size/2) - 1;
            nextlast = size/2 + size;
        }
        items[nextfirst] = x;
        if((nextfirst - 1) % items.length == -1)
        {
            nextfirst = (nextfirst - 1) + items.length;
        }
        else
        {
            nextfirst = (nextfirst - 1) % items.length;
        }
        size += 1;
    }

    //items.length is the length of arrays which malloced by new
    //not how many elements it contains!
    public void addLast(Item x) {
        if (size == items.length)
        {
            resize(size * 2,true);
            nextfirst = (size/2) - 1;
            nextlast = (size/2) + size;
        }
        items[nextlast] = x;
        nextlast = (nextlast + 1) % items.length;
        size = size + 1;

    }

    public Item removeFirst()
    {
        if(isEmpty())
        {
            return null;//判断为空
        }
        if((size < items.length/4) && (size > 4))
        {
            resize(items.length/4,false);
            nextfirst = items.length - 1;//TODO maybe BUG exists
            nextlast = size;
        }
        nextfirst = (nextfirst+1) % items.length;
        Item item = items[nextfirst];
        items[nextfirst] = null;
        size = size - 1;
        return item;
    }

    public Item removeLast()
    {
        if(isEmpty())
        {
            return null;//判断为空
        }
        if((size < items.length/4) && (size > 4))
        {
            resize(items.length/4,false);
            nextfirst = items.length - 1;//TODO maybe BUG exists
            nextlast = size;
        }

        if((nextlast - 1) % items.length == -1)
        {
            nextlast = (nextlast - 1) + items.length;
        }
        else
        {
            nextlast = (nextlast - 1) % items.length;
        }
        Item item = items[nextlast];
        items[nextlast] = null;
        size -= 1;
        return item;
    }

    public Item get(int i)//index starts from 0
    {
        if(i < 0 && i >= size)
            return null;
        int index = nextfirst + i + 1;
        return items[index % items.length];
    }

    public boolean isEmpty()
    {
        if(size == 0)
            return true;
        return false;
    }

    public int size()
    {
        return size;
    }


    public void printDeque()
    {
        int tsize = size();
        int cnt = (nextfirst + 1) % items.length;//TODO BUG
        while(tsize > 0)
        {
            if(tsize == 1)
            {
                System.out.println(items[cnt]);
                return ;
            }
            System.out.print(items[cnt] + " ");
            cnt = (cnt + 1) % items.length;
            tsize -=1;

        }

    }



    public Iterator<Item> iterator() {
        return new ArrayListDequeIterator();
    }

    private class ArrayListDequeIterator implements Iterator<Item>
    {
        private int total = 0;
        private int cnt = (nextfirst + 1) % items.length;

        public ArrayListDequeIterator()
        {
            total = size();
            cnt = (nextfirst + 1) % items.length;
        }

        public boolean hasNext()
        {
            if(total == 0)
                return false;
            return true;
        }

        public Item next()
        {
            Item returnItem = items[cnt];
            cnt = (cnt + 1) % items.length;
            total-=1;
            return returnItem;
        }

    }


    public boolean contains(Item x)
    {
        int tsize = size();
        int cnt = (nextfirst + 1) % items.length;//TODO BUG
        while(tsize > 0)
        {
            if(items[cnt].equals(x))
            {
                return true;
            }
            cnt = (cnt + 1) % items.length;
            tsize -=1;

        }
        return false;
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        if (o == null)
        {
            return false;
        }
        ArrayDeque<Item> obj = (ArrayDeque<Item>) o;
        if(this.size() != obj.size())
        {
            return false;
        }
        //这里要等一下，吧迭代器做完了才行
        for(Item item:this)
        {
            if(!obj.contains(item))
            {
                return false;
            }
        }
        return true;
    }
}
