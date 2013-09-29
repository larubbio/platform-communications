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


    //todo: remove $log when not testing anymore
    smsModule.controller('SettingsController', function ($scope, $log, $http, ConfigService, TemplateService) {

        //TODO: figure out a way for directives.js to use a this array
        $scope.reservedProperties = ['name', 'template', 'openAccordion'];

        $http.get('../sms/configs')
            .success(function(res){
                var i;
                $scope.configs = res;
                $scope.originalConfigs = angular.copy($scope.configs);
                $scope.accordions = [];
                for (i=0 ; i<$scope.configs.configs.length ; i++) {
                    $scope.accordions.push(false);
                }
            });

        $scope.templates = TemplateService.get();

        /* TODO

            This replaces the configuration's properties with the ones from the selected template.
            Do we want to 'remember' the old template properties in case the user chooses to select the old template
            back from the dropdown?
        */
        $scope.changeTemplateProperties = function (config) {
            var key;
            for (key in config) {

                if ($scope.reservedProperties.indexOf(key) === -1) {
                    delete config[key];
                }
            }
            for (key in $scope.templates[config.template]) {
                if ($scope.reservedProperties.indexOf(key) === -1) {
                    config[key] = '';
                }
            }

        };

        $scope.reset = function () {
            $scope.configs = angular.copy($scope.originalConfigs);
        };

        $scope.isDirty = function () {
            return !angular.equals($scope.originalConfigs, $scope.configs);
        };

        $scope.submit = function () {

/*
            $http({method: 'POST', url: '../sms/configs', data: $scope.configs}).
                success(function(data, status) {
                    $scope.status = status;
                    $scope.data = data;
                }).
                error(function(data, status) {
                    $scope.data = data || "Request failed";
                    $scope.status = status;
                });
*/
            ConfigService.save(
                {},
                $scope.configs,
                function () {
                    var i;
                    for (i=0 ; i<$scope.accordions.length ; i++) {
                        $scope.accordions[i] = false;
                    }
                    $scope.originalConfigs = ConfigService.get();
                },
                function (response) {
                    handleWithStackTrace('sms.header.error', 'server.error', response);
                }
            );
        };
    });
}());
