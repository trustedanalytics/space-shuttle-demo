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
    'use strict';

    App.factory("DataProvider", function ($http, appConfig) {
        var service = {};

        service.getData = function (since, groupby) {
            return $http.get(appConfig.restBase + 'chart', {
                params: {
                    since: since || appConfig.since,
                    groupBy: groupby || appConfig.groupBy
                }
            })
            .then(function (data) { 
                return data;
            })
        };

        service.getSamples = function (serieTimestamp) {
            var timestamp = new Date(serieTimestamp).getTime();
            return $http.get(appConfig.restBase + 'samples', {
                params: {
                    intervalStart: timestamp,
                    intervalLength: appConfig.groupby
                }
            })
            .then(function (samples) {
                return samples
            });
        };

        service.getHistograms = function () {
            return $http.get(appConfig.restBase + 'histogram')
                .then(function (histograms) {
                    return histograms;
                });
        };

        return service;
    });
}());