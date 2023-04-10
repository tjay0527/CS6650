import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetRunnable implements Runnable{
    private static final int SWIPERIDLIMIT = 5000;
    private static final String STATSURL = "http://localhost:8080/assignment3/stats/";
    private static final String MATCHESURL = "http://localhost:8080/assignment3/matches/";
    private static List<Long> latenciesList = new ArrayList<>();
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            for(int i = 0; i < 5; i++){
                Long requestStartTime = System.currentTimeMillis();
                try {
                    randomGETRequest();
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }

                //get latency, set max, min latency, save res back to list
                Long latency = System.currentTimeMillis() - requestStartTime;
                latenciesList.add(latency);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void randomGETRequest() throws ApiException {
        boolean matchesFlag = ThreadLocalRandom.current().nextBoolean();
        String randomSwiperID = ThreadLocalRandom.current().nextInt(SWIPERIDLIMIT)+1 + "";
        ApiClient apiClient = new ApiClient();
        if(matchesFlag){
            apiClient.setBasePath(MATCHESURL);
            MatchesApi matchesApi = new MatchesApi(apiClient);
            //apiInstance.getApiClient().setBasePath("http://54.245.162.31:8080/assignment2_archive/TwinderServlet");
            matchesApi.matches(randomSwiperID);
        }else{
            apiClient.setBasePath(STATSURL);
            StatsApi statsApi = new StatsApi(apiClient);
            statsApi.matchStats(randomSwiperID);
        }
    }
    public void print(){
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for(long l: latenciesList)
            stats.addValue(l);

        System.out.println("The GETs min response time: " + stats.getMin() + "(millisecs) ");
        System.out.println("The GETs mean response time: " + stats.getMean() + "(millisecs) ");
        System.out.println("The GETs max response time: " + stats.getMax() + "(millisecs) ");
    }
}
