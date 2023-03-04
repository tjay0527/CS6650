import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

//@WebServlet(name = "TwinderServlet", value = "/swipe/*")
@WebServlet(name = "TwinderServlet", value = "/TwinderServlet/*")
public class TwinderServlet extends HttpServlet {
    private static final int ON_DEMAND = 20;
    // RMQ broker machine
//    private static final String SERVER = "localhost";
    private static final String SERVER = "35.87.81.55";
    // test queue name
    private static final String QUEUE_NAME = "test";
    // the durtaion in seconds a client waits for a channel to be available in the pool
    // Tune value to meet request load and pass to config.setMaxWait(...) method
    private static final int WAIT_TIME_SECS = 1;
    private GenericObjectPool<Channel> pool;
    @Override
    public void init() throws ServletException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setVirtualHost("cherry_broker");
        factory.setUsername("user");
        factory.setPassword("password");
//        factory.setUsername("guest");
//        factory.setPassword("guest");
        try {
            // we use this object to tailor the behavior of the GenericObjectPool
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            // The code as is allows the channel pool to grow to meet demand.
            // Change to config.setMaxTotal(NUM_CHANS) to limit the pool size
            config.setMaxTotal(ON_DEMAND);
            // clients will block when pool is exhausted, for a maximum duration of WAIT_TIME_SECS
            config.setBlockWhenExhausted(true);
            // tune WAIT_TIME_SECS to meet your workload/demand
            config.setMaxWait(Duration.ofSeconds(WAIT_TIME_SECS));
            // The channel facory generates new channels on demand, as needed by the GenericObjectPool
            RMQChannelFactory chanFactory = new RMQChannelFactory (factory.newConnection());
            //create the pool
            pool = new GenericObjectPool<>(chanFactory, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        // check we have a URL
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // urlPath  = "/swipe/left"
        // urlParts = [, swipe, left]
        if (urlParts.length != 3 || !(urlParts[2].equals("left") || urlParts[2].equals("right"))) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Valid url input: left or right");
            return;
        }

        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = req.getReader().readLine()) != null) {
            sb.append(s);
        }
        Swipe swipe = (Swipe) gson.fromJson(sb.toString(), Swipe.class);

        try {
            boolean invalidInPut = false;
            //if the swiper id is not within [1,5000]
            if(Integer.parseInt(swipe.getSwiper()) < 1 || 5000 < Integer.parseInt(swipe.getSwiper())){
                invalidInPut = true;
                res.getWriter().write("The swiper id is not within 1, 5000");
            }
            //if the swipee id is not within [1,1000000]
            if(Integer.parseInt(swipe.getSwipee()) < 1 || 1000000 < Integer.parseInt(swipe.getSwipee())){
                invalidInPut = true;
                res.getWriter().write("The swipee id is not within 1, 1000000");
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
            if(publishMessage(sb.toString())){
                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write(sb.toString());
                res.getWriter().flush();
            }else{
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("Failed to publish");
                res.getWriter().flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean publishMessage(String swipe) throws Exception {
        try {
            Channel channel;
            // get a channel from the pool
            channel = pool.borrowObject();

            // publish a message
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            byte[] payLoad = swipe.getBytes();
            channel.basicPublish("", QUEUE_NAME, null, payLoad);

            // return the channel to the pool
            pool.returnObject(channel);
            return true;
        } catch (Exception ex) {
            System.out.println("Failed to publish swipe message to RabbitMQ");
            return false;
        }
    }
}
