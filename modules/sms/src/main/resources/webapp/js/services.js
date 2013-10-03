(function () {
    'use strict';

    /* Services */
    //TODO see if we're using all these, nuke otherwise

    angular.module('testService', ['ngResource']).factory('TestService', function($resource) {
        return $resource('../sms/send');
    });

    angular.module('logService', ['ngResource']).factory('LogService', function($resource) {
        return $resource('../sms/log');
    });

    angular.module('templateService', ['ngResource']).factory('TemplateService', function($resource) {
        return $resource('../sms/templates');
    });

    angular.module('configService', ['ngResource']).factory('ConfigService', function($resource) {
        return $resource('../sms/configs');
    });

    angular.module("motech-sms").factory("ValidateConfigs", function(){

        var appendError = function(err, txt) {
            if (err.length > 0) {
                return err + ", and " + txt;
            }
            return txt;
        };

        // todo - validate
        // no or invalid retry count
        // no or invalid template
        // no duplicate config names

        return function(scope, configs, templates) {
            var i, errors = [], validConfigs = [], error, config, valid;
            for (i = 0 ; i < configs.length ; i = i + 1) {
                valid = true;
                error = "";
                config = configs[i];

                //
                // name
                //
                if (!config.hasOwnProperty('name') || config.name.length < 1) {
                    error = appendError(error, scope.msg('sms.settings.validate.no_name', JSON.stringify(config)));
                    valid = false;
                }

                //
                // retry count
                //
                if (!config.hasOwnProperty('retry') || config.retry.length < 1) {
                    error = appendError(error, scope.msg('sms.settings.validate.no_retry', config.name));
                } else if (isNaN(config.retry)) {
                    error = appendError(error, scope.msg('sms.settings.validate.invalid_retry', config.name));
                }

                // fragment starred below removes duplicates [needed by ng-repeat]
                //
                //                      *****************************
                if (error.length > 0 && errors.indexOf(error) > -1) {
                    errors.push(error);
                }
                if (valid) {
                    validConfigs.push(config);
                }
            }

            return {"errors" : errors, "configs": validConfigs};
        };
    });

}());
