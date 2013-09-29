(function () {
    'use strict';

    /* App Module */

    angular.module('motech-sms', ['motech-dashboard', 'ngCookies', 'ui.bootstrap', 'testService', 'templateService',
    'logService', 'settingService', 'ngRoute']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/test', {templateUrl: '../sms/resources/partials/test.html', controller: 'TestController'}).
                when('/log', {templateUrl: '../sms/resources/partials/log.html', controller: 'LogController'}).
                when('/settings', {templateUrl: '../sms/resources/partials/settings.html', controller: 'SettingsController'}).
                otherwise({redirectTo: '/test'});
    }]);
}());
