package com.meltwater.rxrabbit;

import com.rabbitmq.client.BasicProperties;

/**
 * Convenience enum to map integer tags to descriptive delivery mode names
 *
 * @see BasicProperties#getDeliveryMode()
 * @see <a href="https://www.rabbitmq.com/amqp-0-9-1-reference.html">amqp-0-9-1-reference</a>
 */
public enum DeliveryMode {
    non_persistent(1),
    persistent(2);

    public final int code;
    DeliveryMode(int code) {
        this.code = code;
    }
}
