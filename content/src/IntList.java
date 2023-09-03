public class IntList {
    public int first;
    public IntList rest;
    
    public IntList(int f,IntList h)
    {
        first = f;
        rest = h;
    }
    public int size()
    {
        if(rest == null){
            return 1;
        }//这个计算链表的方法是用递归，搜到最后一个，为null的时候回溯
        return 1 + this.rest.size();
    }

    public int iterativeSize()
    {
        IntList p =this;
        int totalSize = 0;
        while(p != null)
        {
            totalSize += 1;
            p = p.rest;
        }
        return totalSize;
    }
    public void addFirst(int x)
    {
        this.rest = new IntList(x, this.rest);//麻烦之处就在于
        //我需要重新分配一个节点，将现有的接到那个上面
        //貌似this的值不是一个合理的IntList
        //所以这样做其实是加到第二个节点

    }
    public static void main(String[] args)
    {
        IntList L = new IntList(15,null);
        L = new IntList(10,L);
        L = new IntList(5,L);//这样的链表生成策略非常像头插法
        L.addFirst(12);

        System.out.println(L.iterativeSize());
        return;
    }
    
}
