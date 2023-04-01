import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class StatsServlet extends HttpServlet {
    private static final String SERVER = "54.245.186.113";
    private static final String USER = "rabbit";
    private static final String PASSWORD = "rabbit";
    private static final int ON_DEMAND = 20;
    private static final int WAIT_TIME_SECS = 1;
    private GenericObjectPool<Channel> pool;
    private FanoutExchange fanoutExchange;

    @Override
    public void init() throws ServletException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setUsername(USER);
        factory.setPassword(PASSWORD);
        try {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(ON_DEMAND);
            config.setBlockWhenExhausted(true);
            config.setMaxWait(Duration.ofSeconds(WAIT_TIME_SECS));
            RMQChannelFactory chanFactory = new RMQChannelFactory (factory.newConnection());
            pool = new GenericObjectPool<>(chanFactory, config);
            fanoutExchange = new FanoutExchange(pool);
            fanoutExchange.declareQueues();
            fanoutExchange.declareExchange();
            fanoutExchange.declareBindings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        // check if we have a URL
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // urlPath  = "/swipe/left"
        // urlParts = [, swipe, left]
        if (urlParts.length != 3 || !(urlParts[2].equals("left") || urlParts[2].equals("right"))) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Invalid url input: left or right");
            return;
        }

        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = req.getReader().readLine()) != null) {
            sb.append(s);
        }
        Swipe swipe = (Swipe) gson.fromJson(sb.toString(), Swipe.class);
        //set direction in swipe pojo
        swipe.setDirection(urlParts[2]);
        String json = gson.toJson(swipe);


        try {
            boolean invalidInPut = false;
            //if the swiper id is not within [1,5000]
            if(Integer.parseInt(swipe.getSwiper()) < 1 || 5000 < Integer.parseInt(swipe.getSwiper())){
                invalidInPut = true;
            }
            //if the length of the string is less than or equal to 256
            if(swipe.getComment() == null || swipe.getComment().length() > 256){
                invalidInPut = true;
                res.getWriter().write("The length of the comment is longer than 256");
            }
            //if the input is invalid
            if(invalidInPut){
                res.setStatus((HttpServletResponse.SC_NOT_FOUND));
                res.getWriter().flush();
                return;
            }
        } catch (NumberFormatException e) {
            //if the body is null or id contains characters
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Invalid input");
            res.getWriter().flush();
            return;
        }

        try {
            if(fanoutExchange.publishMessage(json)){
                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write(json);
            }else{
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("Failed to publish");
            }
            res.getWriter().flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
