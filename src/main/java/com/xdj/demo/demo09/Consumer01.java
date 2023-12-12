package com.xdj.demo.demo09;

import com.rabbitmq.client.*;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/11/21:07
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
        //设置正常队列的长度限制
        params.put("x-max-length", 6);

        //声明正常队列
        String normalQueue = "normal-queue";
        channel.queueDeclare(normalQueue, false, false, false, params);

        //绑定
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "lisi");
        channel.queueBind(normalQueue, NORMAL_EXCHANGE, "zhansan");
        System.out.println("waiting for message ...");
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("消费者1接收到消息：" + message);
        };
        CancelCallback cancelCallback = s -> System.out.println("消息取消时回调");
        channel.basicConsume(normalQueue, true, deliverCallback, cancelCallback);

    }
}
