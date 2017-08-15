/**
 * Created by sk2rldnr on 2017-07-12.
 */
angular.module('adminApp').controller('PageCtrl', PageCtrl);

function PageCtrl($scope) {

    $scope.currentPage = 0;
    $scope.pageSize = 15;
    $scope.currentPageSize = 0;

    $scope.numberOfPages = function (filteredDataSize) {
        $scope.currentPageSize = Math.ceil(filteredDataSize / $scope.pageSize);
        return Math.ceil(filteredDataSize / $scope.pageSize);
    };

    $scope.isLastPage = function(currentPage, currentPageSize) {
        return currentPage >= currentPageSize - 1;
    };

    $scope.isFirstPage = function(currentPage) {
        return currentPage === 0;
    };
}

angular.module('adminApp').filter('startFrom', function () {
    return function (input, start) {
        start = +start; // == parseInt
        return input.slice(start);
    }
});