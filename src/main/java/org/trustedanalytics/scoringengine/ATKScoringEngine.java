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

import com.google.common.primitives.Floats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

public class ATKScoringEngine {

    private static final Logger LOG = LoggerFactory.getLogger(ATKScoringEngine.class);

    private String scoringEngineUrl;

    public ATKScoringEngine(String scoringEngineUrl){
        this.scoringEngineUrl = scoringEngineUrl;
    }

    public Boolean score(float[] data) {
        String commaSeparatedNumbers = convertToCommaSeparated(data);
        String url = getUrl() + commaSeparatedNumbers;
        Float result;
        try {

            RestTemplate template = new RestTemplate();
            ResponseEntity<String> response = template.postForEntity(url, null, String.class);
            String body = response.getBody();
            result = Float.parseFloat(body);
            LOG.debug("Score from scoring engine: {}", result);
        } catch (Exception ex) {
            LOG.warn("problem with getting scoring result! " + ex.getMessage(), ex);
            result = -100f;
        }

        return result.compareTo(1.0f) == 0;
    }

    private String getUrl() {
        return "http://" + scoringEngineUrl + "/v1/score?data=";
    }

    private String convertToCommaSeparated(float[] data) {

        return Floats.asList(data)
            .stream()
            .map(i -> String.format("%.4f", i))
            .collect(Collectors.joining(","));
    }
}
