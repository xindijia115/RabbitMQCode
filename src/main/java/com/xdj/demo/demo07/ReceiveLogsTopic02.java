package com.xdj.demo.demo07;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/11/16:48
 */
public class ReceiveLogsTopic02 {
    private final static String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //声明队列
        String queueName = "Q2";
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "*.*.rabbit");
        channel.queueBind(queueName, EXCHANGE_NAME, "lazy.#");
        System.out.println("waiting for message ...");
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            message = "接收队列：" + queueName + "\t绑定键：" + delivery.getEnvelope().getRoutingKey() + "\t消息：" + message;
            System.out.println(message);
        };
        CancelCallback cancelCallback = s -> System.out.println("消息取消时回调");
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
