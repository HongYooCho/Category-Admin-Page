angular.module('adminApp')
    .directive('uploadBar', function() {

        template = '/app/html/upload.html';

        return {
            restrict: 'E',
            templateUrl: template
        };
    });