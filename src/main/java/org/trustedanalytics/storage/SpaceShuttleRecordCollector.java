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

import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SpaceShuttleRecordCollector
    implements Collector<SpaceShuttleRecord, Map<Double, Double>, Map<Double, Double>> {

    public static SpaceShuttleRecordCollector collect() {
        return new SpaceShuttleRecordCollector();
    }

    @Override public Supplier<Map<Double, Double>> supplier() {
        return () -> {
            Map<Double, Double> result = new HashMap<>();
            for(int i = 1; i < 8; i++) {
                result.put((double)i, 0.0);
            }
            return result;
        };
    }

    @Override public BiConsumer<Map<Double, Double>, SpaceShuttleRecord> accumulator() {
        return (alreadyGrouped, row) -> {
            Double key = row.getVectorClass();
            Double count = Optional.ofNullable(alreadyGrouped.get(key)).orElse(0.0);
            alreadyGrouped.put(key, count + row.getCount());
        };
    }

    @Override public BinaryOperator<Map<Double, Double>> combiner() {
        return (grouped1, grouped2) -> {
            for (Map.Entry<Double, Double> row : grouped2.entrySet()) {
                Double key = row.getKey();
                Double count =
                    Optional.ofNullable(grouped1.get(key)).orElse(0.0) + row.getValue();
                grouped1.put(key, count);
            }
            return grouped1;
        };
    }

    @Override public Function<Map<Double, Double>, Map<Double, Double>> finisher() {
        return grouped -> grouped;
    }

    @Override public Set<Characteristics> characteristics() {
        return ImmutableSet.of(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
    }
}
