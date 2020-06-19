package org.playpi.study.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class SimpleConsumer {

    public static void main(String[] args) {
        new SimpleConsumer().consume();
    }

    private static KafkaConsumer<String, String> consumer;
    private final static String TOPIC = "topic_test_v1";


    public void consume() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "dev2:6667");
//        props.put("zookeeper.connect", "dev2:2181");
//
        //每个消费者分配独立的组号
        props.put("group.id", "consumer_group_test");
        //如果value合法，则自动提交偏移量
        props.put("enable.auto.commit", "true");
//        //设置多久一次更新被消费消息的偏移量
        props.put("auto.commit.interval.ms", "100");
//        //设置会话响应的时间，超过这个时间kafka可以选择放弃消费或者消费下一条消息
        props.put("session.timeout.ms", "10000");
//        //自动重置offset
//        props.put("auto.offset.reset", "smallest");
//        props.put("auto.offset.reset", "largest");
//        props.put("auto.offset.reset", "earliest");
        props.put("auto.offset.reset", "latest");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(TOPIC));
        while (true) {
            System.out.println("start..");
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
                System.out.println();
            }
            consumer.commitAsync();
        }
    }

}
