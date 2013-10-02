(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    smsModule.controller('TestController', function ($log, $scope, $timeout, $http, TestService) {
        $scope.sms = {};
        $scope.messages = [];
        $scope.errors = [];

        $http.get('../sms/configs')
            .success(function(res) {
                var key;
                $scope.configs = res;
                for (key in $scope.configs) {
                    if ($scope.configs[key]['default'] === 'true') {
                        $scope.sms.config = $scope.configs[key].name;
                         break;
                    }
                }
            });

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 3000);
        }

        $scope.sendSms = function () {

            $http.post('../sms/send', $scope.sms)
                .success(function(response) {
                    var index = $scope.messages.push(response);
                    hideMsgLater(index-1);
                })
                .failure(function(response) {
                    $scope.errors.push(response);
                });
        };
    });


    smsModule.controller('LogController', function ($scope, $http) {
        $scope.log = [];
    });


    smsModule.controller('SettingsController', function ($scope, $http, ConfigService, TemplateService) {

        function getConfigs(response) {
                var i;
                $scope.configs = response;
                $scope.configsSettings = angular.copy($scope.configs);
                $scope.accordions = [];
                for (i=0 ; i< $scope.configs.length ; i = i + 1) {
                    $scope.accordions.push(false);
                }
                $scope.errors = $scope.validateConfigs();
        }

        $http.get('../sms/configs')
            .success(function(response){
                getConfigs(response);
            });

        $scope.templates = TemplateService.get();

        /* TODO

            This replaces the configuration's properties with the ones from the selected template.
            Do we want to 'remember' the old template properties in case the user chooses to select the old template
            back from the dropdown?
        */
        $scope.changeTemplateProperties = function (config) {
            var key;
            //remove the previous template's properties
            for (key in config) {
                if (key.substring(0,5) === "user.") {
                    delete config[key];
                }
            }
            //insert the new template's properties
            for (key in $scope.templates[config.template]) {
                if (key.substring(0,5) === "user.") {
                    config[key] = $scope.templates[config.template][key];
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
            $scope.configs = angular.copy($scope.originalConfigs);
            $scope.collapseAccordions();
        };

        $scope.setDefault = function (index) {
            var i;
            for (i = 0 ; i < $scope.configs.length ; i = i + 1) {
                if (i === index) {
                    $scope.configs[i]['default'] = 'true';
                }
                else {
                    $scope.configs[i]['default'] = 'false';
                }
            }
        };

        $scope.setNewDefault = function () {
            var i;
            for (i = 0 ; i < $scope.configs.length ; i = i + 1) {
                if ($scope.configs[i]['default'] === 'true') {
                    return;
                }
            }
            $scope.configs[0]['default'] = 'true';
        };

        $scope.addConfig = function () {
            $scope.configs.push({'name':$scope.msg('sms.settings.name.default'), 'template':'', 'retry':$scope.msg('sms.settings.retry.default'), 'default':'false'});
            $scope.accordions.push(true);
            $scope.setNewDefault();
        };

        $scope.isDirty = function () {
            return !angular.equals($scope.originalConfigs, $scope.configs);
        };

        $scope.betterMsg = function (key) {
            var s = $scope.msg('sms.settings.' + key);
            if (s === '[sms.settings.' + key + ']') {
                s = key.substr(5);
            }
            return s;
        };

        $scope.tooltipOrBlank = function (key) {
            var fullKey = "sms.settings." + key + ".tooltip",
                ret = $scope.msg(fullKey);
            if (ret === "[" + fullKey + "]") {
                ret = "";
            }
            return ret;
        };

        // todo - validate
        // no or invalid retry count
        // no or invalid template
        // no duplicate config names

        $scope.validateConfigs = function() {
            var i, errors = [];
            for (i = 0 ; i < $scope.configs.length ; i = i + 1) {
                if (!$scope.configs[i].hasOwnProperty('name') || $scope.configs[i].name.length < 1) {
                    errors.push($scope.msg('sms.settings.validate.no_name', $scope.configs[i]));
                }
            }
            return errors;
        };

        $scope.submit = function () {

            ConfigService.save(
                {},
                $scope.configs,
                function (response) {
                    getConfigs(response);
                },
                function (response) {
                    handleWithStackTrace('sms.header.error', 'server.error', response);
                }
            );
        };
    });
}());
