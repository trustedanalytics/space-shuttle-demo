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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.serviceinfo.InfluxDbServiceInfo;
import org.trustedanalytics.serviceinfo.ScoringEngineServiceInfo;
import org.trustedanalytics.serviceinfo.ScoringEngineServiceInfoCreator;
import org.trustedanalytics.storage.StoreProperties;

@Configuration
@Profile("cloud")
public class ServicesConfig {

    public static final String INFLUXDB_ID = "influxdb";

    @Autowired
    private Cloud cloud;
    
    @Bean
    public StoreProperties storeProperties() {
        InfluxDbServiceInfo influxDbServiceInfo = (InfluxDbServiceInfo) cloud.getServiceInfo(INFLUXDB_ID);
        return influxDbServiceInfo.getStoreProperties();
    }

    @Bean
    public String scoringEngineUrl() {
        ScoringEngineServiceInfo scoringEngineServiceInfo =
                (ScoringEngineServiceInfo) cloud.getServiceInfo(ScoringEngineServiceInfoCreator.SCORING_ENGINE_ID);
        return scoringEngineServiceInfo.getUri();
    }
}
