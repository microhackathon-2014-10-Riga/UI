'use strict';

angular.module('BootstrapApplication.services')
    .factory('CityService', ['$http', function ($http) {
        return {
            applyForLoan: function (app, successFn) {
                successFn(4242);
                $http({
                    url: '/application',
                    dataType: 'json',
                    method: 'POST',
                    data: JSON.stringify(app),
                    headers: {
                        'Content-Type': 'application/vnd.com.ofg.twitter-places-analyzer.v1+json'
                    }
                }).success(function (data) {
                    //successFn(data);
                });
            }
        };
    }
    ]);
