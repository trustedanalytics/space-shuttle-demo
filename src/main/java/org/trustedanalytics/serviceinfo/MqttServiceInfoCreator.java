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
package org.trustedanalytics.serviceinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;
import org.trustedanalytics.config.ServicesConfig;

import java.util.Map;

public class MqttServiceInfoCreator extends CloudFoundryServiceInfoCreator<MqttServiceInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(MqttServiceInfoCreator.class);

    public MqttServiceInfoCreator() {
        super(new Tags(ServicesConfig.MQTT_ID));
    }

    @Override
    public boolean accept(Map<String, Object> serviceData) {
        String label = (String) serviceData.get("label");
        return label.equals(ServicesConfig.MQTT_ID);
    }

    @Override
    public MqttServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        MqttProperties mqttProperties = getMqttProperties(serviceData);
        return new MqttServiceInfo(ServicesConfig.MQTT_ID, mqttProperties);
    }

    private MqttProperties getMqttProperties(Map<String, Object> serviceData) {
        Map<String, Object> credentials = getCredentials(serviceData);
        return new MqttProperties(credentials);
    }
}
