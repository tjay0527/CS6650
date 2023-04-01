import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable{
    private static final int COMMENTLENGTH = 256;
    private static final int SWIPERIDLIMIT = 5000;
    private static final int SWIPEEIDLIMIT = 10000;
    private static final int RETRYLIMIT = 5;
    private Metrics metrics;
    public Client(Metrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void run() {
        io.swagger.client.api.SwipeApi apiInstance = new io.swagger.client.api.SwipeApi();
        apiInstance.getApiClient().setBasePath("http://localhost:8080/assignment3/TwinderServlet");
//        apiInstance.getApiClient().setBasePath("http://54.245.162.31:8080/assignment2_archive/TwinderServlet");
        SwipeDetails body = randomBody();
        String[] randomLeftRight = new String[]{"left", "right"};
        String leftOrRight = randomLeftRight[ThreadLocalRandom.current().nextInt(2)];

        //set result info
        Result res = new Result();
        res.setType("Post");
        Long requestStartTime = System.currentTimeMillis();
        res.setStartTime(String.valueOf(requestStartTime));
        metrics.increaseTotalRequest();//increase total request count
        //post request
        int tryCnt = 0;
        while(tryCnt < RETRYLIMIT){
            try {
                apiInstance.swipe(body, leftOrRight);
                res.setResponseCode("201");//store a 201 response code if the post succeeds
                break;
            } catch (ApiException e) {
                tryCnt++;
                //if we have failed for the 5th times, store the response code to the list
                if(tryCnt == RETRYLIMIT)
                    res.setResponseCode(String.valueOf(e.getCode()));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        //if we use fewer than times than limit => increase success count
        if(tryCnt < RETRYLIMIT)
            metrics.increaseSuccessRequest();

        //get latency, set max, min latency, save res back to list
        Long latency = System.currentTimeMillis() - requestStartTime;
        res.setLatency(latency);
        metrics.setMaxResponse(Math.max(metrics.getMaxResponse(), latency));
        metrics.setMinResponse(Math.min(metrics.getMinResponse(), latency));
        metrics.addToResultQueue(res);
    }

    public static SwipeDetails randomBody(){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final int LEN = ThreadLocalRandom.current().nextInt(COMMENTLENGTH)+1;
        SwipeDetails body = new SwipeDetails();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < LEN; i++)
            sb.append(AB.charAt(ThreadLocalRandom.current().nextInt(AB.length())));

        body.setSwiper(String.valueOf(ThreadLocalRandom.current().nextInt(SWIPERIDLIMIT)+1));
        body.setSwipee(String.valueOf(ThreadLocalRandom.current().nextInt(SWIPEEIDLIMIT)+1));
        body.setComment(sb.toString());
        return body;
    }
}
