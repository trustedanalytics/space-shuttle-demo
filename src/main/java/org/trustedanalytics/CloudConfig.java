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

package org.trustedanalytics;

import com.google.common.base.Preconditions;
import org.trustedanalytics.process.FeatureVectorDecoder;
import org.trustedanalytics.process.ScoringProcess;
import org.trustedanalytics.scoringengine.ATKScoringEngine;
import org.trustedanalytics.storage.DataStore;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
@Profile("cloud")
public class CloudConfig {

    private final static Logger LOG = LoggerFactory.getLogger(CloudConfig.class);

    @Value("${zookeeper.connect}")
    private String zookeeper;

    @Value("${consumer.group}")
    private String consumerGroup;

    @Value("${topic}")
    private String topicName;

    @Bean
    protected ConsumerConfig consumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("group.id", consumerGroup);
        props.put("zookeeper.session.timeout.ms", "1000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }

    @Bean
    protected KafkaStream<String, float[]> kafkaStream() {
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

    @Bean(initMethod = "init", destroyMethod = "destroy")
    protected ScoringProcess scoringConsumer(ATKScoringEngine scoringEngine, DataStore store) {
        return new ScoringProcess(kafkaStream(), scoringEngine::score, store::saveClass, store::saveFeatures);
    }
}
