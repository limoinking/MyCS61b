public class SLList {
    public static class IntNode {
        public int item;
        public IntNode next;
    
        public IntNode(int i,IntNode n)
        {
            item = i;
            next = n;
        }
    }
    
    private IntNode first;

    public SLList(int x){
        first = new IntNode(x,null);
    }

    

    public void addFirst(int x)//相当于是头节点插入，换个说法就是
    {
        first = new IntNode(x,first);//可以参考之前的那个东西，IntList,用指针调换的方法
    }

    public int getFirst()
    {
        return first.item;
    }

    public void addLast(int x)
    {
        IntNode p = first;
        while(p.next != null)
        {
            p = p.next;
        }
        p.next = new IntNode(x,null);
    }
    
    public int getLast(int x)
    {
        IntNode p = first;
        while(p.next != null)
        {
            p = p.next;
        }
        return p.item;
    }

    private static int size(IntNode p)
    {
        if(p.next == null)
        {
            return 1;
        }
        return 1 + size(p.next);
    }

    public int size()
    {
        return size(first);
    }



    public static void main(String[] args)
    {
        SLList L = new SLList(15);
        L.addFirst(10);
        L.first.next.next = L.first.next;
        return;
    }

    private int findFirstHelper(int n,int index,IntNode curr)
    {
        if(curr == null)
            return -1;
        if(curr.item == n)
            return index;
        else{
            return findFirstHelper(n, index, curr.next);
        }
    }
}
