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

package org.trustedanalytics.process;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class DataConsumer implements ProcessConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(DataConsumer.class);
    private final Function<float[], Boolean> scoringEngine;
    private final Consumer<Double> scoringStore;
    private final Consumer<Double[]> featuresStore;

    public DataConsumer(Function<float[], Boolean> scoringEngine,
                        Consumer<Double> scoringStore, Consumer<Double[]> featuresStore) {
        this.scoringEngine = scoringEngine;
        this.scoringStore = scoringStore;
        this.featuresStore = featuresStore;
    }

    @Override
    public void processMessage(float[] message) {
        LOG.debug("message: {}", message);


        // minimal requirement is a class and one characteristics value
        if (message.length < 2) {
            LOG.warn(
                    "Bad input data format: we're looking for at least 2 array elements, but got only {}",
                    message.length);
            return;
        }

        float score = message[0];
        float[] featureVector = Arrays.copyOfRange(message, 1, message.length);

        LOG.debug("score: {}", score);
        LOG.debug("featureVector: {}", featureVector);

        try {
            boolean isNormal = scoringEngine.apply(featureVector);
            if (!isNormal) {
                LOG.debug("Anomaly detected - store info in Influx");
                scoringStore.accept((double) score);
            } else {
                LOG.debug("No anomaly detected");
            }

            double[] prefix = { isNormal ? 0d : 1d };
            double[] row = ArrayUtils.addAll(prefix, Doubles.toArray(Floats.asList(message)));

            featuresStore.accept(ArrayUtils.toObject(row));
        }
        catch (Exception ex) {
            LOG.error("Procesing feature vector failed", ex);
        }
    }
}
