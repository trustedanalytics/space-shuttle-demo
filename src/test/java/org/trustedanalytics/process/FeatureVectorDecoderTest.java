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
/*
// Copyright (c) 2015 Intel Corporation 
//
// Licensed under the Apache License, Version 2.0 (the \"License\");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an \"AS IS\" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
 */

package org.trustedanalytics.process;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FeatureVectorDecoderTest {

    @Test
    public void fromBytes_validMessage() {
        String msg =
                "{\"id\":\"91a281f4-dade-4499-a33a-14c3ca32daa8\","
                        + "\"on\":\"2015-06-04T23:04:43.932343553Z\","
                        + "\"body\":\"[4.0, -0.43, -0.025, -0.03, 0.01, -0.49, 0.02, -0.01, 0.49, 0.42]\"}";
        float[] expected = new float[]{4.0f, -0.43f, -0.025f, -0.03f, 0.01f, -0.49f, 0.02f, -0.01f, 0.49f, 0.42f};
        

        FeatureVectorDecoder decoder = new FeatureVectorDecoder();
        float[] vector = decoder.fromBytes(msg.getBytes());
        assertThat(vector, is(expected));
    }
}
