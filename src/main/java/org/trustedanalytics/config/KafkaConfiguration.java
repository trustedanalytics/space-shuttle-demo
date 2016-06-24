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

import com.google.common.base.Preconditions;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.Cloud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.DataProviders.KafkaDataProvider;
import org.trustedanalytics.process.DataConsumer;
import org.trustedanalytics.process.FeatureVectorDecoder;
import org.trustedanalytics.serviceinfo.GatewayServiceInfo;
import org.trustedanalytics.serviceinfo.GatewayServiceInfoCreator;
import org.trustedanalytics.serviceinfo.ZookeeperServiceInfo;
import org.trustedanalytics.serviceinfo.ZookeeperServiceInfoCreator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
@Profile("kafka")
public class KafkaConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConfiguration.class);

    @Autowired
    private Cloud cloud;

    @Value("${consumer.group}")
    private String consumerGroup;


    @Bean
    protected KafkaStream<String, float[]> kafkaStream() {

        final String topicName = retrieveTopicNameFromGatewayAddress(gatewayUrl());
        ConsumerConnector consumerConnector =
                Consumer.createJavaConsumerConnector(consumerConfig());
        Map<String, Integer> topicCounts = new HashMap<>();
        topicCounts.put(topicName, 1);
        VerifiableProperties emptyProps = new VerifiableProperties();
        StringDecoder keyDecoder = new StringDecoder(emptyProps);
        FeatureVectorDecoder valueDecoder = new FeatureVectorDecoder();
        Map<String, List<KafkaStream<String, float[]>>> streams =
                consumerConnector.createMessageStreams(topicCounts, keyDecoder, valueDecoder);

        List<KafkaStream<String, float[]>> streamsByTopic = streams.get(topicName);
        Preconditions.checkNotNull(streamsByTopic, String.format("Topic %s not found in streams map.", topicName));
        Preconditions.checkElementIndex(0, streamsByTopic.size(),
                String.format("List of streams of topic %s is empty.", topicName));
        return streamsByTopic.get(0);
    }

    @Bean(initMethod = "init")
    public KafkaDataProvider kafkaDataProvider(DataConsumer dataConsumer, KafkaStream<String, float[]> kafkaStream) {
        return new KafkaDataProvider(dataConsumer, kafkaStream);
    }

    private ConsumerConfig consumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeperCluster());
        props.put("group.id", consumerGroup);
        props.put("zookeeper.session.timeout.ms", "1000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

    private String retrieveTopicNameFromGatewayAddress(String gatewayUrl) {
        return gatewayUrl.substring(0, gatewayUrl.indexOf('.'));
    }

    private String zookeeperCluster() {
        ZookeeperServiceInfo zookeeperServiceInfo = (ZookeeperServiceInfo) cloud.getServiceInfo(ZookeeperServiceInfoCreator.ZOOKEEPER_ID);
        return zookeeperServiceInfo.getCluster();
    }

    private String gatewayUrl() {
        GatewayServiceInfo gatewayServiceInfo = (GatewayServiceInfo) cloud.getServiceInfo(GatewayServiceInfoCreator.GATEWAY_ID);
        return gatewayServiceInfo.getUri();
    }
}
