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
var App = angular.module('app', ['ngDialog']);

(function(){

    var GROUPBY_1MIN = "1m",
        GROUPBY_15MIN = "15m",
        GROUPBY_1H = "1h",
        DEFAULT_GROUPBY = GROUPBY_1MIN,
        SINCE_7D = "7d",
        SINCE_1D = "1d",
        SINCE_1H = "1h",
        SINCE_DEFAULT = SINCE_1D,
        COUNT_TRESHOLD = 600,
        REST_BASE = '/rest/space-shuttle/';


    App.value('appConfig', {
            restBase: REST_BASE,
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
            ]
        })

        .controller('MainController', ['$scope', 'ChartFactory', '$http', '$timeout',
            '$location', 'ngDialog', '$q', '$filter', 'appConfig',
            function($scope, ChartFactory, $http, $timeout, $location, ngDialog, $q, $filter, appConfig){

        var dataProvider = [];
        var timer;
        var hoveredItem = null;

        $scope.since = $location.search().since || SINCE_DEFAULT;

        $scope.sinceValues = {
            hour: SINCE_1H,
            day: SINCE_1D,
            week: SINCE_7D
        };

        var categories = appConfig.categories.map(function(v, k) {
            return {
                valueField: $filter('number')(k+1, 1),
                title: v
            };
        });

        var chart = ChartFactory.create("chartdiv", dataProvider, categories);
        $scope.chart = chart;

        /*chart.addListener("clickGraphItem", function onGraphClick(item) {
            ngDialog.open({
                template: 'samplesPopup',
                controller: 'SamplesPopupController',
                data: {
                    item: item
                }
            });
        });*/

        $scope.onClick = function(){
            if(hoveredItem) {
                ngDialog.open({
                    template: 'samplesPopup',
                    controller: 'SamplesPopupController',
                    data: {
                        item: hoveredItem
                    }
                });
            }
        };

        chart.chartCursor.addListener("changed", function onGraphClick(cursor) {
            hoveredItem = _.isUndefined(cursor.index) ? null :  chart.dataProvider[cursor.index];
        });

        $scope.$watch('since', function(){

            switch($scope.since) {
                case SINCE_7D:
                    appConfig.groupby = GROUPBY_1H;
                    break;
                case SINCE_1D:
                    appConfig.groupby = GROUPBY_15MIN;
                    break;
                default:
                    appConfig.groupby = DEFAULT_GROUPBY;
            }
            chart.dataProvider = [];

            refreshData();
        });


        function refreshData() {
            $timeout.cancel(timer);
            $http.get(REST_BASE + 'chart', {
                    params: {
                        since: $scope.since,
                        groupby: appConfig.groupby
                    }
                })
                .then(function onSuccess(data){

                    var series = _.sortBy(_.pairs(data.data).map(function(d){
                        d[1].timestamp = d[0];
                        return d[1];
                    }), 'timestamp');

                    var newPoints = series.filter(function(d) {
                        return !_.findWhere(chart.dataProvider, { timestamp: d.timestamp });
                    });

                    var lastPoint = _.last(chart.dataProvider);
                    if(lastPoint) {
                        var newLastPoint = _.findWhere(series, {timestamp: lastPoint.timestamp});
                        if(newLastPoint && !_.isEqual(lastPoint, newLastPoint)) {
                            chart.dataProvider.pop();
                            newPoints.unshift(newLastPoint);
                        }
                    }

                    _.each(newPoints, function(d) {
                        chart.dataProvider.push(d);
                    });

                    if(newPoints.length) {
                        chart.validateData();
                    }

                })
                .finally(scheduleRefresh);
        }

        function scheduleRefresh() {
            timer = $timeout(function () {
                refreshData();
            }, 5000);
        }


    }]);


})();
