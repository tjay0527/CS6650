//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class RMQChannelFactory extends BasePooledObjectFactory<Channel> {
    private final Connection connection;
    private int count;

    public RMQChannelFactory(Connection connection) {
        this.connection = connection;
        this.count = 0;
    }

    public synchronized Channel create() throws IOException {
        ++this.count;
        Channel chan = this.connection.createChannel();
        return chan;
    }

    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject(channel);
    }

    public int getChannelCount() {
        return this.count;
    }
}
