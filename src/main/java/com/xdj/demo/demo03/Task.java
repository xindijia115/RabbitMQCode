package com.xdj.demo.demo03;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息持久化
 * @author xia
 * @since 2023/11/22/15:30
 */
public class Task {
    public static String QUEUE_NAME = "durable_queue";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        boolean durable = true;//表示队列开启持久化
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        String message = "辛迪加有限公司";
        channel.basicQos(1);
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    }
}
