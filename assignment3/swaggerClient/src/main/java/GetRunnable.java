import io.swagger.client.ApiException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetRunnable implements Runnable{
    private static final int NUMOFAPI = 2;
    private static final int SWIPERIDLIMIT = 5000;
    private static List<Long> latenciesList = new ArrayList<>();
    @Override
    public void run() {
        for(int i = 0; i < 5; i++){
            Long requestStartTime = System.currentTimeMillis();
            try {
                randomGETRequest();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

            //get latency, set max, min latency, save res back to list
            Long latency = System.currentTimeMillis() - requestStartTime;
        }
    }


    public void randomGETRequest() throws ApiException {
        boolean matchesFlag = ThreadLocalRandom.current().nextBoolean();
        String randomSwiperID = ThreadLocalRandom.current().nextInt(SWIPERIDLIMIT)+1 + "";
        StringBuilder URL = new StringBuilder();
        if(matchesFlag){
            io.swagger.client.api.MatchesApi apiInstance = new io.swagger.client.api.MatchesApi();
            URL.append("http://localhost:8080/assignment3/stats/" + randomSwiperID);
            //apiInstance.getApiClient().setBasePath("http://54.245.162.31:8080/assignment2_archive/TwinderServlet");
            apiInstance.getApiClient().setBasePath(URL.toString());
            apiInstance.matches(randomSwiperID);
        }else{
            io.swagger.client.api.StatsApi apiInstance = new io.swagger.client.api.StatsApi();
            URL.append("http://localhost:8080/assignment3/matches/" + randomSwiperID);
            apiInstance.getApiClient().setBasePath(URL.toString());
            apiInstance.matchStats(randomSwiperID);
        }

    }
    public void print(){
        DescriptiveStatistics stats = new DescriptiveStatistics();
    }
}
