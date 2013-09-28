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

    smsModule.filter('excludeSpecials', function() {
        return function(input) {
            var key;
            var ret = {};
            for (key in input) {
            //TODO: figure out a way to use $scope.reservedProperties from controller.js instead of hard-coding below
                if (['name', 'template', 'openAccordion'].indexOf(key) == -1) {
                    ret[key] = input[key];
                }
            }
            return ret;
        }
    });

}());
