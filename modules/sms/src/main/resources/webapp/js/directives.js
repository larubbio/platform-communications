(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    smsModule.directive('focus', function () {
        return {
            link: function (scope, element, attrs) {
                attrs.$observe('focus', function (newValue) {
                    if (newValue === 'true') {
                        element[0].focus();
                    }
                    return true;
                });
            }
        };
    });

}());
