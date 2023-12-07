package com.xdj.demo.demo05;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import com.xdj.demo.utils.RabbitMqUtils;

import java.io.*;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/7/16:03
 */
public class ReceiveLogs02 {//消费者2
    private final static String EXCHANGE_NAME = "logs";
    public static void main(String[] args) throws IOException, TimeoutException {
        //声明交换机
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        //生成一个临时队列，当消费者断开时该队列会自动删除
        String queue = channel.queueDeclare().getQueue();
        //将交换机和队列进行绑定
        channel.queueBind(queue,EXCHANGE_NAME , "");//这里的routingkey 暂时为空串
        System.out.println("等待接收消息...");
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src\\\\data.txt"), "UTF-8"));
            System.out.println("将消息写入文件");
            bufferedWriter.write(message);
        };
        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});
    }
}
