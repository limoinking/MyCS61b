public class DogLauncher {
    
    public static void change(Dog a,int x)
    {
        a.weightInPounds -= 10;
        x = x -5;
        return;
    }
    public static void main(String[] args) {
        Dog[] dogs = new Dog[2];
        dogs[0] = new Dog(8);
        dogs[1] = new Dog(23);
        Dog a = dogs[0].maxDog1(dogs[1]);
        a.makeNoise();
        int x = 5;
        change(a, x);
        System.out.println(a.weightInPounds);
        System.out.println(x);
    }
}