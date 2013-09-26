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


    smsModule.directive('expandAccordion', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $('.accordion').on('show', function (e) {
                    $(e.target).siblings('.accordion-heading').find('.accordion-toggle i.icon-chevron-right').removeClass('icon-chevron-right').addClass('icon-chevron-down');
                });

                $('.accordion').on('hide', function (e) {
                    $(e.target).siblings('.accordion-heading').find('.accordion-toggle i.icon-chevron-down').removeClass('icon-chevron-down').addClass('icon-chevron-right');
                });
            }
        };
    });


}());
