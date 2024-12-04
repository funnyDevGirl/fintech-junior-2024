package org.tbank.rabbitmq.benchmarks;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class RabbitConsumer {
    private final Channel channel;
    private static final String QUEUE_NAME = "test_queue";
    private static final AtomicInteger messageCount = new AtomicInteger(0);

    public RabbitConsumer(Channel channel) {
        this.channel = channel;
    }

    public void consumeMessages() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            confirmMessage(channel, deliveryTag);
            messageCount.incrementAndGet();
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    public static void confirmMessage(Channel channel, long deliveryTag) throws IOException {
        channel.basicAck(deliveryTag, false);
    }

    public void close() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }
}
