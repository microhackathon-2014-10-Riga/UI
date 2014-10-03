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
                    displayMsg('Error: ' + data + '. ' + status);
                });
        };

        $scope.refreshStatus = function () {
            var clientId = $scope.app.name + '_' + $scope.app.surname;
            CityService
                .refreshApplicationStatus($scope.loanId, clientId)
                .success(function (resultObj) {
                    var decision = (resultObj.decisionAboutTheLoan ? 'accepted' : 'rejected');
                    var offers = resultObj.offers? resultObj.offers : 'none';
                    displayMsg('Result is: ' + decision + ', offer: ' + offers);
                })
                .error(function(data, status) {
                    displayMsg('Error in status: ' + data + '. ' + status);
                });
        };

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };

        function displayMsg(text) {
            $scope.alerts = [
                {msg: text}
            ];
        }
    }]);
