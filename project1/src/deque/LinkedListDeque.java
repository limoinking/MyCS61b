package deque;
import java.util.Iterator;
public class LinkedListDeque <Item> implements Iterable<Item> ,Deque<Item>{

    public Iterator<Item> iterator() {
        return new LinkedListDequeIterator();
    }

    private class IntNode {
        public Item item;
        public IntNode next;

        public IntNode(Item i, IntNode n) {
            item = i;
            next = n;
        }
    }

    public LinkedListDeque() {
        sentinel = new IntNode(null, null);
        size = 0;
    }

    private IntNode sentinel;
    private int size;

    public void addFirst(Item item) {
        sentinel.next = new IntNode(item, sentinel.next);
        size = size + 1;
    }

    public void addLast(Item item) {
        size = size + 1;
        IntNode p = sentinel;
        while (p.next != null) {
            p = p.next;
        }
        p.next = new IntNode(item, null);
    }

    public boolean isEmpty() {
        if (size == 0)
            return true;
        return false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        IntNode p = sentinel.next;
        while (p != null) {
            System.out.print(p.item);
        }
        System.out.println();
    }

    public Item removeFirst() {
        IntNode p = sentinel.next;
        if (p != null) {
            sentinel.next = p.next;
            return p.item;
        }
        return null;

    }

    public Item removeLast() {
        IntNode p = sentinel.next;
        if (p == null) {
            return null;
        }
        IntNode s = sentinel;
        while (p.next != null) {
            s = s.next;
            p = p.next;
        }
        s.next = null;
        return p.item;
    }

    public Item get(int index) {
        if (index >= size) {
            return null;
        }
        IntNode p = sentinel.next;
        int cur = 0;
        while (p != null) {
            if (cur == index) {
                break;
            }
            cur++;
        }
        return p.item;
    }
    private Item trans(int index,int cur,IntNode p)
    {
        if(index != cur)
        {
            trans(index,cur+1,p.next);
        }
        return p.item;//safe bet
    }
    public Item getRecursive(int index)
    {
        if(index >= size)
        {
            return null;
        }
        IntNode pnode = sentinel.next;
        return trans(index,0,pnode);
    }
    private class LinkedListDequeIterator implements Iterator<Item>
    {
        private IntNode p;

        public LinkedListDequeIterator()
        {
            p = sentinel.next;
        }

        public boolean hasNext()
        {
            if(p != null){
                return true;
            }
            return false;
        }



        public Item next()
        {
            Item returnItem = p.item;
            p = p.next;
            return returnItem;
        }

    }

    public boolean contains(Item x)
    {
        IntNode p = sentinel.next;
        while(p != null)
        {
            if(p.item.equals(x))//直接用现有的类判断equal
            {
                return true;
            }
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
        LinkedListDeque<Item> obj = (LinkedListDeque<Item>) o;
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
