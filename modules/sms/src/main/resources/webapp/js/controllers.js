(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    smsModule.controller('SendSmsController', function ($scope, SendSmsService, ConfigService) {
        $scope.sms = {};

        $scope.configs = ConfigService.get();

        $scope.sendSms = function () {

            SendSmsService.save(
                {},
                $scope.sms,
                function () {
                    motechAlert('sms.send.alert.success', 'sms.send.alert.title');
                },
                function (response) {
                    handleWithStackTrace('sms.send.alert.title', 'sms.send.alert.failure', response);
                }
            );
        };
    });

    smsModule.controller('SettingsController', function ($scope, $http, ConfigService, TemplateService) {

        $http.get('../sms/configs')
            .success(function(res){
                var key;
                $scope.configs = res;
                for (key in $scope.configs.configs) {
                    $scope.configs.configs[key].openAccordion = false;
                }
                $scope.originalConfigs = angular.copy($scope.configs);
            });

        $scope.templates = TemplateService.get();

        $scope.setDefault = function (n) {
            $scope.configs.defaultConfig = n;
        };

        $scope.reset = function () {
            $scope.configs = angular.copy($scope.originalConfigs);
        };

        $scope.isDirty = function () {
            return !angular.equals($scope.originalConfigs, $scope.configs);
        };

    });
}());
