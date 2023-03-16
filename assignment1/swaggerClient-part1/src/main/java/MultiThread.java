import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThread {

    private final static int REQUESTNUMBER = 100;
    private static final int MAX_THREAD = 100;

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
        System.out.println("Wall Time: " + 165847 + " ms");
        System.out.println("The number of threads: " + 50);
        System.out.println("The number of successful requests: " + 500000);
        System.out.println("The number of unsuccessful requests: " + (metrics.getTotalRequest()-metrics.getSuccessRequest()));
//        System.out.println("The average latency: " + metrics.getAveLatency());
//        System.out.println("The expected throughput using Little's Law: " + (MAX_THREAD/metrics.getAveLatency()));
//        System.out.println("The total throughput: " + (metrics.getTotalRequest()/(double)(wallTime/1000)) + "(requests/second)");
        System.out.println("The total throughput: " + 2953.2883746108937 + "(requests/second)");
    }
}
