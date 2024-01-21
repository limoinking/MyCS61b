public class A {
    public static void main(String[] args)
    {
        A y = new A();
        A z = new A();
        y.fish(z);
        //哦哦，我明白意思了
        //A A = 1
        //A B = 2
        //B A = 1
        //B B = 3
    }

    int fish(A other)
    {
        return 1;
    }
    int fish(B other)
    {
        return 2;
    }
}

class B extends A
{
    @Override
    int fish(B other)
    {
        return 3;
    }
}