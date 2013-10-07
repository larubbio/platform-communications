(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    /*
     *
     * Send
     *
     */
    smsModule.controller('SendController', function ($log, $scope, $timeout, $http, SendService, ConfigService) {
        $scope.sms = {};
        $scope.messages = [];
        $scope.error = "";

        //todo: kill next two debug-only helper lines
        $scope.sms.recipients = ['123'];
        $scope.sms.message = 'foobar!';

        $http.get('../sms/configs')
        .success(function(response) {
            $scope.config = response;
            $scope.sms.config = $scope.config.defaultConfigName;
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

    /*
     *
     * Log
     *
     */

    smsModule.controller('LogController', function ($scope, $http) {
        $scope.log = [];
    });

    /*
     *
     * Settings
     *
     */
    smsModule.controller('SettingsController', function ($scope, $http, ConfigService, TemplateService,
        ValidateConfigs) {

        $scope.errors = [];

        $http.get('../sms/templates')
            .success(function(response){
                $scope.templates = response;
            })
            .error(function() {
                 $scope.errors.push($scope.msg('sms.settings.validate.no_templates'));
             });

        function setAccordions(configs) {
                var i;
                $scope.accordions = [];
                for (i=0 ; i< configs.length ; i = i + 1) {
                    $scope.accordions.push(false);
                }
        }

        $http.get('../sms/configs')
            .success(function(response){
                $scope.config = response;
                $scope.originalConfig = angular.copy($scope.config);
                setAccordions($scope.config.configs);
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('sms.settings.validate.no_config', response));
            });

        $scope.collapseAccordions = function () {
            var key;
            for (key in $scope.accordions) {
                $scope.accordions[key] = false;
            }
        };

        /* TODO: FIX!

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

        $scope.setNewDefaultConfig = function() {
            var i;
            for (i in $scope.config.configs) {
                if ($scope.config.configs[i].name === $scope.config.defaultConfig) {
                    return;
                }
            }
            if ($scope.config.configs.length > 0) {
                $scope.config.defaultConfig = $scope.config.configs[0].name;
            }
        };

        $scope.deleteConfig = function(index) {
            $scope.config.configs.splice(index, 1);
            $scope.accordions.splice(index, 1);
            $scope.setNewDefaultConfig();
        };

        $scope.reset = function () {
            $scope.config = angular.copy($scope.originalConfig);
            $scope.collapseAccordions();
            $scope.setNewDefaultConfig();
        };

        $scope.addConfig = function () {
            $scope.config.configs.push({'name':$scope.msg('sms.settings.name.default'), 'template':'', 'max_retries':$scope.msg('sms.settings.max_retries.default'), 'default':'false'});
            $scope.accordions.push(true);
            if ($scope.config.configs.length === 1) {
                $scope.config.defaultConfig = $scope.config.configs[0].name;
            }
        };

        $scope.isDirty = function () {
            return !angular.equals($scope.originalConfig, $scope.config);
        };

        $scope.betterMsg = function (name) {
            var s = $scope.msg('sms.settings.prop.' + name);
            if (s === '[sms.settings.prop.' + name + ']') {
                s = name;
            }
            return s;
        };

        $scope.tooltipOrBlank = function (name) {
            var key = "sms.settings.prop." + name + ".tooltip",
                ret = $scope.msg(key);
            if (ret === "[" + key + "]") {
                ret = "";
            }
            return ret;
        };

        $scope.submit = function () {
            $http.post('../sms/configs', $scope.config)
                .success(function (response) {
                    $scope.config = response;
                    $scope.originalConfig = angular.copy($scope.config);
                    setAccordions($scope.config.configs);
                })
                .error (function (response) {
                    //todo: better than that!
                    handleWithStackTrace('sms.header.error', 'server.error', response);
                });
        };
    });
}());
