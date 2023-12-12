package com.xdj.demo.demo10;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/12/16:53
 */
public class Consumer02 {
    private static final String DEAD_EXCHANGE = "dead_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        String deadQueue = "dead-queue";
        channel.queueDeclare(deadQueue, false, false, false, null);
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "lisi");
        System.out.println("waiting for deadQueue message ...");
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("Consumer02 接收死信队列的消息" + message);
        };
        CancelCallback cancelCallback = s -> {
        };
        channel.basicConsume(deadQueue, true, deliverCallback, cancelCallback);
    }
}
