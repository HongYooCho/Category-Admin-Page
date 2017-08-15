/**
 * Created by sk2rldnr on 2017-07-18.
 */
angular.module('adminApp').controller('SearchCtrl', SearchCtrl);

function SearchCtrl($scope) {

    $scope.propertyName = "catNo";
    $scope.reverse = false;
    $scope.secondDepthCategory = {};

    // Sorted by one of {'catNo', 'weight', 'availability', 'worker', 'workDate'}
    $scope.sortBy = function(propertyName) {
        $scope.reverse = ($scope.propertyName === propertyName) ? !$scope.reverse : false;
        $scope.propertyName = propertyName;
    };

    // If firstDepthCategory Changed, initialize secondDepthCategory & page
    $scope.onFirstDepthChanged = function() {
        $scope.secondDepthCategory = $scope.secondDepthCategoryList[$scope.selected1DepthCategory];
        $scope.selected2DepthCategory = $scope.secondDepthCategoryList[$scope.selected1DepthCategory][0];
        $scope.onSearched();
    };

    // if searched, change current page to first page
    $scope.onSearched = function() {
        $scope.currentPage = 0;
    };


    // Filter : which row contains searched words
    $scope.synonym = function(row) {
        return !!(row.sameWords.indexOf($scope.synonymFilter || '') !== -1 || row.similarWords.indexOf($scope.synonymFilter || '') !== -1);
    };

    // Filter : which row contains selected availability
    $scope.availability = function(row) {
        var selectedAvailability = $scope.availabilityFilter;

        if (selectedAvailability === "all") {
            return true;
        } else if (selectedAvailability === "true") {
            return (row.availability === true);
        } else if (selectedAvailability === "false")
            return (row.availability === false);
    };

    // Filter : which row contains selected firstDepthCategory
    $scope.firstDepth = function(row) {
        return $scope.selected1DepthCategory === "전체" ? true : row.firstCatName === $scope.selected1DepthCategory;
    };

    // Filter : which row contains selected secondDepthCategory
    $scope.secondDepth = function(row) {
        return $scope.selected2DepthCategory === "전체" ? true : row.secondCatName === $scope.selected2DepthCategory;
    };

}
