/*
 * Copyright (c) 2019. All rights reserved.
 * MqttInboundPublishMessageHandler.java created at 2019-10-08 11:33:12
 * This file is for internal use only, it is belong to TYPHOON,
 * you cannot redistribute it nor modify it for any purpose.
 */
package io.github.batman.client.handler.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.batman.MqttQoS;
import io.github.batman.client.ClientContext;
import io.github.batman.client.SubscribedMessageNotifier;
import io.github.batman.client.message.MqttPacketIdVariableHeader;
import io.github.batman.client.message.MqttPubAckMessage;
import io.github.batman.client.message.MqttPubRecMessage;
import io.github.batman.client.message.MqttPublishMessage;

import static io.github.batman.client.handler.MqttMessageHandlerHelper.appendToPendingZone;

/**
 * @author C.
 */
public class MqttInboundPublishMessageHandler implements InboundMqttMessageHandler<MqttPublishMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttInboundPublishMessageHandler.class);

    @Override
    public void handle(MqttPublishMessage message, ClientContext context) {
        MqttQoS qos = message.fixedHeader().qosLevel();
        int packetId = message.variableHeader().packetId();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Processing PUBLISH message with qos level: {}, topic: {}, packetId: {}", qos, message.variableHeader().topicName(), packetId);
        }
        if (qos == MqttQoS.AT_MOST_ONCE) {
            SubscribedMessageNotifier messageNotifier = context.getMessageNotifier();
            messageNotifier.notifyMessage(message);
        } else if (qos == MqttQoS.AT_LEAST_ONCE) {
            SubscribedMessageNotifier messageNotifier = context.getMessageNotifier();
            messageNotifier.notifyMessage(message);

            MqttPacketIdVariableHeader variableHeader = new MqttPacketIdVariableHeader(packetId);
            MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(variableHeader);
            appendToPendingZone(pubAckMessage, context);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Sending PUBACK message, packetId: {}", packetId);
            }
        } else {
            context.getClientSession().inFlight(message);
            MqttPacketIdVariableHeader variableHeader = new MqttPacketIdVariableHeader(packetId);
            MqttPubRecMessage pubRecMessage = new MqttPubRecMessage(variableHeader);
            appendToPendingZone(pubRecMessage, context);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Sending PUBREC message, packetId: {}", packetId);
            }
        }
    }
}
