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
package org.trustedanalytics.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.DataProviders.MqttDataProvider;
import org.trustedanalytics.DataProviders.OnMqttMessageArrived;
import org.trustedanalytics.process.DataConsumer;
import org.trustedanalytics.serviceinfo.MqttProperties;
import org.trustedanalytics.storage.DataStore;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("mqtt")
public class MqttConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MqttConfiguration.class);

    @Bean(initMethod = "init")
    public MqttDataProvider mqttDataProvider(DataStore store, MqttProperties mqttProperties, DataConsumer dataConsumer) {
        LOG.debug("Mqtt properties: " + mqttProperties);
        return new MqttDataProvider(mqttProperties, new OnMqttMessageArrived(dataConsumer));
    }
}
