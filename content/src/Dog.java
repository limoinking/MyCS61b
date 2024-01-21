public class Dog {
    public int weightInPounds;

    public Dog(int w) {
        weightInPounds = w;
    }

    public void makeNoise() {
        if (weightInPounds < 10) {
            System.out.println("yipyipyip!");
        } else if (weightInPounds < 30) {
            System.out.println("bark. bark.");
        } else {
            System.out.println("woof!");
        }    
    }

    public static Dog maxDog(Dog d1,Dog d2) 
    {
    if (d1.weightInPounds > d2.weightInPounds) {
        return d1;
    }
    return d2;
    }

    public Dog maxDog1(Dog d2)
    {
        if (weightInPounds > d2.weightInPounds) {//比较有意思的是，
            //这里无论加不加this，他都默认为该Dog类中的this值
            return this;
        }
        return d2;
    }

}
