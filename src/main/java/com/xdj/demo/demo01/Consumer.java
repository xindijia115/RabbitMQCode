package com.xdj.demo.demo01;

import com.rabbitmq.client.*;

/**
 * @author xia
 * @since 2023/11/11/15:50
 */
public class Consumer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("43.140.208.52");
        factory.setUsername("admin");
        factory.setPassword("admin123456");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信息管道
        Channel channel = connection.createChannel();
        System.out.println("等待接收消息.........");

        //函数式编程
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {//将接收到的消息放到这里面来
            String message = new String(delivery.getBody());
            System.out.println(message);
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费被中断");
        };
        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者未成功消费的回调
         * 4.消息被取消时的回调
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}