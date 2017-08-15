angular.module('adminApp')
    .directive('paging', function() {

        template = '/app/html/paging.html';

        return {
            restrict: 'E',
            templateUrl: template
        };
    });