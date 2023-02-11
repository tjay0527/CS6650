import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Metrics {
    private AtomicInteger totalRequest = new AtomicInteger(0);
    private AtomicInteger successRequest = new AtomicInteger(0);
    private LinkedBlockingDeque<Long> latencyQueue = new LinkedBlockingDeque<>();
    public int getTotalRequest() {
        return totalRequest.intValue();
    }

    public int getSuccessRequest() {
        return successRequest.intValue();
    }

    public LinkedBlockingDeque<Long> getLatencyQueue(){return latencyQueue;}

    public void increaseTotalRequest() {
        this.totalRequest.incrementAndGet();
    }

    public void increaseSuccessRequest() {
        this.successRequest.incrementAndGet();
    }

    public void addToLatencyQueue(Long latency){
        this.latencyQueue.add(latency);
    }

    public long getAveLatency(){
       long res = 0;
       Iterator<Long> iterate = latencyQueue.iterator();
       while(iterate.hasNext()){
           res += iterate.next();
       }
       return res/latencyQueue.size();
    }
}
