(function () {
    'use strict';

    /* Services */
    /* TODO see if we're using all these, nuke otherwise */

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

    angular.module('motech-sms').factory('ValidateConfigs', function(){

        var addError = function(arr, err) {
            // fragment starred below removes duplicates [needed by ng-repeat]
            //                    ***********************
            if (err.length > 0 && arr.indexOf(err) === -1) {
                arr.push(err);
            }
        };

        // todo - validate
        // no or invalid max_retries count
        // no or invalid template
        // no duplicate config names

        return function(scope, configs, templates, defaults) {
            var i, j, errors = [], validConfigs = [], config, valid, defaultConfig = null, key, match;
            for (i = 0 ; i < configs.length ; i = i + 1) {
                valid = true;
                config = configs[i];

                //
                // name
                //
                if (!config.hasOwnProperty('name') || config.name.length < 1) {
                    addError(errors, scope.msg('sms.settings.validate.no_name', JSON.stringify(config)));
                    valid = false;
                }

                //
                // default
                //
                if (!config.hasOwnProperty('default') || (config['default'] !== 'true' && config['default'] !== 'false')) {
                    addError(errors, scope.msg('sms.settings.validate.invalid_default', JSON.stringify(config)));
                }
                else if (config['default'] === 'true') {
                    if (defaultConfig !== null) {
                        addError(errors, scope.msg('sms.settings.validate.duplicate_default', config.name, configs[defaultConfig].name));
                        config['default'] = 'false';
                    }
                }

                //
                // max_retries count
                //
                if (!config.hasOwnProperty('max_retries') || config.max_retries.length < 1) {
                    addError(errors, scope.msg('sms.settings.validate.no_max_retries', config.name, defaults.max_retries));
                } else if (isNaN(config.max_retries)) {
                    addError(errors, scope.msg('sms.settings.validate.invalid_max_retries', config.name, defaults.max_retries));
                }

                //
                // max_message_size count
                //
                if (!config.hasOwnProperty('max_message_size') || config.max_message_size.length < 1) {
                    addError(errors, scope.msg('sms.settings.validate.no_max_message_size', config.name, defaults.max_message_size));
                } else if (isNaN(config.max_message_size)) {
                    addError(errors, scope.msg('sms.settings.validate.invalid_max_message_size', config.name, defaults.max_message_size));
                }

                //
                // template
                //
                if (!config.hasOwnProperty('template') || config.template < 1) {
                    addError(errors, scope.msg('sms.settings.validate.no_template', config.name));
                    valid = false;
                } else {
                    match = false;
                    for (key in templates) {
                        if (key === config.template) {
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        addError(errors, scope.msg('sms.settings.validate.no_matching_template', config.template, config.name));
                        valid = false;
                    }
                }

                if (valid) {
                    if (config['default'] === 'true') {
                        defaultConfig = i;
                    }
                    validConfigs.push(config);
                }
            }

            if (defaultConfig === null && validConfigs.length > 0) {
                validConfigs[0]['default'] = 'true';
            }

            return {'errors' : errors, 'configs': validConfigs};
        };
    });

}());
