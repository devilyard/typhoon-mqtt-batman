/*
 * Copyright (c) 2019. All rights reserved.
 * SubscribePromiseImpl.java created at 2019-10-08 11:33:12
 * This file is for internal use only, it is belong to TYPHOON,
 * you cannot redistribute it nor modify it for any purpose.
 */
package org.typhoon.batman.client.handler.outbound;

import org.typhoon.batman.MqttQoS;
import org.typhoon.batman.client.MessageListener;
import org.typhoon.batman.client.message.MqttSubAckMessage;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author C.
 */
public class SubscribePromiseImpl extends AbstractActionPromise<MqttSubAckMessage> implements SubscribePromise {

    private final Map<String, Map<MqttQoS, MessageListener>> subscriptions;

    public SubscribePromiseImpl(Map<String, Map<MqttQoS, MessageListener>> subscriptions, ExecutorService executorService) {
        super(executorService);
        this.subscriptions = subscriptions;
    }

    @Override
    public Map<String, Map<MqttQoS, MessageListener>> getSubscriptions() {
        return subscriptions;
    }

}
