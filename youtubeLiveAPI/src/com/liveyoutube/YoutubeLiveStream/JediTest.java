package com.liveyoutube.YoutubeLiveStream;


import kotlin.reflect.jvm.internal.impl.javax.inject.Singleton;
import redis.clients.jedis.Jedis;

// singleton class for Jedis
// Run the main method after registering a java redis cluster to check if redis works.
@Singleton
public class JediTest {
    public static Jedis jPublisher;
    static {
        jPublisher = new Jedis("localhost");
    }

    public static void testJedis() {
        jPublisher.publish("channel", "test message");
    }

    public static void postJedis(String message) {
        jPublisher.publish("channel", message);
    }

    public static void main(String args[]){
        testJedis();
        postJedis("hey there");
        postJedis("yo");
    }
}
