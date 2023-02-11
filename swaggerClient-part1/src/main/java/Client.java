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

    /***
     * Create a single client instance for sending POST request with retry mechanism.
     */
    @Override
    public void run() {
        //set up base URL and request body
        io.swagger.client.api.SwipeApi apiInstance = new io.swagger.client.api.SwipeApi();
//        apiInstance.getApiClient().setBasePath("http://localhost:8080/assignment1__exploded/TwinderServlet");
        apiInstance.getApiClient().setBasePath("http://35.86.84.201:8080/assignment1 _exploded archive/TwinderServlet");
        SwipeDetails body = randomBody();
        String[] randomLeftRight = new String[]{"left", "right"};
        String leftOrRight = randomLeftRight[ThreadLocalRandom.current().nextInt(2)];

        //post request
        Long requestStartTime = System.currentTimeMillis();
        metrics.increaseTotalRequest();//increase total request count
        int tryCnt = 0;
        while(tryCnt < RETRYLIMIT){
            try {
                apiInstance.swipe(body, leftOrRight);
                break;
            } catch (ApiException e) {
                if(e.getCode() == 404){
                    System.out.println(e.getCode() + ": " + e.getResponseBody());
                }
                tryCnt++;
                //retry for up to 5 times if the previous request fails
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

        Long requestEndTime = System.currentTimeMillis();
        //add the latency of this request to Metrics' latencyList
        metrics.addToLatencyQueue(requestEndTime-requestStartTime);
    }

    /***
     * Generating request body randomly.
     * @return
     */
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
