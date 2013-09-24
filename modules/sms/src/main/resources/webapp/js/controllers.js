(function () {
    'use strict';

    /* Controllers */
    var smsModule = angular.module('motech-sms');

    smsModule.controller('SendSmsController', function ($scope, SendSmsService, SettingsService) {
        $scope.sms = {};

        $scope.settings = SettingsService.get();

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

    smsModule.controller('SettingsController', function ($scope, SettingsService) {
        $scope.settings = SettingsService.get();

        $scope.setDefault = function (n) {
            $scope.settings.defaultConfig = n;
        };

        $scope.submit = function () {

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
        };

    });
}());
