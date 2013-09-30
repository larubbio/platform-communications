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
                if (key.substring(0, 5) === "user.") {
                    ret[key] = input[key];
                }
            }
            return ret;
        };
    });

}());
