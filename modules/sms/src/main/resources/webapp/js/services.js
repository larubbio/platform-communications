(function () {
    'use strict';

    /* Services */
    /* TODO see if we're using all these, nuke otherwise */

    angular.module('sendService', ['ngResource']).factory('SendService', function($resource) {
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

        return function(scope, configsDto, templates, defaults) {
/*
            var i, j, errors = [], validConfigs = [], config, valid, defaultConfig = null, key, match, size;
            for (i = 0 ; i < configs.length ; i = i + 1) {
                valid = true;
                config = configs[i];

                //
                // name
                //
                if (!config.hasOwnProperty('name') || config.name.length < 1) {
                    addError(errors, scope.msg('sms.settings.validate.no_name', JSON.stringify(config)));
                    valid = false;
                } else {
                    for (j=0 ; j<validConfigs.length ; j=j+1) {
                        if (config.name === validConfigs[j].name) {
                            addError(errors, scope.msg('sms.settings.validate.duplicate_name', config.name, JSON.stringify(config)));
                            valid = false;
                        }
                    }
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
                // multi_recipient
                //
                if (!config.hasOwnProperty('multi_recipient') || (config.multi_recipient !== 'true' && config.multi_recipient !== 'false')) {
                    addError(errors, scope.msg('sms.settings.validate.invalid_multi_recipient', config.name, defaults.multi_recipient));
                    config.multi_recipient = defaults.multi_recipient;
                }

                //
                // max_retries
                //
                if (!config.hasOwnProperty('max_retries') || config.max_retries.length < 1) {
                    addError(errors, scope.msg('sms.settings.validate.no_max_retries', config.name, defaults.max_retries));
                } else if (isNaN(config.max_retries)) {
                    addError(errors, scope.msg('sms.settings.validate.invalid_max_retries', config.name, defaults.max_retries));
                }

                //
                // max_sms_size
                //
                if (!config.hasOwnProperty('max_sms_size') || config.max_sms_size.length < 1) {
                    addError(errors, scope.msg('sms.settings.validate.no_max_sms_size', config.name, defaults.max_sms_size));
                } else if (isNaN(config.max_sms_size)) {
                    addError(errors, scope.msg('sms.settings.validate.invalid_max_sms_size', config.name, defaults.max_sms_size));
                }
                //
                // header+footer size < max_sms_size
                //
                else {
                    size = 0;
                    if (config.hasOwnProperty('split_header') && config.split_header.length > 0) {
                        size = size + config.split_header.length + 1; //+1 for the extra \n after header
                    }
                    if (config.hasOwnProperty('split_footer') && config.split_footer.length > 0) {
                        size = size + config.split_header.length + 1; //+1 for the extra \n before footer
                    }
                    if (parseInt(config.max_sms_size, 10) <= size) {
                        addError(errors, scope.msg('sms.settings.validate.templates_max_sms_size_mismatch', config.name));
                        valid = false;
                    }
                }

                //
                // split_header
                //
                if (!config.hasOwnProperty('split_header')) {
                    addError(errors, scope.msg('sms.settings.validate.no_split_header', config.name, defaults.split_header));
                }

                //
                // split_footer
                //
                if (!config.hasOwnProperty('split_footer')) {
                    addError(errors, scope.msg('sms.settings.validate.no_split_footer', config.name, defaults.split_footer));
                }

                //
                // split_exclude_last_footer
                //
                if (!config.hasOwnProperty('split_exclude_last_footer') || (config.split_exclude_last_footer !== 'true' && config.split_exclude_last_footer !== 'false')) {
                    addError(errors, scope.msg('sms.settings.validate.invalid_split_exclude_last_footer', config.name, defaults.split_exclude_last_footer));
                    config.split_exclude_last_footer = defaults.split_exclude_last_footer;
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
*/
        };
    });

}());
