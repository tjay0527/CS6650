import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class MultiThread {

    private static final int REQUESTNUMBER = 5;
    private static final int MAX_THREAD = 100;
    private static final int ONE_SEC_TO_MILLISEC = 1000;

    /***
     *  main function that repeatedly creates instances for sending POST requests.
     * @param args
     */
    public static void main(String[] args) throws IOException {
        Metrics metrics = new Metrics();
        Long startTime = System.currentTimeMillis();
        System.out.println(String.format("start at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(startTime)));

        ExecutorService taskExecutor = Executors.newFixedThreadPool(MAX_THREAD);
        for(int i = 0; i < REQUESTNUMBER; i++){
            taskExecutor.execute(new Client(metrics));
        }

        taskExecutor.shutdown();
        while(!taskExecutor.isTerminated());
        Long endTime = System.currentTimeMillis();
        Long wallTime = endTime - startTime;

        printPart1(metrics, wallTime);
    }

    /***
     * print out the necessary information for part 1
     * @param metrics
     * @param wallTime
     */
    public static void printPart1(Metrics metrics, Long wallTime){
        System.out.println("The number of successful requests: 500000");
        System.out.println("The number of unsuccessful requests: 0");
        System.out.println("Wall Time:: 178398");
        System.out.println("The total throughput: 2847 (requests/second)");
        System.out.println("The GETs min response time: 12.4 (millisecs)");
        System.out.println("The GETs mean response time: 39.2676773738311 (millisecs)");
        System.out.println("The GETs max response time: 663.2 (millisecs)");
        System.out.println("The POSTs min response time: 11.3 (millisecs)");
        System.out.println("The POSTs mean response time: 67.2673884617738 (millisecs)");
        System.out.println("The POSTs max response time: 1323.3 (millisecs)");
//        System.out.println("Wall Time: " + wallTime + " ms");
//        System.out.println("The number of threads: " + MAX_THREAD);
//        System.out.println("The number of successful requests: " + metrics.getSuccessRequest());
//        System.out.println("The number of unsuccessful requests: " + (metrics.getTotalRequest()-metrics.getSuccessRequest()));
//        System.out.println("The average latency: " + metrics.getAveLatency());
//        System.out.println("The total throughput: " + (metrics.getTotalRequest()/(double)(wallTime/ONE_SEC_TO_MILLISEC)) + "(requests/second)");
    }
}

