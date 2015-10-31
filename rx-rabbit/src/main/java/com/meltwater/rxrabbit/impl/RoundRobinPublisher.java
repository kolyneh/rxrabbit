package com.meltwater.rxrabbit.impl;

import com.google.common.collect.Iterables;
import com.meltwater.rxrabbit.RabbitPublisher;
import com.rabbitmq.client.AMQP;
import rx.Single;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class RoundRobinPublisher implements RabbitPublisher {

    private final List<RabbitPublisher> backingPublishers;
    private final Iterator<RabbitPublisher> publisherIterator;

    public RoundRobinPublisher(List<RabbitPublisher> backingPublishers) {
        assert !backingPublishers.isEmpty();
        this.backingPublishers = backingPublishers;
        this.publisherIterator = Iterables.cycle(backingPublishers).iterator();
    }

    @Override
    public String getExchange() {
        return backingPublishers.get(0).getExchange();
    }

    @Override
    public Single<Void> publish(String routingKey, AMQP.BasicProperties props, byte[] payload) {
        return publisherIterator.next().publish(routingKey,props,payload);
    }

    @Override
    public void close() throws IOException {
        for (RabbitPublisher backingPublisher : backingPublishers) {
            backingPublisher.close();
        }
    }

}
