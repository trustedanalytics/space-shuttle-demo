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
    App.factory('ChartFactory', [function(){
        return {
            create: function(id, data, graphs) {
                graphs = graphs.map(function(g, i) {
                    return angular.extend(g, {
                        "bullet": "circle",
                        "bulletBorderThickness": 0,
                        "bulletSize": 1,
                        "fillAlphas": 0.7,
                        "id": "AmGraph-" + i,
                        "lineAlpha": 0,
                        "type": "line",
                        //"balloonText": "[[title]]: [[value]]",,
                        "balloonFunction": function(arg) {
                            return arg.values.value ? arg.graph.title + ": " + arg.values.value : "";
                        },
                        "pattern": {"url":"/js/lib/amcharts/patterns/intel/pattern-" + i + ".png", "width":4, "height":4}
                    });
                });

                AmCharts.isReady = true;
                return AmCharts.makeChart(id, {
                    "type": "serial",
                    "path": "http://www.amcharts.com/lib/3/",
                    "categoryField": "timestamp",
                    //"dataDateFormat": "YYYY-MM-DD HH:NN",
                    "legend": [{
                        "useGrapSettings": true,
                        "equalWidths": false,
                        "valueText": ''
                    }],
                    "colors": [
                        "#495fba",
                        "#e8d685",
                        "#ae85c9",
                        "#c9f0e1",
                        "#d48652",
                        "#629b6d",
                        "#719dc3",
                        "#ffffff",
                    ],
                    "theme": "black",
                    "categoryAxis": {
                        "minPeriod": "mm",
                        "parseDates": true
                    },
                    "valueAxes": [{
                        "stackType": "regular",
                        "integersOnly": true
                    }],
                    "chartCursor": {
                        "categoryBalloonDateFormat": "JJ:NN",
                        zoomable: false
                    },
                    "trendLines": [],
                    "graphs": graphs,
                    "guides": [],
                    "allLabels": [],
                    "balloon": {},
                    "export": {
                        "enabled": true
                    },
                    "dataProvider": data,
                });
            }
        };
    }]);
})();
