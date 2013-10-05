(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    smsModule.controller('SendController', function ($log, $scope, $timeout, $http, SendService, ConfigService) {
        $scope.sms = {};
        $scope.messages = [];
        $scope.error = "";


        $http.get('../sms/configs')
        .success(function(response) {
            $scope.config = response;
            $scope.sms['config'] = response.defaultConfig;
        })
        .error(function(response) {
            $scope.error = $scope.msg('sms.settings.validate.no_config', response);
        });

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.sendSms = function () {

            $scope.error = null;
            $http.post('../sms/send', $scope.sms)
                .success(function(response) {
                    var index = $scope.messages.push(response);
                    hideMsgLater(index-1);
                })
                .error(function(response) {
                    $scope.error = response;
                });
        };
    });


    smsModule.controller('LogController', function ($scope, $http) {
        $scope.log = [];
    });


    smsModule.controller('SettingsController', function ($scope, $http, ConfigService, TemplateService, ValidateConfigs) {

        $scope.errors = [];

        function setAccordions(configs) {
                var i;
                $scope.accordions = [];
                for (i=0 ; i< configs.length ; i = i + 1) {
                    $scope.accordions.push(false);
                }
        }

        $http.get('../sms/templates')
            .success(function(response){
                $scope.templates = response;
            })
            .error(function() {
                 $scope.errors.push($scope.msg('sms.settings.validate.no_templates'));
             });

        $http.get('../sms/configs')
            .success(function(response){
                $scope.config = response;
                setAccordions($scope.config.configs);
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('sms.settings.validate.no_config', response));
            });

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
            $scope.config = angular.copy($scope.originalConfig);
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
            $scope.configs.push({'name':$scope.msg('sms.settings.name.default'), 'template':'', 'max_retries':$scope.msg('sms.settings.max_retries.default'), 'default':'false'});
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

        $scope.submit = function () {
            $http.post('../sms/configs', $scope.config)
                .success(function (response) {
                    $scope.config = response;
                    setAccordions($scope.config.configs);
                })
                .error (function (response) {
                    //todo: better than that!
                    handleWithStackTrace('sms.header.error', 'server.error', response);
                });
        };
    });
}());
