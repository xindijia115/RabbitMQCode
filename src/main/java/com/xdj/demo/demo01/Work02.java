package com.xdj.demo.demo01;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/11/20/23:31
 */
public class Work02 {
    public static String QUEUE_NAME = "hello";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("接收到消息" + message);
        };
        CancelCallback cancelCallback = s -> System.out.println(s + "消费者取消消费接口回调逻辑");
        System.out.println("c2等待消费");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);

    }
}
