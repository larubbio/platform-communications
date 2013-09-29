(function () {
    'use strict';

    /* Services */

    angular.module('sendSmsService', ['ngResource']).factory('SendSmsService', function($resource) {
        return $resource('../sms/send');
    });

    angular.module('templateService', ['ngResource']).factory('TemplateService', function($resource) {
        return $resource('../sms/templates');
    });

    angular.module('settingService', ['ngResource']).factory('SettingService', function($resource) {
        return $resource('../sms/settings');
    });

}());
