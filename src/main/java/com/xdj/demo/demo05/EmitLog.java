package com.xdj.demo.demo05;

import com.rabbitmq.client.Channel;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/7/16:34
 */
public class EmitLog {//生产者
    private final static String EXCHANGE_NAME = "logs";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("发送消息:" + message);
        }
    }
}
