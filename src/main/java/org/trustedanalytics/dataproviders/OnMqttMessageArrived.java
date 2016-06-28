/**
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.dataproviders;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trustedanalytics.process.DataConsumer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OnMqttMessageArrived implements MqttCallback {

    private static final Logger LOG = LoggerFactory.getLogger(OnMqttMessageArrived.class);

    private final DataConsumer dataConsumer;

    public OnMqttMessageArrived(DataConsumer dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    @Override public void connectionLost(Throwable throwable) {
        LOG.error("Connection lost. " + throwable);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        LOG.debug("message: {}", mqttMessage);

        final List<Float> dataVector = getDataVector(mqttMessage);
        dataConsumer.processMessage(messageAsFloatArray(dataVector));
    }

    private List<Float> getDataVector(MqttMessage mqttMessage) {
        return Arrays.stream(mqttMessage.toString().split(","))
                .map(Float::parseFloat)
                .collect(Collectors.toList());
    }

    private float[] messageAsFloatArray(List<Float> message) {
        return ArrayUtils.toPrimitive(message.toArray(new Float[0]), 0.0F);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.debug("deliveryComplete: " + iMqttDeliveryToken);
    }
}
