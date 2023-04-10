import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class MultiThread {

    private static final int REQUESTNUMBER = 500000;
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
        GetRunnable getRunnable = new GetRunnable();
        Thread getThread = new Thread(getRunnable);
        getThread.start();
        while(!taskExecutor.isTerminated());
        getThread.stop();
        Long endTime = System.currentTimeMillis();
        Long wallTime = endTime - startTime;

        print(metrics, wallTime);
        getRunnable.print();
    }

    /***
     * print out the necessary information for part 1
     * @param metrics
     * @param wallTime
     */
    public static void print(Metrics metrics, Long wallTime){
        System.out.println("The number of successful requests: " + metrics.getSuccessRequest());
        System.out.println("The number of unsuccessful requests: " + (metrics.getTotalRequest()-metrics.getSuccessRequest()));
        System.out.println("Wall Time: "  + wallTime + " ms");
        System.out.println("The total throughput: " + (metrics.getTotalRequest()/(double)(wallTime/ONE_SEC_TO_MILLISEC)) + "(requests/second)");
        System.out.println("The POSTs min response time: " + metrics.getMinResponse() + " (millisecs)");
        System.out.println("The POSTs mean response time: " + (double)metrics.getSumOfLatency()/metrics.getResultQueue().size() + " (millisecs)");
        System.out.println("The POSTs max response time: " + metrics.getMaxResponse() + " (millisecs)");
    }
}

