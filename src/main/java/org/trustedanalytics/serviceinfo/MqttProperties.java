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
package org.trustedanalytics.serviceinfo;

import lombok.Data;

import java.util.Map;

@Data
public class MqttProperties {

    private static final String URI_SCHEME = "tcp://";
    private static final String CLIENT_NAME = "space-shuttle-mqtt-demo";
    private static final String TOPIC = "space-shuttle/test-data";

    private String hostname;
    private String port;
    private String username;
    private String password;
    private String clientName;
    private String topic;

    public MqttProperties(Map<String, Object> serviceCredentials) {
        this(serviceCredentials, CLIENT_NAME, TOPIC);
    }

    public MqttProperties(Map<String, Object> serviceCredentials, String clientName, String topic) {
        this.hostname = URI_SCHEME + (String) serviceCredentials.get("hostname");
        this.port = (String) ((Map<String, Object>)serviceCredentials.get("ports")).get("1883/tcp");
        this.username = (String) serviceCredentials.get("username");
        this.password = (String) serviceCredentials.get("password");
        this.clientName = clientName;
        this.topic = topic;
    }
}
