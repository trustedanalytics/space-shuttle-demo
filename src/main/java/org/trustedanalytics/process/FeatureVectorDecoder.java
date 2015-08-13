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
package org.trustedanalytics.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.serializer.Decoder;

public class FeatureVectorDecoder implements Decoder<float[]> {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureVectorDecoder.class);
    private static float[] EMPTY_VALUE = new float[] {};
	
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public float[] fromBytes(byte[] data) {
        try {
            Message msg = mapper.readValue(data, Message.class);
            return mapper.readValue(msg.getBody(), float[].class);
        } catch (Exception e) {
            LOG.error("Cannot parse the message", e);
            return EMPTY_VALUE;
        }
    }

}
