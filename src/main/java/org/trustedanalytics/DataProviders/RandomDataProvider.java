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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.process.DataConsumer;

import java.util.Random;

@Profile("random")
public class RandomDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(RandomDataProvider.class);
    private Random randomGenerator = new Random();

    @Autowired
    DataConsumer dataConsumer;

    public float[] generateRandomFloatArray() {
        float[] randomArray = new float[10];
        randomArray[0] = randomGenerator.nextInt(5) + 1;
        for (int i = 1; i < 10; i++) {
            randomArray[i] = randomGenerator.nextFloat() * 100;
        }
        return randomArray;
    }

    public void init() {
        new Thread(() -> {
            LOG.info("start consumer thread");
            while (true) {
                try {
                    Thread.sleep(1000);
                    dataConsumer.processMessage(generateRandomFloatArray());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
