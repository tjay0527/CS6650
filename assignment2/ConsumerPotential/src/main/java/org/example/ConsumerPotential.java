package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ConsumerPotential implements Runnable{
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> potential;
    private static final String SERVER = "35.92.131.62";
    private static final String USER = "rabbit";
    private static final String PASSWORD = "rabbit";
    private static final String QUEUE_NAME = "test";
    private static final int MAX_QUEUE = 20;
    private Connection connection;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> mapPotential;

    public ConsumerPotential(Connection connection,  ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map) {
        this.connection = connection;
        this.mapPotential = map;
    }

    @Override
    public void run() {
        try {
            final Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // max one message per receiver
            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            // process messages
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void saveInfo(String msg){
        String[] msgs = msg.split("\\+");
        Gson gson = new Gson();
        Swipe swipe = (Swipe) gson.fromJson(msgs[0].toString(), Swipe.class);
        mapPotential.computeIfAbsent(swipe.getSwiper(), k->new ConcurrentLinkedQueue<String>()).offer(swipe.getSwipee());
    }

    public ConcurrentLinkedQueue<String> getPotential(String id){
        return mapPotential.get(id);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(SERVER);
        factory.setUsername(USER);
        factory.setPassword(PASSWORD);
        Connection connection = factory.newConnection();
        for(int i = 0; i < MAX_QUEUE; i++){
            Thread con = new Thread(new ConsumerPotential(connection, new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>()));
            con.start();
        }
    }
}