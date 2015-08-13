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
(function(){
    var COLOR_NORMAL = '#0062a8',
        COLOR_ACTIVE = '#FF0000';

    App.controller('SamplesPopupController', ['$scope', 'ngDialog', '$q', '$http', 'appConfig', '$filter',
        function($scope, ngDialog, $q, $http, appConfig, $filter) {

        var charts = {};

        $scope.dataLoaded = false;

        $scope.selectSample = function (sample) {
            $scope.selectedSample = sample;
            highlightHistograms();
        };

        $scope.getCategory = function(id) {
            return appConfig.categories[id - 1];
        };

        $q.all([
            fetchSamples($scope.ngDialogData.item),
            fetchHistograms()
        ])
        .then(function(values) {
            $scope.samples = values[0];
            var histograms = values[1];

            charts = _.mapObject(histograms, function(v, k) {
                return drawHistogram(k, v);
            });

            $scope.dataLoaded = true;
            $scope.dataLoaded = true;

        });

        function highlightHistograms() {
            $scope.selectedSample.features.map(function(v, k){
                var featureChart = charts[k+1];
                var bucket = Math.floor((v + 1) * 5);
                _.each(featureChart.dataProvider, function(b){
                    b.color = COLOR_NORMAL;
                });
                featureChart.dataProvider[bucket].color = COLOR_ACTIVE;
                featureChart.validateData();
            });
        }


        function drawHistogram(id, data) {

            var sum = _.reduce(_.pluck(data, 'value'), function(memo, num) {
                return memo + num;
            }, 0);

            var chart = AmCharts.makeChart("histogram-" + id, {
                "type": "serial",
                "theme": "black",
                "dataProvider": data,
                "startDuration": 1,
                "marginTop": 0,
                "marginRight": 0,
                "marginLeft": 0,
                "marginBottom": 0,
                "autoMargins": false,
                "graphs": [ {
                    title: "Feature #" + id,
                    "fillAlphas": 0.8,
                    "lineAlpha": 0.2,
                    "type": "column",
                    "valueField": "value",
                    colorField: 'color',
                    balloonFunction: function(arg) {
                        var category = parseFloat(arg.category);
                        var percent = $filter('number')(arg.values.value * 100 / sum, 1) + '%';
                        var bucketEnd = $filter('number')(category + appConfig.bucketSize, 1);
                        return '<b>[' + category + ', ' + bucketEnd + ']</b>: ' + percent;
                    },
                } ],
                "categoryField": "range",
                categoryAxis: {
                    labelsEnabled: false
                },
                valueAxes: [{
                    axisAlpha: 0
                }],
                colors: [
                    COLOR_NORMAL
                ]
            });

            return chart;
        }

        function fetchSamples(item) {
            var timestamp = new Date(item.timestamp).getTime();
            var deferred = $q.defer();

            $http.get(appConfig.restBase + 'samples', {
                    params: {
                        intervalStart: timestamp,
                        intervalLength: appConfig.groupby
                    }
                })
                .success(function onSuccess(data) {
                    var samples = _.mapObject(data, function(v, k) {
                        return {
                            time: k,
                            class: v[1],
                            features: v.slice(2)
                        };
                    });
                    samples = _.groupBy(_.sortBy(_.sortBy(samples, 'time'), 'class'), 'class');

                    deferred.resolve(samples);
                })
                .error(function onError() {
                    deferred.reject();
                });
            return deferred.promise;
        }

        function fetchHistograms() {
            var deferred = $q.defer();

            $http.get(appConfig.restBase + 'histogram')
                .success(function onSuccess(data) {
                    var histograms = _.mapObject(data, function(v) {
                        // fill missing values with 0
                        return _.sortBy(_.mapObject(_.extend(getEmptyHistogram(), v), function(v, k){
                            return {
                                range: Number(k),
                                value: v
                            };
                        }), 'range');
                    });

                    deferred.resolve(histograms);
                })
                .error(function onError() {
                    deferred.reject();
                });
            return deferred.promise;
        }

        function getEmptyHistogram() {
            var bucketsCount = 10;
            return _.object(Array.apply(null, {length: bucketsCount})
                    .map(Number.call, Number)
                    .map(function(v){
                        return [((v*2 - bucketsCount) / bucketsCount).toFixed(1), 0];
                    }));
        }

    }]);
})();
