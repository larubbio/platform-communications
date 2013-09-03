(function () {
    'use strict';

    /* Services */

    angular.module('sendSmsService', ['ngResource']).factory('SendSmsService', function($resource) {
        return $resource('../sms/send');
    });

    angular.module('settingsService', ['ngResource']).factory('SettingsService', function($resource) {
        return $resource('../sms/settings');
    });

}());
