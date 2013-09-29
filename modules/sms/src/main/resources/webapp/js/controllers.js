(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    smsModule.controller('SendSmsController', function ($scope, $http, SendSmsService) {
        $scope.sms = {};

        $http.get('../sms/settings')
            .success(function(res){
                var key;
                $scope.settings = res;
                for (key in $scope.settings.configs) {
                    if ($scope.settings.configs[key]['default'] === 'true') {
                        $scope.sms.config = $scope.settings.configs[key].name;
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


    smsModule.controller('SettingsController', function ($scope, $http, SettingService, TemplateService) {

        //TODO: figure out a way for directives.js to use a this array
        $scope.reservedProperties = ['name', 'template', 'default', 'openAccordion'];

        $http.get('../sms/settings')
            .success(function(res){
                var i;
                $scope.settings = res;
                $scope.originalSettings = angular.copy($scope.settings);
                $scope.accordions = [];
                for (i=0 ; i< $scope.settings.configs.length ; i = i + 1) {
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
            var key;
            for (key in $scope.accordions) {
                $scope.accordions[key] = false;
            }
        };

        $scope.reset = function () {
            $scope.settings = angular.copy($scope.originalSettings);
            $scope.collapseAccordions();
        };

        $scope.setDefault = function (name) {
            var key;
            for (key in $scope.settings.configs) {
                if ($scope.settings.configs[key].name === name) {
                    $scope.settings.configs[key]['default'] = 'true';
                }
                else {
                    $scope.settings.configs[key]['default'] = 'false';
                }
            }
        };

        $scope.addConfig = function () {
            var d = "false";
            if (!$scope.settings.configs) {
                $scope.settings.configs = [];
                d = "true";
            }
            $scope.settings.configs.push({'name':'Untitled', 'template':'', 'default':d});
            $scope.accordions.push(true);
        };

        $scope.isDirty = function () {
            return !angular.equals($scope.originalSettings, $scope.settings);
        };

        $scope.submit = function () {

            SettingService.save(
                {},
                $scope.settings,
                function () {
                    $scope.collapseAccordions();
                    $scope.originalSettings = SettingService.get();
                },
                function (response) {
                    handleWithStackTrace('sms.header.error', 'server.error', response);
                }
            );
        };
    });
}());
