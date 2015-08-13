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

package org.trustedanalytics.scoringengine;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.common.primitives.Floats;

public class ATKScoringEngine {

    private static final Logger LOG = LoggerFactory.getLogger(ATKScoringEngine.class);

    private ATKScoringProperties properties;

    public ATKScoringEngine(ATKScoringProperties properties){
        this.properties = properties;
    }

    public Boolean score(float[] data) {
        String commaSeparatedNumbers = convertToCommaSeparated(data);
        String url = getUrl() + commaSeparatedNumbers;
        Float f_result;
        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> response = template.getForEntity(url, String.class);
            String result = response.getBody();
            f_result = Float.parseFloat(result);
            LOG.debug("Score from scoring engine: {}", f_result);
        } catch (Exception ex) {
            LOG.warn("problem with getting scoring result! " + ex.getMessage(), ex);
            f_result = -100f;
        }

        return f_result == 1.0f;
    }

    private String getUrl(){
        return properties.getBaseUrl() + "/v1/models/DemoModel/score?data=";
    }

    private String convertToCommaSeparated(float[] data) {
        List<Float> dataList = Floats.asList(data);

        String commaSeparatedNumbers = dataList.stream()
            .map(i -> String.format("%.4f", i))
            .collect(Collectors.joining(","));

        return commaSeparatedNumbers;
    }
}
