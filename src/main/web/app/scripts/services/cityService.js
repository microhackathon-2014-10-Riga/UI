'use strict';

angular.module('BootstrapApplication.services')
    .factory('CityService', ['$http', function ($http) {
        return {
            applyForLoan: function (app) {
                return $http({
                    url: '/application',
                    dataType: 'json',
                    method: 'POST',
                    data: JSON.stringify(app),
                    headers: {
                        'Content-Type': 'application/vnd.com.ofg.twitter-places-analyzer.v1+json'
                    }
                });
            },
            refreshApplicationStatus: function(loanId, clientId) {
                return $http({
                    url: '/application/' + loanId + '/' + clientId,
                    dataType: 'json',
                    method: 'GET',
                    data: '',
                    headers: {
                        'Content-Type': 'application/vnd.com.ofg.twitter-places-analyzer.v1+json'
                    }
                });
            }
        };
    }
    ]);
