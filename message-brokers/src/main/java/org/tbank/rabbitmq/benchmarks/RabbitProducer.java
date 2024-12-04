package org.tbank.rabbitmq.benchmarks;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Getter
public class RabbitProducer {
    private final Channel channel;
    private static final String QUEUE_NAME = "test_queue";

    public RabbitProducer() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
        setupQueue(this.channel);
    }

    private void setupQueue(Channel channel) throws IOException {
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    public void produceMessages(int count, byte[] message) throws IOException {
        for (int i = 0; i < count; i++) {
            channel.basicPublish("", QUEUE_NAME, null, message);
        }
    }

    public void close() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }
}
