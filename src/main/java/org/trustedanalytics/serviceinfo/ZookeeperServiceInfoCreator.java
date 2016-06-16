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

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;
import org.trustedanalytics.config.ServicesConfig;

import java.util.Map;

public class ZookeeperServiceInfoCreator extends CloudFoundryServiceInfoCreator<ZookeeperServiceInfo> {

    private static final String POSTFIX = "/kafka";

    public ZookeeperServiceInfoCreator() {
        super(new Tags(ServicesConfig.ZOOKEEPER_ID));
    }

    @Override
    public ZookeeperServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        Map<String, Object> credentials = getCredentials(serviceData);
        String cluster = (String) credentials.get("zk.cluster");
        return new ZookeeperServiceInfo(ServicesConfig.ZOOKEEPER_ID, cluster + POSTFIX);
    }
}
