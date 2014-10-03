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
            CityService.applyForLoan($scope.app, function (loanId) {
                $scope.loanId = loanId;
                $scope.alerts = [{msg: 'Submitted, please refresh status'}]
            });
        };

        $scope.refreshStatus = function () {
            CityService
                .refreshApplicationStatus($scope.loanId)
                .success(function (result) {
                    $scope.alerts = [{msg: 'Result is: ' + result}]
                });
        };

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };
    }]);
