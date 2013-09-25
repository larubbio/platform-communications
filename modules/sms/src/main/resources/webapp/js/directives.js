(function () {
    'use strict';

    var smsModule = angular.module('motech-sms');

    smsModule.directive('focus', function () {
      return function (scope, element, attrs) {
        attrs.$observe('focus', function (newValue) {
          return (newValue === 'true') && element[0].focus();
        });
      };
    });

}());
