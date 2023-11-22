package com.xdj.demo.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 工具类
 *
 * @author xia
 * @since 2023/11/20/23:12
 */
public class RabbitMqUtils {
    public static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("43.140.208.52");
        factory.setUsername("admin");
        factory.setPassword("admin123456");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }
}
