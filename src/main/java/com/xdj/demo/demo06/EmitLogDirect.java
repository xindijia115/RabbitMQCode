package com.xdj.demo.demo06;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.xdj.demo.utils.RabbitMqUtils;

import javax.management.remote.JMXServerErrorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/11/16:05
 */
public class EmitLogDirect {
    private final static String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //创建多个bandingKey
        HashMap<String, String> bandingKeys = new HashMap<>();
        bandingKeys.put("info", "普通消息");
        bandingKeys.put("warning", "警告warning消息");
        bandingKeys.put("error", "错误error消息");
        //没有消费者接收这个消息，这个消息就会丢失
        bandingKeys.put("debug", "警告debug消息");
        for (Map.Entry<String, String> stringStringEntry : bandingKeys.entrySet()) {
            String bandingKey = stringStringEntry.getKey();
            String message = stringStringEntry.getValue();
            channel.basicPublish(EXCHANGE_NAME, bandingKey, null, message.getBytes("UTF-8"));
            System.out.println("生产者发送消息: " + message);
        }
    }
}
