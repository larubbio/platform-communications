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
        $scope.dt = "now";
        $scope.messages = [];
        $scope.error = "";

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        $scope.sms.recipients = ["12066120198"];
        $scope.sms.message = "Msg" + new Date().getHours() + ":" + new Date().getMinutes() + ":" + new Date().getSeconds();

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
            var now = new Date(), then;
            $scope.error = null;

            if ($scope.dt === "now") {
                $scope.sms.deliveryTime = null;
            } else if ($scope.dt === "10sec") {
                then = new Date(now.getTime() + 10*1000);
                $scope.sms.deliveryTime = then;
            } else if ($scope.dt === "1min") {
                then = new Date(now.getTime() + 60*1000);
                $scope.sms.deliveryTime = then;
            } else if ($scope.dt === "1hour") {
                then = new Date(now.getTime() + 3600*1000);
                $scope.sms.deliveryTime = then;
            }
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
    smsModule.controller('SettingsController', function ($scope, $http, ConfigService, TemplateService) {

        $scope.errors = [];

        $http.get('../sms/templates')
            .success(function(response){
                $scope.templates = response;
            })
            .error(function(response) {
                 $scope.errors.push($scope.msg('sms.settings.validate.no_templates', response));
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
            var i, requires = $scope.templates[config.templateName].requires;
            config.props = [];
            for (i=0 ; i<requires.length ; i=i+1) {
                config.props.push({"name": requires[i], "value": ""});
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
            $scope.setNewDefaultConfig();
            setAccordions($scope.config.configs);
        };

        $scope.addConfig = function () {
            var firstTemplateName = Object.keys($scope.templates)[0], newLength, newConfig;
            newConfig = {
                'name':'',
                'templateName':firstTemplateName,
                'maxRetries':parseInt($scope.msg('sms.settings.max_retries.default'), 10),
                'splitHeader':$scope.msg('sms.settings.split_header.default'),
                'splitFooter':$scope.msg('sms.settings.split_footer.default'),
                'splitExcludeLastFooter':$scope.msg('sms.settings.split_exclude.default')
                };
            newLength = $scope.config.configs.push(newConfig);
            $scope.accordions.push(true);
            if ($scope.config.configs.length === 1) {
                $scope.config.defaultConfig = $scope.config.configs[0].name;
            }
            $scope.changeTemplateProperties($scope.config.configs[newLength-1]);
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