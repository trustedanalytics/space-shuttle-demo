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

import com.google.common.base.Preconditions;
import java.util.Map;
import lombok.Getter;

public class SpaceShuttleHistogramRecord {

    @Getter
    private Double bucketStartRounded;

    @Getter
    private Integer countRounded;

    public SpaceShuttleHistogramRecord(Map<String, Object> record){
        Double bucketStartOriginal = (Double)record.get("bucket_start");
        Preconditions.checkNotNull(bucketStartOriginal, "Invalid histogram record: bucket_start is null.");
        bucketStartRounded = Math.round(bucketStartOriginal * 10 ) / 10.0;

        Double countOriginal = (Double)record.get("count");
        Preconditions.checkNotNull(countOriginal, "Invalid histogram record: count is null.");
        countRounded = countOriginal.intValue();
    }
}
