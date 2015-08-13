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
package org.trustedanalytics.service;

import org.trustedanalytics.storage.DataStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(value = "/rest/space-shuttle/")
public class RestSpaceShuttleController {

    @Autowired
    private DataStore store;

    @RequestMapping(value = "chart")
    public Map<Date, Map<Double, Double>> chart(@RequestParam(required = false) String since,
        @RequestParam(required = false, value = "groupby") String groupBy) {

        return store.readClass(since, groupBy);
    }

    @RequestMapping(value = "samples")
    public Map<Date, Double[]> getSamples(@RequestParam long intervalStart, @RequestParam String intervalLength) {
        Map<Date, Double[]> toReturn = store.readFeatures(intervalStart, intervalLength)
            .orElseThrow(() -> new NoDataInGivenInterval());

        return toReturn;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class NoDataInGivenInterval extends RuntimeException {
    }
}
