'use strict';

/**
 * @ngdoc function
 * # MainCtrl
 */
angular.module('BootstrapApplication.controllers')
    .controller('MainCtrl', ['$scope', 'CityService', function ($scope, CityService) {
        $scope.alerts = [];
        $scope.app = {};

        $scope.applyForLoan = function () {
            CityService
                .applyForLoan($scope.app)
                .success(function (loanId) {
                    $scope.loanId = loanId;
                    displayMsg('Submitted, please refresh status');
                })
                .error(function(data, status) {
                    displayMsg('Error: ' + data + ". " + status);
                });
        };

        $scope.refreshStatus = function () {
            CityService
                .refreshApplicationStatus($scope.loanId)
                .success(function (result) {
                    displayMsg('Result is: ' + result);
                })
                .error(function(data, status) {
                    displayMsg('Error in status: ' + data + ". " + status + err);
                });
        };

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };

        function displayMsg(text) {
            $scope.alerts = [{msg: text}]
        }
    }]);
