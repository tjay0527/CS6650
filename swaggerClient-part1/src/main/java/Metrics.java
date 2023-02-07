import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
    private AtomicInteger totalRequest = new AtomicInteger(0);
    private AtomicInteger successRequest = new AtomicInteger(0);
    private List<Long> latencyList = new ArrayList<>();

    public int getTotalRequest() {
        return totalRequest.intValue();
    }

    public int getSuccessRequest() {
        return successRequest.intValue();
    }

    public List<Long> getLatencyList() {
        return latencyList;
    }

    public void increaseTotalRequest() {
        this.totalRequest.incrementAndGet();
    }

    public void increaseSuccessRequest() {
        this.successRequest.incrementAndGet();
    }

    public void addToLatencyList(Long latency) {
        this.latencyList.add(latency);
    }
}
