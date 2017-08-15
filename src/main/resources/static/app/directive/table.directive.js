angular.module('adminApp')
    .directive('tableSection', function() {

        template = '/app/html/table.html';

        return {
            restrict: 'E',
            templateUrl: template
        };
    });