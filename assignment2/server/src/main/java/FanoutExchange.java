import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class FanoutExchange {
    private GenericObjectPool<Channel> pool;
    private static final String FANOUT_EXCHANGE = "my-fanout-exchange";
    private static final String LIKE_Q = "LikeQ";
    private static final String POTENTIAL_Q = "PotentialQ";

    public FanoutExchange(GenericObjectPool<Channel> pool) {
        this.pool = pool;
    }

    public void declareExchange() throws Exception {
        Channel channel;
        channel = pool.borrowObject();
        //Declare my-fanout-exchange
        channel.exchangeDeclare(FANOUT_EXCHANGE, BuiltinExchangeType.FANOUT, true);
        pool.returnObject(channel);
    }

    public void declareQueues() throws Exception {
        Channel channel;
        channel = pool.borrowObject();
        //Create the Queues
        channel.queueDeclare(LIKE_Q, true, false, false, null);
        channel.queueDeclare(POTENTIAL_Q, true, false, false, null);
        pool.returnObject(channel);
    }

    public void declareBindings() throws Exception {
        Channel channel;
        channel = pool.borrowObject();
        //Create bindings - (queue, exchange, routingKey) - routingKey != null
        channel.queueBind(LIKE_Q, FANOUT_EXCHANGE, "");
        channel.queueBind(POTENTIAL_Q, FANOUT_EXCHANGE, "");
        pool.returnObject(channel);
    }

    public boolean publishMessage(String swipe) throws Exception {
        try {
            Channel channel;
            channel = pool.borrowObject();
            channel.basicPublish("my-fanout-exchange", "", null, swipe.getBytes());
            pool.returnObject(channel);
            return true;
        } catch (Exception ex) {
            System.out.println("Failed to publish swipe message to RabbitMQ");
            return false;
        }
    }
}
