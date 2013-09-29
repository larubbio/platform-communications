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

    //todo see why i couldn't use a simple filter function ie:  | filter:function
    smsModule.filter('excludeSpecials', function() {
        return function(input) {
            var key, ret = {};
            for (key in input) {
            //TODO: figure out a way to use $scope.reservedProperties from controller.js instead of hard-coding below
                if (['name', 'template', 'default', 'openAccordion'].indexOf(key) === -1) {
                    ret[key] = input[key];
                }
            }
            return ret;
        };
    });

}());
