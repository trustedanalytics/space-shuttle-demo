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
package org.trustedanalytics.storage;

import lombok.Data;

import java.util.Map;

@Data
public class StoreProperties {

    private static final String DEFAULT_GROUPING_INTERVAL = "1m";
    private static final String DEFAULT_TIME_LIMIT = "1h";
    private static final String DATABASE_NAME = "space-shuttle-demo";
    private static final String URI_SCHEME = "http://";

    private String baseUrl;
    private String apiPort;
    private String username;
    private String password;
    private String defaultGroupingInterval;
    private String defaultTimeLimit;
    private String databaseName;

    public StoreProperties(Map<String, Object> serviceCredentials) {
        this(serviceCredentials, DEFAULT_GROUPING_INTERVAL, DEFAULT_TIME_LIMIT, DATABASE_NAME);
    }

    public StoreProperties(Map<String, Object> serviceCredentials, String defaultGroupingInterval,
                           String defaultTimeLimit, String databaseName) {
        this.baseUrl = URI_SCHEME + (String) serviceCredentials.get("hostname");
        this.apiPort = (String) ((Map<String, Object>)serviceCredentials.get("ports")).get("8086/tcp");
        this.username = (String) serviceCredentials.get("username");
        this.password = (String) serviceCredentials.get("password");
        this.defaultGroupingInterval = defaultGroupingInterval;
        this.defaultTimeLimit = defaultTimeLimit;
        this.databaseName = databaseName;
    }

    public String getFullUrl() {
        return baseUrl + ":" + apiPort;
    }
}
