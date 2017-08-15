angular.module('adminApp')
    .directive('searchBar', function() {

        template = '/app/html/search.html';

        return {
            restrict: 'E',
            templateUrl: template
        };
    });