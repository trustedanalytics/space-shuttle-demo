/**
 * Copyright (c) 2015 Intel Corporation
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.serviceinfo.GatewayServiceInfo;
import org.trustedanalytics.serviceinfo.InfluxDbServiceInfo;
import org.trustedanalytics.serviceinfo.MqttProperties;
import org.trustedanalytics.serviceinfo.ZookeeperServiceInfo;
import org.trustedanalytics.serviceinfo.MqttServiceInfo;
import org.trustedanalytics.serviceinfo.ScoringEngineServiceInfo;
import org.trustedanalytics.storage.StoreProperties;

@Configuration
@Profile("cloud")
public class ServicesConfig {

    public static final Logger LOG = LoggerFactory.getLogger(ServicesConfig.class);
    public static final String GATEWAY_ID = "gateway";
    public static final String INFLUXDB_ID = "influxdb";
    public static final String SCORING_ENGINE_ID = "scoring-engine";
    public static final String ZOOKEEPER_ID = "zookeeper";
    public static final String MQTT_ID = "mosquitto14";

    @Autowired
    private Cloud cloud;

    @Bean
    public String zookeeperCluster() {
        ZookeeperServiceInfo zookeeperServiceInfo = (ZookeeperServiceInfo) cloud.getServiceInfo(ZOOKEEPER_ID);
        return zookeeperServiceInfo.getCluster();
    }

    @Bean
    public String gatewayUrl() {
        GatewayServiceInfo gatewayServiceInfo = (GatewayServiceInfo) cloud.getServiceInfo(GATEWAY_ID);
        return gatewayServiceInfo.getUri();
    }

    @Bean
    public StoreProperties storeProperties() {
        InfluxDbServiceInfo influxDbServiceInfo = (InfluxDbServiceInfo) cloud.getServiceInfo(INFLUXDB_ID);
        return influxDbServiceInfo.getStoreProperties();
    }

    @Bean
    public MqttProperties mqttProperties() {
        MqttServiceInfo mqttServiceInfo = (MqttServiceInfo) cloud.getServiceInfo(MQTT_ID);
        return mqttServiceInfo.getMqttProperties();
    }

    @Bean
    public String scoringEngineUrl() {
        ScoringEngineServiceInfo scoringEngineServiceInfo =
                (ScoringEngineServiceInfo) cloud.getServiceInfo(SCORING_ENGINE_ID);
        return scoringEngineServiceInfo.getUri();
    }
}
