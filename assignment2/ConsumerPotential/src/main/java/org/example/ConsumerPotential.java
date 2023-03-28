package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

public class ConsumerPotential implements Runnable{
    private static final String SERVER = "172.31.29.252";
    private static final String USER = "rabbit";
    private static final String PASSWORD = "rabbit";
    private static final String FANOUT_EXCHANGE = "my-fanout-exchange";
    private static final String QUEUE_NAME = "PotentialQ";
    private static final int MAX_QUEUE = 20;

    private Connection connection;
    private Channel channel;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> mapPotential;

    public ConsumerPotential(Connection connection,  ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map) {
        this.connection = connection;
        this.mapPotential = map;
    }

    @Override
    public void run() {
        try {
            channel = connection.createChannel();
            declareQueues();
            declareExchange();
            declareBindings();
            subscribeMessage();
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void declareExchange() throws Exception {
        channel.exchangeDeclare(FANOUT_EXCHANGE, BuiltinExchangeType.FANOUT, true);
    }

    public void declareQueues() throws Exception {
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    }

    public void declareBindings() throws Exception {
        channel.queueBind(QUEUE_NAME, FANOUT_EXCHANGE, "");
    }

    public void subscribeMessage() throws IOException, TimeoutException {
        // max one message per receiver
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            saveInfo(message);
//            System.out.println(message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        // process messages
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }


    public void saveInfo(String msg){
        String[] msgs = msg.split("\\+");
        Gson gson = new Gson();
        Swipe swipe = gson.fromJson(msgs[0].toString(), Swipe.class);
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
            Thread con = new Thread(new ConsumerPotential(connection, new ConcurrentHashMap<>()));
            con.start();
        }
    }
}