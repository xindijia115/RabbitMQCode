package com.xdj.demo.demo06;

import com.rabbitmq.client.*;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/11/16:05
 */
public class ReceiveLogsDirect01 {
    private final static String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //队列名称
        String queueName = "disk";
        channel.queueDeclare(queueName, false, false, false, null);
        //将队列和交换机通过bandingKey绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
        System.out.println("waiting for message ...");
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            message = "接收绑定键：" + delivery.getEnvelope().getRoutingKey() + "\t接收到的消息：" + message;
            System.out.println(message);
        };
        CancelCallback cancelCallback = s -> System.out.println("消息取消时回调...");
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
