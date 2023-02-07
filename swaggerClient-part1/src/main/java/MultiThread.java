import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThread {

    private final static int REQUESTNUMBER = 500000;
    private static final int MAX_THREAD = 100;
    public static void main(String[] args) throws InterruptedException {
        Metrics metrics = new Metrics();
        Long startTime = System.currentTimeMillis();
        System.out.println(String.format("start at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(startTime)));

        ExecutorService taskExecutor = Executors.newFixedThreadPool(MAX_THREAD);
        for(int i = 0; i < REQUESTNUMBER; i++){
            taskExecutor.execute(new Client(metrics));
        }

        taskExecutor.shutdown();
        while(!taskExecutor.isTerminated());

        //print result
        Long endTime = System.currentTimeMillis();
        System.out.println(String.format("All threads complete at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(endTime)));
        Long wallTime = endTime - startTime;
        System.out.println("Wall Time: " + wallTime + " ms");
        System.out.println("The number of threads: " + MAX_THREAD);
        System.out.println("The number of total requests: " + metrics.getTotalRequest());
        System.out.println("The number of successful requests: " + metrics.getSuccessRequest());
        System.out.println("The number of unsuccessful requests: " + (metrics.getTotalRequest()-metrics.getSuccessRequest()));
        System.out.println("The total throughput: " + (metrics.getTotalRequest()/(double)(wallTime/1000)) + "(requests/second)");
    }
}
