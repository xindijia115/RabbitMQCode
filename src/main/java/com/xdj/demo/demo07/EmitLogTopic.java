package com.xdj.demo.demo07;

import com.rabbitmq.client.Channel;
import com.xdj.demo.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author xia
 * @since 2023/12/11/16:48
 */
public class EmitLogTopic {
    private final static String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        /**
         * Q1-->绑定的是
         * 中间带 orange 带 3 个单词的字符串(*.orange.*)
         * Q2-->绑定的是
         * 最后一个单词是 rabbit 的 3 个单词(*.*.rabbit)
         * 第一个单词是 lazy 的多个单词(lazy.#)
         *
         */
        HashMap<String, String> bandingKeys = new HashMap<>();
        bandingKeys.put("quick.orange.rabbit","被队列 Q1Q2 接收到");
        bandingKeys.put("lazy.orange.elephant","被队列 Q1Q2 接收到");
        bandingKeys.put("quick.orange.fox","被队列 Q1 接收到");
        bandingKeys.put("lazy.brown.fox","被队列 Q2 接收到");
        bandingKeys.put("lazy.pink.rabbit","虽然满足两个绑定但只被队列 Q2 接收一次");
        bandingKeys.put("quick.brown.fox","不匹配任何绑定不会被任何队列接收到会被丢弃");
        bandingKeys.put("quick.orange.male.rabbit","是四个单词不匹配任何绑定会被丢弃");
        bandingKeys.put("lazy.orange.male.rabbit","是四个单词但匹配 Q2");
        for (Map.Entry<String, String> stringStringEntry : bandingKeys.entrySet()) {
            String bandingKey = stringStringEntry.getKey();
            String message = stringStringEntry.getValue();
            channel.basicPublish(EXCHANGE_NAME, bandingKey, null, message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:" + message);
        }

    }
}
