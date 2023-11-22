package com.xdj.demo.demo02;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.xdj.demo.utils.RabbitMqUtils;
import com.xdj.demo.utils.SleepUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/11/22/14:56
 */
public class Work02 {
    public static String QUEUE_NAME = "xindijia";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("c2等待接收处理消息的时间较长");
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "utf-8");
            SleepUtils.sleep(30);//睡眠三十秒
            System.out.println("接收到消息" + message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = s -> System.out.println("消息取消时回调处理逻辑");
        boolean autoAck = false;//手动应答
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
