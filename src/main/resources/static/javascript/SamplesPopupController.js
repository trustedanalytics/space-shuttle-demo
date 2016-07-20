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

(function () {
    var COLOR_NORMAL = '#0062a8',
        COLOR_ACTIVE = '#FF0000';

    App.controller('SamplesPopupController', function ($scope, DataProvider, appConfig) {
        $scope.dataLoaded = false;

        fetchSamples($scope.ngDialogData.timestamp);

        $scope.samples = {};
        $scope.dataLoaded = false;
        $scope.values = [];

        $scope.getCategory = function (id) {
            return appConfig.categories[id-1];
        };

        $scope.selectSample = function (sample) {
            $scope.selectedSample = sample;
            highlightHistograms($scope.selectedSample.features, $scope.values);
        };

        fetchHistograms();

        $scope.options = {
            chart: {
                type: 'discreteBarChart',
                margin : {
                    top: 20,
                    right: 20,
                    bottom: 50,
                    left: 55
                },
                x: function(d){return d.label;},
                y: function(d){return d.value + (1e-10);},
                showXAxis: false,
                forceY: [0, 100],
                valueFormat: function(d){
                    return d3.format(',.4f')(d);
                },
                duration: 500,
                yAxis: {
                    axisLabelDistance: -10,
                    tickFormat: function(d){
                        return d3.format(',.1f')(d) + '%';
                    }
                }
            }
        };

        function fetchSamples (serieTimestamp) {
                DataProvider.getSamples(serieTimestamp)
                    .then(function (_samples) {
                        var samples = _.mapObject(_samples.data, function (v, k) {
                            return {
                                timestamp: k,
                                class: v[1],
                                features: v.slice(2)
                            };
                        });
                        $scope.samples = _.groupBy(_.sortBy(_.sortBy(samples, 'timestamp'), 'class'), 'class');
                    });
        }

        function fetchHistograms () {
            $scope.histogramLoaded = false;
            DataProvider.getHistograms()
                .then(function (_histograms) {
                    var histograms = _.mapObject(_histograms.data, function (v) {
                        // fill missing values with 0
                        return _.sortBy(_.mapObject(_.extend(getEmptyHistogram(), v), function (v, k) {
                            return {
                                range: Number(k),
                                value: v
                            };
                        }), 'range');
                    });

                    $scope.values = _.map(histograms, function (histogram) {
                        var sum = _.reduce(_.pluck(histogram, 'value'), function (memo, num) {
                            return memo + num;
                        }, 0);
                        var array = _.map(histogram, function (v) {
                            var endRange = (v.range +0.2).toFixed(1);
                            return {
                                label: '[' + v.range +',' + endRange +')',
                                value: v.value / sum * 100,
                                color: COLOR_NORMAL
                            };
                        });
                        return [{values: array}]
                    });
                    $scope.histogramLoaded = true;
                });
        }

        function highlightHistograms (selectedFeatures, values) {
            var buckets = _.map(selectedFeatures, function (feature) {
                return Math.floor((feature + 1) * 5);
            });

            var iterator = 0;
            _.map(values, function (v) {
                _.map(v[0].values, function (d) {
                    d.color = COLOR_NORMAL;
                });
                var activeBucket = v[0].values[buckets[iterator]];
                if (activeBucket) {
                    activeBucket.color = COLOR_ACTIVE;
                }
                iterator ++;
            });
        }

        function getEmptyHistogram() {
            var bucketsCount = 10;
            return _.object(Array.apply(null, {length: bucketsCount})
                .map(Number.call, Number)
                .map(function(v){
                    return [((v*2 - bucketsCount) / bucketsCount).toFixed(1), 0];
                }));
        }
    });
}());