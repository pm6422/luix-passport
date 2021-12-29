/**
 * INSPINIA - Responsive Admin Theme
 *
 */
(function () {
    'use strict';
    
    angular.module('smartcloudserviceApp', [
        'ui.router',                    // Routing
        'oc.lazyLoad',                  // ocLazyLoad
        'ui.bootstrap',                 // Ui Bootstrap
        'pascalprecht.translate',       // Angular Translate
        'ngIdle',                       // Idle timer
        'ngSanitize',                   // ngSanitize
        'ngStorage',
        'ngResource',
        'ngCookies',
        'ngAria',                       // ngAria
        'ngCacheBuster',
        'oitozero.ngSweetAlert',
        'toaster',                       // AngularJS Toaster
        'angles',                        // AngularJS Chart JS
        'jsonFormatter',                 // AngularJS JSON formatter
        'checklist-model',
        'ngFileUpload'
    ])
    .run(run);
    
    run.$inject = ['StateHandler'];

    function run(StateHandler) {
        StateHandler.initialize();
    }
})();

// Other libraries are loaded dynamically in the config.js file using the library ocLazyLoad