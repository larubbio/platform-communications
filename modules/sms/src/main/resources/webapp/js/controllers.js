(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    smsModule.controller('SendSmsController', function ($scope, $http, SendSmsService) {
        $scope.sms = {};

        $http.get('../sms/configs')
            .success(function(res){
                var key;
                $scope.configs = res;
                for (key in $scope.configs.configs) {
                    if ($scope.configs.configs[key]['default'] === 'true') {
                        $scope.sms['config'] = $scope.configs.configs[key]['name'];
                         break;
                    }
                }
            });

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

        //TODO: figure out a way for directives.js to use a this array
        $scope.reservedProperties = ['name', 'template', 'default', 'openAccordion'];

        $http.get('../sms/configs')
            .success(function(res){
                var key;
                $scope.configs = res;
                $scope.originalConfigs = angular.copy($scope.configs);
                $scope.accordions = [];
                for (key in $scope.configs.configs) {
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

        $scope.collapseAccordions = function () {
            var i;
            for (i=0 ; i<$scope.accordions.length ; i = i+1) {
                $scope.accordions[i] = false;
            }
        };

        $scope.reset = function () {
            $scope.configs = angular.copy($scope.originalConfigs);
            $scope.collapseAccordions();
        };

        $scope.setDefault = function (name) {
            var key;
            for (key in $scope.configs.configs) {
                if ($scope.configs.configs[key].name === name) {
                    $scope.configs.configs[key]['default'] = 'true';
                }
                else {
                    $scope.configs.configs[key]['default'] = 'false';
                }
            }
        };

        $scope.addConfig = function () {
            var d = "false";
            if (!$scope.configs.configs) {
                $scope.configs.configs = [];
                d = "true";
            }
            $scope.configs.configs.push({'name':'Untitled', 'template':'', 'default':d});
            $scope.accordions.push(true);
        };

        $scope.isDirty = function () {
            return !angular.equals($scope.originalConfigs, $scope.configs);
        };

        $scope.submit = function () {

            ConfigService.save(
                {},
                $scope.configs,
                function () {
                    $scope.collapseAccordions();
                    $scope.originalConfigs = ConfigService.get();
                },
                function (response) {
                    handleWithStackTrace('sms.header.error', 'server.error', response);
                }
            );
        };
    });
}());
