(function () {
    'use strict';

    /* Controllers */
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

    smsModule.controller('SettingsController', function ($scope, ConfigService, TemplateService) {
        $scope.configs = ConfigService.get();
        $scope.templates = TemplateService.get();

        $scope.setDefault = function (n) {
            $scope.settings.defaultConfig = n;
        };

        $scope.submit = function () {
/*
            SettingsService.save(
                {},
                $scope.settings,
                function () {
                    motechAlert('sms.header.success', 'sms.settings.saved');
                    $scope.settings = SettingsService.get();
                },
                function (response) {
                    handleWithStackTrace('sms.header.error', 'server.error', response);
                }
            );
*/
        };

    });
}());
