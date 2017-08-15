/**
 * Created by sk2rldnr on 2017-07-10.
 */
(function () {
    'use strict';

    var app = angular.module('adminApp', ["xeditable"]);
    app.run(function(editableOptions) {
        editableOptions.theme = 'bs3';
    });

})();