'use strict';

angular.module('BootstrapApplication.services')
    .factory('CityService', ['$http', function ($http) {
        return {
            applyForLoan: function (app, successFn) {
                successFn(4242);
                $http({
                    url: '/loan/application/' + app,
                    dataType: 'json',
                    method: 'POST',
                    data: '',
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
