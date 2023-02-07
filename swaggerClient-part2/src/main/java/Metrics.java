import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
    private AtomicInteger totalRequest = new AtomicInteger(0);
    private AtomicInteger successRequest = new AtomicInteger(0);
    private List<Result> resultList = new ArrayList<>();
    private Long maxResponse = Long.MIN_VALUE;
    private Long minResponse = Long.MAX_VALUE;

    //getter
    public int getTotalRequest() {
        return totalRequest.intValue();
    }

    public int getSuccessRequest() {
        return successRequest.intValue();
    }

    public List<Result> getResultList() {
        return resultList;
    }

    public long getSumOfLatency(){
        long sum = 0;
        for(Result res: resultList){
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

    //setter
    public void increaseTotalRequest() {
        this.totalRequest.incrementAndGet();
    }

    public void increaseSuccessRequest() {
        this.successRequest.incrementAndGet();
    }

    public void addToResultList(Result res) {
        this.resultList.add(res);
    }

    public void setMaxResponse(Long maxResponse) {
        this.maxResponse = maxResponse;
    }

    public void setMinResponse(Long minResponse) {
        this.minResponse = minResponse;
    }
}
