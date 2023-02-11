import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThread {

    private final static int REQUESTNUMBER = 10000;
    private static final int MAX_THREAD = 1;

    /***
     *  main function that repeatedly creates instances for sending POST requests.
     * @param args
     */
    public static void main(String[] args) {
        Metrics metrics = new Metrics();
        Long startTime = System.currentTimeMillis();

        //create an executor with a fixed amount of thread allowed
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

    /***
     * print out the necessary information for part 1
     * @param metrics
     * @param wallTime
     */
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
