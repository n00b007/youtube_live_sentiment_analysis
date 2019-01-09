package com.liveyoutube.YoutubeLiveStream;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

// create a class KafkaTest which has a singleton producer to send the comments to pyspark.
public class KafkaTest {

    static Properties configProperties;

    static Producer producer;

    // topic to send the stream to.
    private static String TOPIC_NAME = "hello-world";

    static {
        producer = init();
    }


    public static Producer init() {
        org.apache.log4j.BasicConfigurator.configure();

        configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer(configProperties);
    }

    public static void main(String[] args) throws Exception {

        // Register a kafka-consumer listening on port 9092 on localhost
        // run the main function and you can see 10 -> 1 printed on the console of the consumer.
        org.apache.log4j.BasicConfigurator.configure();

        String topicName = "hello-world";

        Properties properties = new Properties();

        configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer producer = new KafkaProducer(configProperties);


        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(i)));
            System.out.println("Message sent successfully");


        }
        producer.close();


    }

    // function used to send the message to pyspark.
    public static void sendMessage(String s) {
        producer.send(new ProducerRecord<String,String>(TOPIC_NAME, s));
    }
}
