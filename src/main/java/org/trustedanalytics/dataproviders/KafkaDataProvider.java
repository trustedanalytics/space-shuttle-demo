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

import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trustedanalytics.process.DataConsumer;

public class KafkaDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaDataProvider.class);

    private final DataConsumer dataConsumer;
    private final KafkaStream<String, float[]> kafkaStream;

    public KafkaDataProvider(DataConsumer dataConsumer, KafkaStream<String, float[]> kafkaStream) {
        this.dataConsumer = dataConsumer;
        this.kafkaStream = kafkaStream;
    }

    public void init() {
        new Thread(() -> {
            LOG.info("start consumer thread");
            kafkaStream.forEach(v -> dataConsumer.processMessage(v.message()));
        }).start();
    }
}
