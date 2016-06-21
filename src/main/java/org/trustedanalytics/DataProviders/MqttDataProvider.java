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
package org.trustedanalytics.DataProviders;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trustedanalytics.serviceinfo.MqttProperties;

public class MqttDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(MqttDataProvider.class);

    private final MqttProperties mqttProperties;
    private final OnMqttMessageArrived onMqttMessageArrived;
    private IMqttClient mqttClient;

    public MqttDataProvider(MqttProperties mqttProperties, OnMqttMessageArrived onMqttMessageArrived) {
        this.mqttProperties = mqttProperties;
        this.onMqttMessageArrived = onMqttMessageArrived;
    }

    public void init() throws MqttException {
        String url = mqttProperties.getHostname() + ":" + mqttProperties.getPort();
        LOG.info("Opening MQTT connection: '{}'", url);

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(mqttProperties.getUsername());
        connectOptions.setPassword(mqttProperties.getPassword().toCharArray());

        mqttClient = new MqttClient(url, mqttProperties.getTopic(), new MemoryPersistence());
        mqttClient.setCallback(onMqttMessageArrived);
        mqttClient.connect(connectOptions);
        mqttClient.subscribe(mqttProperties.getTopic());
    }
}
