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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trustedanalytics.process.ProcessConsumer;
import org.trustedanalytics.scoringengine.ATKScoringEngine;

@RestController
public class DemoController {

    @Value("${message:Test message}")
    private String message;

    @Autowired
    ATKScoringEngine scoring;

    @Autowired ProcessConsumer process;

    @RequestMapping("/test")
    String test() {
        return message;
    }

    @RequestMapping("/test-score")
    String scoreTest() {
        float[] data =
                {-0.414141f, -0.0246564f, -0.125f, 0.0140301f, -0.474359f, 0.0256049f, -0.0980392f,
                        0.463884f, 0.40836f};
        Boolean res = scoring.score(data);
        return res.toString();
    }

    @RequestMapping("/test-add")
    void addTest() {
        float[] data = {0.0f, -0.414141f, -0.0246564f, -0.125f, 0.0140301f, -0.474359f, 0.0256049f, -0.0980392f,
            0.463884f, 0.40836f};
        process.processMessage(data);
    }
}
