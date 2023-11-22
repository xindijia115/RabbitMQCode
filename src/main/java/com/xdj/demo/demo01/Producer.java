package com.xdj.demo.demo01;

import com.rabbitmq.client.Channel;
import com.xdj.demo.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author xia
 * @since 2023/11/11/15:43
 */
public class Producer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 1.队列名称
         * 2.队列里面的消息是否持久化，默认是false，即存在内存中
         * 3.true表示只可以单个消费者消费
         * 4.是否自动删除，最后一个消费者在连接断了之后该队列是否自动删除，true为删除
         * 5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //String message = "hello world";
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("消息发送完毕");
        }
        /**
         * 1.发送到哪个交换机
         * 2.路由的key是哪个
         * 3.其他的参数信息
         * 4.信息内容
         */


    }
}

