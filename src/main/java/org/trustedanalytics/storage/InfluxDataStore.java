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

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import org.apache.commons.lang3.ArrayUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trustedanalytics.process.FeaturesRow;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfluxDataStore implements DataStore {

    private static final Logger LOG = LoggerFactory.getLogger(InfluxDataStore.class);

    private static final String CLASSIFICATION_SERIE = "classification";
    private static final String CLASS_COLUMN = "class";
    private static final String FEATURES_SERIE = "features";
    private static final List<String> FEATURES_COLUMNS = Lists.newArrayList("anomaly", "classification", "1",
        "2", "3", "4", "5", "6", "7", "8", "9");
    private static final double BUCKET_SIZE = 0.2;

    private StoreProperties properties;

    private InfluxDB store;

    public InfluxDataStore(StoreProperties properties) {

        this.store = InfluxDBFactory
            .connect(properties.getFullUrl(), properties.getUsername(), properties.getPassword());
        this.properties = properties;

        initializeDatabase();
    }

    @Override public void saveClass(Double score) {
        save(CLASSIFICATION_SERIE, new String[] {CLASS_COLUMN}, new Double[] {score});
    }

    @Override public void saveFeatures(Double[] features) {
        save(FEATURES_SERIE, FEATURES_COLUMNS.stream().toArray(String[]::new), features);
    }

    @Override public Map<Date, Map<Double, Double>> readClass(String since, String groupBy) {
        return read(CLASSIFICATION_SERIE, CLASS_COLUMN,
            Optional.ofNullable(groupBy).orElse(properties.getDefaultGroupingInterval()),
            Optional.ofNullable(since).orElse(properties.getDefaultTimeLimit()));
    }

    @Override
    public Optional<Map<Date, Double[]>> readFeatures(long intervalStart, String intervalLength) {
        String query = String.format("SELECT * from %s where anomaly > 0 AND time > %dms AND time < %dms + %s",
            FEATURES_SERIE, intervalStart, intervalStart, intervalLength);

        LOG.debug(query);

        List<Serie> result = store.query(properties.getDatabaseName(), query, TimeUnit.MILLISECONDS);

        if(result.isEmpty()) {
            return Optional.empty();
        }

        LOG.info(
            String.format("Found %d records meeting criteria. ", result.get(0).getRows().size()));

        return Optional.of(result.get(0).getRows().stream().map(row -> {
            List<Double> array = new ArrayList<>();
            FEATURES_COLUMNS.forEach(s -> array.add((Double) row.get(s)));
            return new FeaturesRow((Double) row.get("time"),
                ArrayUtils.toObject(Doubles.toArray(array)));
        }).collect(Collectors.toMap(FeaturesRow::getTime, p -> p.getFeatures())));
    }

    @Override public Map<String, Map<Double, Integer>> getHistogram() {
        String query = "select HISTOGRAM(\"%s\", " + BUCKET_SIZE + ") from " + FEATURES_SERIE;

        List<Map<Double, Integer>> queryResult = getHistogramColumns().stream().map(
            s -> String.format(query, s))
            .map(q -> store.query(properties.getDatabaseName(), q, TimeUnit.MILLISECONDS))
            .map(qr -> extractHistogramMaps(qr.get(0).getRows()))
            .collect(Collectors.toList());

        return Stream.iterate(0, i -> i + 1).limit(getHistogramColumns().size())
            .collect(Collectors.toMap(getHistogramColumns()::get, queryResult::get));
    }

    private Map<Double, Integer> extractHistogramMaps(List<Map<String, Object>> data){
        return data.stream().map(v -> new SpaceShuttleHistogramRecord(v)).collect(Collectors
            .toMap(SpaceShuttleHistogramRecord::getBucketStartRounded, SpaceShuttleHistogramRecord::getCountRounded));
    }

    private List<String> getHistogramColumns(){
        return FEATURES_COLUMNS.subList(1, FEATURES_COLUMNS.size());
    }

    private void save(String serieName, String[] columns, Double[] values) {
        LOG.debug("Save value(s) in serie '{}'", serieName);

        Serie serie = new Serie.Builder(serieName)
            .columns(columns)
            .values(values)
            .build();
        write(serie);
    }

    private void write(Serie serie) {
        store.write(properties.getDatabaseName(), TimeUnit.MILLISECONDS, serie);
    }

    private Map<Date, Map<Double, Double>> read(String serieName, String key,
        String groupingInterval, String timeLimit) {

        String query =
            String.format("select count(%s) from %s group by time(%s), %s where time > now() - %s",
                key, serieName, groupingInterval, key, timeLimit);
        LOG.debug(query);

        List<Serie> queryResult = store.query(properties.getDatabaseName(), query, TimeUnit.MILLISECONDS);
        LOG.debug("{} series read", queryResult.size());
        if (queryResult.isEmpty()) {
            return null;
        }

        LOG.debug("{} rows read in first serie", queryResult.get(0).getRows().size());
        return queryResult.get(0).getRows().stream().map(
            row -> new SpaceShuttleRecord((Double) row.get("time"), (Double) row.get("class"),
                (Double) row.get("count"))).collect(Collectors
                .groupingBy(SpaceShuttleRecord::getTimestamp,
                    SpaceShuttleRecordCollector.collect()));
    }

    private void createDatabase() {
        LOG.debug("Creating database.");
        store.createDatabase(properties.getDatabaseName());
    }

    private boolean databaseExists() {
        LOG.debug("Check if database exists.");
        return store.describeDatabases()
            .stream()
            .filter(d -> d.getName().equals(properties.getDatabaseName()))
            .count() > 0;
    }

    private void initializeDatabase() {
        if (!databaseExists()) {
            createDatabase();
        }
    }
}
