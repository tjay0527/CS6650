import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class MultiThread {

    private final static int REQUESTNUMBER = 500000;
    private static final int MAX_THREAD = 50;

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

        //print result
        printPart1(metrics, wallTime);
        printPart2(metrics, wallTime/1000);
        //write to CSV
        CSVFileWriter write = new CSVFileWriter();
        write.writeCSVHelper(metrics);

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

    /***
     * print out the necessary information for part 2
     * @param metrics
     * @param wallTimeSec
     */
    public static void printPart2(Metrics metrics, Long wallTimeSec){
        LinkedBlockingDeque<Result> resultQueue = metrics.getResultQueue();
        int sizeOfQueue = resultQueue.size();
        Long[] latencyArr = new Long[sizeOfQueue];
        int idx = 0;
        Iterator<Result> iterate = resultQueue.iterator();
        while(iterate.hasNext()){
            latencyArr[idx++] = iterate.next().getLatency();
        }
        //sort the array of request in ascending order based on the start time
        Arrays.sort(latencyArr);

        long median = latencyArr[sizeOfQueue/2];
        if(sizeOfQueue % 2 == 0)
            median = (latencyArr[sizeOfQueue/2-1] + latencyArr[sizeOfQueue/2])/2;
        System.out.println("Part 2");
        System.out.println("The mean response time: " + (double)metrics.getSumOfLatency()/sizeOfQueue + " milliseconds");
        System.out.println("The median response time: " + median + " milliseconds");
        System.out.println("The total throughput: " + ((double)metrics.getTotalRequest()/(wallTimeSec)) + "(requests/second)");
        System.out.println("The p99 response time: " + latencyArr[(int)(sizeOfQueue*0.99)-1] + " milliseconds");
        System.out.println("The max response time: " + metrics.getMaxResponse() + " milliseconds");
        System.out.println("The min response time: " + metrics.getMinResponse() + " milliseconds");
    }
}

