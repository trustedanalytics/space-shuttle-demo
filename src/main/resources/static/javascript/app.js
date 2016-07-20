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

var App = angular.module('app', ['nvd3', 'ngDialog']);

(function () {
    var GROUPBY_1MIN = "1m",
        GROUPBY_15MIN = "15m",
        GROUPBY_1H = "1h",
        DEFAULT_GROUPBY = GROUPBY_1MIN,
        SINCE_7D = "7d",
        SINCE_1D = "1d",
        SINCE_1H = "1h",
        SINCE_DEFAULT = SINCE_1H,
        REST_BASE = "/rest/space-shuttle/";

    App.value('appConfig', {
        groupby: DEFAULT_GROUPBY,
        bucketSize: 0.2,
        categories: [
            'Rad Flow',
            'Fpv Close',
            'Fpv Open',
            'High',
            'Bypass',
            'Bpv Close',
            'Bpv Open'
        ],
        restBase: REST_BASE
    });

    App.controller('MainController', function ($scope, $location, $timeout, DataProvider, appConfig, ngDialog) {

        var timer,
            ticksNumber = 6,
            timeFormat = "%H:%M",
            series;

        $scope.dataLoaded = false;

        $scope.since = $location.search().since || SINCE_DEFAULT;

        $scope.sinceValues = {
            hour: SINCE_1H,
            day: SINCE_1D,
            week: SINCE_7D
        };

        setChartOptions(timeFormat, ticksNumber);

        $scope.openDialog = function(timestamp){
                ngDialog.open({
                    template: 'samplesPopup',
                    controller: 'SamplesPopupController',
                    data: {
                        timestamp: timestamp
                    }
                });
        };

        $scope.readyCallback = function () {
            $scope.dataLoaded = true;
        };

        $scope.$watch('since', function(){
            refreshData();
            switch($scope.since) {
                case SINCE_7D:
                    appConfig.groupby = GROUPBY_1H;
                    timeFormat = "%d-%m";
                    ticksNumber = 7;
                    break;
                case SINCE_1D:
                    appConfig.groupby = GROUPBY_15MIN;
                    timeFormat = "%H:%M";
                    ticksNumber = 12;
                    break;
                case SINCE_1H:
                    appConfig.groupby = GROUPBY_1MIN;
                    timeFormat = "%H:%M";
                    ticksNumber = 6;
                default:
                    appConfig.groupby = DEFAULT_GROUPBY;
            }
            if($scope.dataLoaded) {
                $scope.chartApi.updateWithOptions(setChartOptions(timeFormat, ticksNumber));
                $scope.chartApi.refresh();
            }

        });

        function refreshData() {
            $timeout.cancel(timer);
            DataProvider.getData($scope.since, appConfig.groupby)
                .then(function (data) {

                    $scope.timestamps =[];

                    $scope.series = [{
                        key: appConfig.categories[0],
                        color: '#495fba',
                        values: []
                    }, {
                        key: appConfig.categories[1],
                        color: '#e8d685',
                        values: []
                    }, {
                        key: appConfig.categories[2],
                        color: '#ae85c9',
                        values: []
                    }, {
                        key: appConfig.categories[3],
                        color: '#c9f0e1',
                        values: []
                    }, {
                        key: appConfig.categories[4],
                        color: '#d48652',
                        values: []
                    }, {
                        key: appConfig.categories[5],
                        color: '#629b6d',
                        values: []
                    }, {
                        key: appConfig.categories[6],
                        color: '#719dc3',
                        values: []
                    }];
                    series = _.sortBy(_.pairs(data.data).map(function (d) {
                        d[1].timestamp = d[0];
                        return d[1];
                    }), 'timestamp');

                    _.each(series, function(serie) {
                        var date = Date.parse(serie.timestamp);
                        $scope.timestamps.push(date);
                        $scope.series[0].values.push({x: date, y: serie['1.0']});
                        $scope.series[1].values.push({x: date, y: serie['2.0']});
                        $scope.series[2].values.push({x: date, y: serie['3.0']});
                        $scope.series[3].values.push({x: date, y: serie['4.0']});
                        $scope.series[4].values.push({x: date, y: serie['5.0']});
                        $scope.series[5].values.push({x: date, y: serie['6.0']});
                        $scope.series[6].values.push({x: date, y: serie['7.0']});
                    });
                })
                .finally(scheduleRefresh);
        }

        function setChartOptions (timeFormat, ticksNumber) {
            return $scope.options = {
                chart: {
                    type: 'stackedAreaChart',
                    minHeight: 300,
                    margin : {
                        top: 130,
                        right: 80,
                        bottom: 45,
                        left: 80
                    },
                    x: function (d) {
                        return d.x;
                    },
                    useInteractiveGuideline: true,
                    clipEdge: true,
                    duration: 500,
                    xAxis: {
                        ticks: ticksNumber,
                        axisLabel: 'Time',
                        showMaxMin: false,
                        tickFormat: function(d){
                            return d3.time.format(timeFormat)(new Date(d));
                        }
                    },
                    yAxis: {
                        axisLabelDistance: -20,
                        tickFormat: function(d){
                            return d3.format(',.1f')(d);
                        }
                    },
                    stacked: {
                        dispatch: {
                            areaClick: function (e){
                                e.stopPropagation();
                                return;
                            }
                        }
                    },
                    interactiveLayer: {
                        dispatch: {
                            elementClick: function (e) {
                                var serieTimestamp = Math.floor(e.pointXValue);
                                $scope.openDialog(getClosest($scope.timestamps, serieTimestamp));
                            }
                        }
                    }
                }
            };
        }

        function scheduleRefresh() {
            timer = $timeout(function () {
                refreshData();
            }, 15000);
        }

        function getClosest(array, target) {
            return _.reduce(array, function (memo, num) {
                return (Math.abs(num - target) < Math.abs(memo - target) ? num : memo);
            });
        }
    });
}());

