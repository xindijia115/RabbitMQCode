package com.xdj.demo.demo10;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/12/16:41
 */
public class Consumer01 {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明死信队列
        String deadQueue = "dead-queue";
        channel.queueDeclare(deadQueue, false, false, false, null);

        //正常队列绑定死信队列信息
        HashMap<String, Object> params = new HashMap<>();
        //正常队列设置死信交换机 参数 key 是固定值
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信 routing-key 参数 key 是固定值
        params.put("x-dead-letter-routing-key", "lisi");

        //声明正常队列
        String normalQueue = "normal-queue";
        channel.queueDeclare(normalQueue, false, false, false, params);

        //绑定
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "lisi");
        channel.queueBind(normalQueue, NORMAL_EXCHANGE, "zhansan");
        System.out.println("waiting for message ...");
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            if(message.equals("info5")) {
                System.out.println("Consumer01接收到消息：" + message + " 并且拒绝了该消息");
                //false 代表拒绝重新入队 如果配置了死信交换机 该消息将被放到死信队列中
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            } else {
                System.out.println("Consumer01 接收到消息" + message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        CancelCallback cancelCallback = s -> System.out.println("消息取消时回调");
        boolean autoAck = false;//手动确认
        channel.basicConsume(normalQueue, autoAck, deliverCallback, cancelCallback);
    }
}
