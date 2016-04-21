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

package org.trustedanalytics.serviceinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;
import org.trustedanalytics.config.ServicesConfig;
import org.trustedanalytics.storage.StoreProperties;

import java.util.Map;

public class InfluxDbServiceInfoCreator extends CloudFoundryServiceInfoCreator<InfluxDbServiceInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(InfluxDbServiceInfoCreator.class);

    public InfluxDbServiceInfoCreator() {
        super(new Tags(ServicesConfig.INFLUXDB_ID));
    }

    @Override
    public boolean accept(Map<String, Object> serviceData) {
        String label = (String) serviceData.get("label");
        return label.startsWith(ServicesConfig.INFLUXDB_ID);
    }

    @Override
    public InfluxDbServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        StoreProperties storeProperties = getStoreProperties(serviceData);
        return new InfluxDbServiceInfo(ServicesConfig.INFLUXDB_ID, storeProperties);
    }

    private StoreProperties getStoreProperties(Map<String, Object> serviceData) {
        Map<String, Object> credentials = getCredentials(serviceData);
        return new StoreProperties(credentials);
    }
}