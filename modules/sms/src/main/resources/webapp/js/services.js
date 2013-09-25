(function () {
    'use strict';

    /* Services */

    angular.module('sendSmsService', ['ngResource']).factory('SendSmsService', function($resource) {
        return $resource('../sms/send');
    });

    angular.module('templateService', ['ngResource']).factory('TemplateService', function($resource) {
        return $resource('../sms/templates');
    });

    angular.module('configService', ['ngResource']).factory('ConfigService', function($resource) {
        return $resource('../sms/configs');
    });

}());
