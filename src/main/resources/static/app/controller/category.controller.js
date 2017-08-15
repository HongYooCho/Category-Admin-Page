/**
 * Created by sk2rldnr on 2017-07-10.
 */
angular.module('adminApp')
    .controller('CategoryCtrl', CategoryCtrl);

CategoryCtrl.$inject = ['$http', '$scope'];

function CategoryCtrl($http, $scope) {
    var SynchronizeState = Object.freeze({CATEGORY_RENEWAL: 0, UPLOAD: 1, UPDATE: 2});

    $scope.category = [];
    $scope.csvPrefix = "카테고리 srl, 1depth 명, 2depth 명, 3depth 명, 4depth 명, 동의어, 유의어, 가중치, 사용 여부, 작업자, 작업 일시\n";

    $scope.availabilityFilter = "all";
    $scope.firstDepthFilter = "전체";
    $scope.secondDepthFilter = "전체";
    $scope.firstDepthCategory = ["전체"];
    $scope.secondDepthCategoryList = {"전체": ["전체"]};

    $scope.items = [true, false];

    $scope.availabilityStatuses = [
        {value: true, text: 'true'},
        {value: false, text: 'false'}
    ];

    $scope.selected1DepthCategory = "";
    $scope.selected2DepthCategory = "";

    $scope.categoryIsRenewaled = false;

    $scope.insertedCatList = [];
    $scope.fileName = "";

    var isRenewal = SynchronizeState.UPDATE;
    var changedCatNo = [];



    function isInitialState(categoryName) {
        return categoryName === "";
    }

    function isDifferentCategory(currentCategory, previousCategory) {
        return currentCategory !== previousCategory;
    }

    // Initialize first and second category filter for search
    $scope.initializeCategoryFilter = function () {
        var preFirstCatName = "", preSecondCatName = "";
        var secondCatList = [];
        $scope.firstDepthCategory = ["전체"];
        $scope.secondDepthCategoryList = {"전체": ["전체"]};

        angular.forEach($scope.category, function (cat) {
            if (isDifferentCategory(cat.firstCatName, preFirstCatName)) {
                if (!isInitialState(preFirstCatName)) {
                    $scope.secondDepthCategoryList[preFirstCatName] = secondCatList;
                }
                preFirstCatName = cat.firstCatName;
                $scope.firstDepthCategory.push(preFirstCatName);
                secondCatList = ["전체"];
                preSecondCatName = "";

            } else {
                if (isDifferentCategory(cat.secondCatName, preSecondCatName)) {
                    secondCatList.push(cat.secondCatName);
                    preSecondCatName = cat.secondCatName;
                }
            }
        });

        if (!isInitialState(preFirstCatName)) {
            $scope.secondDepthCategoryList[preFirstCatName] = secondCatList;
            $scope.selected1DepthCategory = $scope.firstDepthCategory[0];
            $scope.selected2DepthCategory = $scope.firstDepthCategory[0];
        }
    };

    // DB data to angular from server
    $scope.init = function () {
        $scope.initNotFinished = true;
        isRenewal = SynchronizeState.UPDATE;
        $scope.insertedCatList = [];
        var url = "/category/";
        var categoryData = $http.get(url);

        categoryData.then(function success(response) {
            $scope.category = response.data.categoryList;
            $scope.initializeCategoryFilter();

            console.log("초기화 성공");
            $scope.initNotFinished = false;
        }, function error(response) {

            alert("초기화에 실패했습니다");
            console.log(response.data.err);
            $scope.initNotFinished = false;
        });
    };

    $scope.init();

    // To represent category names like ' 1 > 2 > 3 > 4 '
    $scope.isEmptyCategory = function (categoryTitle) {
        return categoryTitle === "" ? categoryTitle : ' > ' + categoryTitle;
    };

    $scope.updateWorkDate = function (category) {
        if (changedCatNo.indexOf(category.catNo) === -1) {
            changedCatNo.push(category.catNo);
        }

        category.workDate = new Date();
    };

    $scope.typeToInt = function typeToInt(category) {
        category.weight = parseInt(category.weight);
    };

    // If field changed, update work date at row
    $scope.isChanged = function isChanged(beforeChange, afterChange, category) {
        var isUpdated = beforeChange !== afterChange;

        if (isUpdated) {
            $scope.updateWorkDate(category);
        }
    };

    $scope.printLog = function printLog(message) {
        alert(message);
        console.log(message);
    };

    // Validation check for weight
    $scope.checkWeight = function checkWeight(beforeChange, weight, category) {
        var isInteger = Number(weight) === parseInt(weight);
        var outOfRange = weight > 100 || weight < 0;
        var invalidWeight = !isInteger || isNaN(weight) || outOfRange;
        var isUpdated = beforeChange !== weight;

        if (invalidWeight) {
            alert("0~100 사이의 값을 정수로 입력해 주세요");
            return false;
        }
        if (isUpdated) {
            $scope.updateWorkDate(category);
        }
    };

    // Renewal category
    $scope.renewalCategory = function renewalCategory() {
        $scope.renewalNotFinished = true;
        $scope.categoryIsRenewaled = true;
        isRenewal = SynchronizeState.CATEGORY_RENEWAL;
        var url = "/category/renewal";

        $http.get(url).then(
            function success(response) {
                $scope.insertedCatList = response.data.changedList;
                $scope.category = response.data.categoryList;
                $scope.initializeCategoryFilter();

                $scope.printLog("카테고리 반영 성공");
                $scope.renewalNotFinished = false;

            }, function error(response) {
                $scope.printLog("카테고리 반영에 실패했습니다" + response.data.err);
                $scope.renewalNotFinished = false;
            }
        )
    };

    // 수정 및 추가된 카테고리 번호 강조
    $scope.highlightChangedCategory = function highlightChangedCategory(catNo) {
        if($scope.insertedCatList.indexOf(catNo) !== -1) {
            return { color: "red" };
        }
    };

    function initializeSynchronizeStatus() {
        $scope.updateNotFinished = false;
        $scope.categoryIsRenewaled = false;
        isRenewal = SynchronizeState.UPDATE;
    }

    // Synchronize UI with DB
    $scope.synchronizeWithDB = function synchronizeWithDB() {
        $scope.updateNotFinished = true;
        var url = "/category/synchronize";
        console.log(changedCatNo.length);
        $http.post(url, $scope.category, {params : {"renewal" : isRenewal, "changedCatNo" : changedCatNo.length === 0 ? "" : changedCatNo}}).then(
            function success() {
                $scope.printLog("DB에 반영되었습니다");
                initializeSynchronizeStatus();
                changedCatNo = [];

            }, function error(response) {
                $scope.printLog("DB 반영 실패" + response.data.err);
                initializeSynchronizeStatus();
            });
    };

    // CSV file download
    $scope.csvDownload = function csvDownload() {
        $scope.downloadNotFinished = true;
        var url = "/category/download";

        $http.get(url).then(
            function success(response) {
                var csvContent = $scope.csvPrefix + response.data.csvContent;
                var link = window.document.createElement("a");

                link.setAttribute("href", "data:text/csv;charset=euc-kr,%EF%BB%BF" + encodeURI(csvContent));
                link.setAttribute("download", "categoryData.csv");
                link.click();

                $scope.printLog("다운로드 성공");
                $scope.downloadNotFinished = false;

            }, function error(response) {
                $scope.printLog("다운로드 실패 : " + response.data.err);
                $scope.downloadNotFinished = false;
            });
    };

    // CSV file upload
    $scope.csvUpload = function csvUpload() {
        var fileSelected = document.getElementById('uploadFile').files[0] !== undefined;
        if (!fileSelected)
            return false;

        var url = "/category/upload";
        var form = new FormData(document.getElementById('uploadForm'));
        isRenewal = SynchronizeState.UPLOAD;

        $scope.uploadNotFinished = true;

        $http.post(url, form, {headers: {'Content-Type': undefined}}).then(
            function success(response) {
                $scope.category = response.data.categoryList;
                $scope.printLog("업로드 되었습니다");
                $scope.uploadNotFinished = false;

            }, function error(response) {
                $scope.printLog("업로드 실패 : " + response.data.err);
                $scope.uploadNotFinished = false;
            }
        )
    };

    $scope.fileSelected = function fileSelected() {
        $scope.$apply(function() {
            var fileSelected = document.getElementById('uploadFile').files[0] !== undefined;
            if (fileSelected) {
                $scope.fileName = document.getElementById('uploadFile').files[0].name;
            }
        });
    };

    // For button disabled
    $scope.doingInit = function doingInit() {
        return $scope.initNotFinished;
    };

    $scope.doingUpdate = function doingUpdate() {
        return $scope.updateNotFinished;
    };

    $scope.doingDownload = function doingDownload() {
        return $scope.downloadNotFinished;
    };

    $scope.doingUpload = function doingUpload() {
        return $scope.uploadNotFinished;
    };

    $scope.doingRenewal = function doingRenewal() {
        return $scope.renewalNotFinished;
    }
}
