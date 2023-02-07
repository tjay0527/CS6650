import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThread {

    private final static int REQUESTNUMBER = 500000;
    private static final int MAX_THREAD = 100;
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
        printPart2(metrics, wallTime/1000);
        //write to CSV
        CSVFileWriter write = new CSVFileWriter();
        write.writeCSVHelper(metrics);

    }

    public static void printPart1(Metrics metrics, Long wallTime){
        System.out.println("Part1");
        System.out.println(String.format("All threads complete at: " + System.currentTimeMillis()));
        System.out.println("Wall Time: " + wallTime + " ms");
        System.out.println("The number of threads: " + MAX_THREAD);
        System.out.println("The number of total requests: " + metrics.getTotalRequest());
        System.out.println("The number of successful requests: " + metrics.getSuccessRequest());
        System.out.println("The number of unsuccessful requests: " + (metrics.getTotalRequest()-metrics.getSuccessRequest()));
        System.out.println("The total throughput: " + ((double)metrics.getTotalRequest()/(wallTime/1000)) + "(requests/second)");
    }

    public static void printPart2(Metrics metrics, Long wallTimeSec){
        List<Result> listOfResult = metrics.getResultList();
        int sizeOfList = listOfResult.size();

        //sort latency to get median
        Long[] latencyArr = new Long[sizeOfList];
        for(int i = 0; i < sizeOfList; i++)
            latencyArr[i] = listOfResult.get(i).getLatency();
        Arrays.sort(latencyArr);

        long median = latencyArr[sizeOfList/2];
        if(sizeOfList % 2 == 0)
            median = (latencyArr[sizeOfList/2-1] + latencyArr[sizeOfList/2])/2;
        System.out.println("Part 2");
        System.out.println("The mean response time: " + (double)metrics.getSumOfLatency()/sizeOfList + " milliseconds");
        System.out.println("The median response time: " + median + " milliseconds");
        System.out.println("The total throughput: " + ((double)metrics.getTotalRequest()/(wallTimeSec)) + "(requests/second)");
        System.out.println("The p99 response time: " + latencyArr[(int)(sizeOfList*0.99)-1] + " milliseconds");
        System.out.println("The max response time: " + metrics.getMaxResponse() + " milliseconds");
        System.out.println("The min response time: " + metrics.getMinResponse() + " milliseconds");
    }
}

