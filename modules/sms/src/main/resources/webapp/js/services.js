(function () {
    'use strict';

    /* Services */

    angular.module('testService', ['ngResource']).factory('TestService', function($resource) {
        return $resource('../sms/send');
    });

    angular.module('logService', ['ngResource']).factory('LogService', function($resource) {
        return $resource('../sms/log');
    });

    angular.module('templateService', ['ngResource']).factory('TemplateService', function($resource) {
        return $resource('../sms/templates');
    });

    angular.module('settingService', ['ngResource']).factory('SettingService', function($resource) {
        return $resource('../sms/settings');
    });

}());
