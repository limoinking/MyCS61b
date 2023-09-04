package timingtest;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList <Integer> ops = new AList<Integer>();
        AList<Integer> start = new AList<Integer>();
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        int n = 500;

        for(int i = 1;i <= 8;i++)
        {
            n *= 2;
            ops.addLast(10000);
            Ns.addLast(n);
            long starttime = System.currentTimeMillis();


            for(int j = 1;j <= n;j++)
            {
                start.addLast(i);
            }
            long end = System.currentTimeMillis();
            times.addLast((double) (end-starttime)/1000);
        }
        printTimingTable(Ns,times,ops);

    }

}
