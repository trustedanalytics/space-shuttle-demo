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

import org.trustedanalytics.scoringengine.ATKScoringEngine;
import org.trustedanalytics.scoringengine.ATKScoringProperties;
import org.trustedanalytics.storage.DataStore;
import org.trustedanalytics.storage.InfluxDataStore;
import org.trustedanalytics.storage.StoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    @Autowired
    private StoreProperties storeProperties;

    @Autowired
    private ATKScoringProperties scoreProperties;

    @Bean
    public DataStore store() {
        LOG.debug("influx config: " + storeProperties);
        LOG.info("Connecting to influxdb instance on " + storeProperties.getFullUrl());
        return new InfluxDataStore(storeProperties);
    }

    @Bean
    protected ATKScoringEngine scoringEngine() {
        LOG.info("Creating ATKScoringEngline with url" + scoreProperties.getBaseUrl());
        return new ATKScoringEngine(scoreProperties);
    }
}
