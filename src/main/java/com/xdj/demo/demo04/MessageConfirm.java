package com.xdj.demo.demo04;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;
import com.xdj.demo.utils.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author xia
 * @since 2023/11/28/11:51
 */
public class MessageConfirm {
    public static void main(String[] args) throws Exception {
        //单个确认发布
        //publishMessageIndividually();//51137
        //批量消息确认
        //publishMessageBatch();//589
        //异步确认发布
        publishMessageAsync();//115
    }

    public static void publishMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认模式
        channel.confirmSelect();
        String QUEUE_NAME = UUID.randomUUID().toString();
        //声明一个队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String message = i + " ";
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            boolean flag = channel.waitForConfirms();
            //服务端返回false或者超时时间内未返回,生产者可以重发消息
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("单个确认发布耗时：" + (end - start));
    }

    public static void publishMessageBatch() throws Exception{//批量消息确认
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认模式
        channel.confirmSelect();
        String QUEUE_NAME = UUID.randomUUID().toString();
        //声明一个队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String message = i + " ";
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            if((i + 1) % 100 == 0) {//发送100条消息时批量确认一次
                channel.waitForConfirms();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("批量消息确认发布耗时：" + (end - start));
    }
    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认模式
        channel.confirmSelect();
        String QUEUE_NAME = UUID.randomUUID().toString();
        //声明一个队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        /**
         * 假如说我们有写消息没有被确认该如何处理呢？可以把已经ack的消息删掉，留下的就是nack的消息
         * 创建一个线程安全的哈希表，适用于高并发的情况
         * 1.轻松将序号和信息进行关联
         * 2.能够根据序号批量删除消息
         * 3.支持并发访问
         */
        ConcurrentSkipListMap<Long, String> lscs = new ConcurrentSkipListMap<>();

        /**
         * 添加一个异步确认的监听器
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {//消息被ack时的回调处理
            //deliveryTag表示消息对应的key序号，multiple表示是否为批量ack
            if(multiple) {//如果是批量ack
                ConcurrentNavigableMap<Long, String> longStringConcurrentNavigableMap = lscs.headMap(deliveryTag, true);
                //true表示返回小于等于当前序号的消息的map
                longStringConcurrentNavigableMap.clear();//删除
            } else {
                lscs.remove(deliveryTag);//只清除当前序号的message
            }
        };
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {//消息被nack时的回调处理
            String s = lscs.get(deliveryTag);
            System.out.println("发布的消息" + s + "未被确认，序号" + deliveryTag);
        };
        channel.addConfirmListener(ackCallback, nackCallback);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String message = "辛迪加" + i;
            lscs.put(channel.getNextPublishSeqNo(), message);//存放关系
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        }

        long end = System.currentTimeMillis();
        System.out.println("异步消息确认发布耗时：" + (end - start));
    }
}
