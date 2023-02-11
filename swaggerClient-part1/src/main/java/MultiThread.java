import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThread {

    private final static int REQUESTNUMBER = 500000;
    private static final int MAX_THREAD = 50;
    public static void main(String[] args) {
        Metrics metrics = new Metrics();
        Long startTime = System.currentTimeMillis();

        ExecutorService taskExecutor = Executors.newFixedThreadPool(MAX_THREAD);
        for(int i = 0; i < REQUESTNUMBER; i++){
            taskExecutor.execute(new Client(metrics));
        }

        taskExecutor.shutdown();
        while(!taskExecutor.isTerminated());

        //print result
        Long wallTime = System.currentTimeMillis() - startTime;
        printPart1(metrics, wallTime);
    }

    public static void printPart1(Metrics metrics, Long wallTime){
        System.out.println("Wall Time: " + wallTime + " ms");
        System.out.println("The number of threads: " + MAX_THREAD);
        System.out.println("The number of successful requests: " + metrics.getSuccessRequest());
        System.out.println("The number of unsuccessful requests: " + (metrics.getTotalRequest()-metrics.getSuccessRequest()));
        System.out.println("The average latency: " + metrics.getAveLatency());
//        System.out.println("The expected throughput using Little's Law: " + (MAX_THREAD/metrics.getAveLatency()));
        System.out.println("The total throughput: " + (metrics.getTotalRequest()/(double)(wallTime/1000)) + "(requests/second)");
    }
}
