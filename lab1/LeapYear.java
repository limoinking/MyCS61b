public class LeapYear {

    public static int je(int a)
    {
        if((a % 4 == 0 && a % 100 != 0) || (a % 400 == 0))
            return 1;
        else
        {
            return 0;
        }
    }
    public static void main(String[] args) {
        String target = args[0];
        int num2 = Integer.parseInt(target);
        int judge = je(num2);
        if(judge == 1)
        {
            System.out.println(target + " is a leap year.");
        }
        else
        {
            System.out.println(target + " is not a leap year.");
        }
	}
}
