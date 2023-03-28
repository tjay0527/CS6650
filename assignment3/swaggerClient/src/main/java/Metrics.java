import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
    private AtomicInteger totalRequest = new AtomicInteger(0);
    private AtomicInteger successRequest = new AtomicInteger(0);
    private LinkedBlockingDeque<Result> resultQueue = new LinkedBlockingDeque<>();
    private Long maxResponse = Long.MIN_VALUE;
    private Long minResponse = Long.MAX_VALUE;

    //getter
    public int getTotalRequest() {
        return totalRequest.intValue();
    }

    public int getSuccessRequest() {
        return successRequest.intValue();
    }

    public LinkedBlockingDeque<Result> getResultQueue() {return resultQueue;}

    /***
     * get the total of latency of all requests
     * @return
     */
    public long getSumOfLatency(){
        long sum = 0;
        for(Result res: resultQueue){
            sum += res.getLatency();
        }
        return sum;
    }

    public Long getMaxResponse() {
        return maxResponse;
    }

    public Long getMinResponse() {
        return minResponse;
    }

    /***
     * get he average latency of all requests
     * @return
     */
    public long getAveLatency(){
        long sum = 0;
        for(Result res: resultQueue){
            sum += res.getLatency();
        }
        return sum/resultQueue.size();
    }

    //setter
    public void increaseTotalRequest() {
        this.totalRequest.incrementAndGet();
    }

    public void increaseSuccessRequest() {
        this.successRequest.incrementAndGet();
    }

    public void addToResultQueue(Result res) {
        this.resultQueue.add(res);
    }

    public void setMaxResponse(Long maxResponse) {
        this.maxResponse = maxResponse;
    }

    public void setMinResponse(Long minResponse) {
        this.minResponse = minResponse;
    }
}
