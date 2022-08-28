/**
 * Using state to manage routing and views
 * Each view are defined as state.
 * Initial there are written state for all view in theme.
 *
 */
angular
    .module('smartcloudserviceApp')
    .config(stateConfig)
    .config(paginationConfig)
    .config(pagerConfig)
    .config(httpConfig)
    .config(localStorageConfig)
    .config(compileServiceConfig)
    .run(function ($rootScope, $state) {
        $rootScope.$state = $state;
        $rootScope.now = new Date(); // Set system time
        $rootScope.parseDate = function (dateString) {
            return new Date(dateString);
        }
    });

function stateConfig($stateProvider, $urlRouterProvider, $ocLazyLoadProvider, IdleProvider, KeepaliveProvider, APP_NAME) {

    // Configure Idle settings
    IdleProvider.idle(5); // in seconds
    IdleProvider.timeout(120); // in seconds

    $urlRouterProvider.otherwise('/');

    $ocLazyLoadProvider.config({
        // Set to true if you want to see what and when is dynamically loaded
        debug: false
    });

    $stateProvider
        .state('layout', {
            abstract: true,
            templateUrl: 'app/views/common/layout.html'
        })
        .state('dashboard', {
            parent: 'layout',
            url: '/',
            views: {
                'content@': {
                    templateUrl: 'app/views/common/dashboard.html'
                }
            },
            data: {
                pageTitle: 'Dashboard',
                authorities: ['ROLE_ADMIN', 'ROLE_DEVELOPER', 'ROLE_USER']
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            name: 'angular-flot',
                            files: ['content/js/plugins/flot/jquery.flot.js', 'content/js/plugins/flot/jquery.flot.time.js', 'content/js/plugins/flot/jquery.flot.tooltip.min.js', 'content/js/plugins/flot/jquery.flot.spline.js', 'content/js/plugins/flot/jquery.flot.resize.js', 'content/js/plugins/flot/jquery.flot.pie.js', 'content/js/plugins/flot/curvedLines.js', 'content/js/plugins/flot/angular-flot.js',]
                        },
                        {
                            name: 'angular-peity',
                            files: ['content/js/plugins/peity/jquery.peity.min.js', 'content/js/plugins/peity/angular-peity.js']
                        }
                    ]);
                }
            }
        })
        .state('error', {
            parent: 'layout',
            url: '/error',
            views: {
                'content@': {
                    templateUrl: 'app/views/common/error.html',
                    controller: 'ErrorPageController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Error page',
                authorities: []
            },
            params: {
                errorMessage: ''
            }
        })
        .state('accessdenied', {
            parent: 'layout',
            url: '/accessdenied',
            views: {
                'content@': {
                    templateUrl: 'app/views/common/accessdenied.html'
                }
            },
            data: {
                pageTitle: 'Access denied',
                authorities: []
            }
        })
        .state('login', {
            url: '/login',
            templateUrl: 'app/views/common/login.html',
            controller: 'LoginController',
            controllerAs: 'vm',
            data: {
                pageTitle: 'Login',
                specialClass: 'gray-bg'
            }
        })
        .state('register', {
            url: '/register',
            templateUrl: 'app/views/common/register.html',
            controller: 'RegisterController',
            controllerAs: 'vm',
            data: {
                pageTitle: 'Registration',
                specialClass: 'gray-bg register-background-img'
            }
        })
        .state('activate', {
            url: '/activate?key',
            templateUrl: 'app/views/common/activate.html',
            controller: 'ActivationController',
            controllerAs: 'vm',
            data: {
                pageTitle: 'Activate account',
                specialClass: 'gray-bg activate-background-img'
            }
        })
        .state('forgot-password', {
            url: '/forgot-password',
            templateUrl: 'app/views/common/forgot-password.html',
            controller: 'ForgotPasswordController',
            controllerAs: 'vm',
            data: {
                pageTitle: 'Send reset password email',
                specialClass: 'gray-bg forget-password-background-img'
            }
        })
        .state('reset-password', {
            url: '/reset-password?key',
            templateUrl: 'app/views/common/reset-password.html',
            controller: 'ResetPasswordController',
            controllerAs: 'vm',
            data: {
                pageTitle: 'Reset password',
                specialClass: 'gray-bg'
            }
        })
        .state('user', {
            abstract: true,
            parent: 'layout',
            data: {
                authorities: ['ROLE_USER']
            }
        })
        .state('profile', {
            parent: 'user',
            url: '/profile',
            views: {
                'content@': {
                    templateUrl: 'app/views/user/profile/profile.html',
                    controller: 'ProfileController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'User info'
            }
        })
        .state('password', {
            parent: 'user',
            url: '/password',
            views: {
                'content@': {
                    templateUrl: 'app/views/user/password/password.html',
                    controller: 'PasswordController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Change password'
            }
        })
        .state('developer', {
            abstract: true,
            parent: 'layout',
            data: {
                authorities: ['ROLE_DEVELOPER']
            }
        })
        .state('api', {
            parent: 'developer',
            url: '/api',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/api/api.html'
                }
            },
            data: {
                pageTitle: 'API'
            }
        })
        .state('api-docs', {
            parent: 'developer',
            url: '/api-docs',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/api-docs/api-docs.html'
                }
            },
            data: {
                pageTitle: 'API Docs'
            }
        })
        .state('metrics', {
            parent: 'developer',
            url: '/metrics',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/metrics/metrics.html',
                    controller: 'MetricsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Metrics'
            },
            resolve: {
                metrics: ['MetricsService', function (MetricsService) {
                    return MetricsService.getMetrics();
                }]
            }
        })
        .state('health', {
            parent: 'developer',
            url: '/health',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/health/health.html',
                    controller: 'HealthController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Health'
            }
        })
        .state('configuration', {
            parent: 'developer',
            url: '/configuration',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/configuration/configuration.html',
                    controller: 'ConfigurationController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Configuration'
            }
        })
        .state('beans', {
            parent: 'developer',
            url: '/beans',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/beans/beans.html',
                    controller: 'BeansController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Beans'
            }
        })
        .state('mappings', {
            parent: 'developer',
            url: '/mappings',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/mappings/mappings.html',
                    controller: 'MappingsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Mappings'
            }
        })
        .state('http-trace', {
            parent: 'developer',
            url: '/http-trace',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/trace/http-trace.html',
                    controller: 'HttpTraceController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Http Trace'
            }
        })
        .state('http-session', {
            parent: 'developer',
            url: '/http-session?page&sort&principal',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/session/http-session.html',
                    controller: 'HttpSessionController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Http Session'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'principal,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        principal: $stateParams.principal
                    };
                }]
            }
        })
        .state('audits', {
            parent: 'developer',
            url: '/audits',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/audits/audits.html',
                    controller: 'AuditsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Audits'
            }
        })
        .state('tracker', {
            parent: 'developer',
            url: '/tracker',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/tracker/tracker.html',
                    controller: 'TrackerController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Tracker'
            },
            onEnter: ['TrackerService', function (TrackerService) {
                TrackerService.subscribe();
            }],
            onExit: ['TrackerService', function (TrackerService) {
                TrackerService.unsubscribe();
            }]
        })
        .state('dict-list', {
            parent: 'developer',
            url: '/dict-list?page&sort&dictName',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/dict/dict-list.html',
                    controller: 'DictListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '数据字典列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'dictCode,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        dictName: $stateParams.dictName
                    };
                }]
            }
        })
        .state('dict-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建数据字典信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict/dict-dialog.html',
                    controller: 'DictDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            id: null,
                            dictCode: null,
                            dictName: null,
                            remark: null,
                            enabled: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('dict-list.edit', {
            url: '/edit/:id',
            data: {
                pageTitle: '编辑数据字典信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict/dict-dialog.html',
                    controller: 'DictDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DictService', function (DictService) {
                            return DictService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('dict-item-list', {
            parent: 'developer',
            url: '/dict-item-list?page&sort&dictCode&dictItemName',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/dict-item/dict-item-list.html',
                    controller: 'DictItemListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '数据字典项列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'dictCode,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        dictCode: $stateParams.dictCode,
                        dictItemName: $stateParams.dictItemName
                    };
                }]
            }
        })
        .state('dict-item-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建数据字典项信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict-item/dict-item-dialog.html',
                    controller: 'DictItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            id: null,
                            dictItemCode: null,
                            dictItemName: null,
                            dictCode: null,
                            remark: null,
                            enabled: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('dict-item-list.edit', {
            url: '/edit/:id',
            data: {
                pageTitle: '编辑数据字典项信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict-item/dict-item-dialog.html',
                    controller: 'DictItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DictItemService', function (DictItemService) {
                            return DictItemService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('logger', {
            parent: 'developer',
            url: '/logger',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/logger/logger.html',
                    controller: 'LoggerController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Loggers'
            }
        })
        .state('schedule', {
            parent: 'developer',
            url: '/schedule',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/schedule/schedule.html',
                    controller: 'ScheduleController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Schedule'
            }
        })
        .state('control', {
            parent: 'developer',
            url: '/control',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/control/control.html',
                    controller: 'ControlController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Control'
            }
        })
        .state('admin', {
            abstract: true,
            parent: 'layout',
            data: {
                authorities: ['ROLE_ADMIN']
            }
        })
        .state('authority', {
            abstract: true,
            parent: 'admin',
            data: {
                pageTitle: 'Authority'
            }
        })
        .state('authority.authority-list', {
            url: '/authority-list?page&sort',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/authority/authority-list.html',
                    controller: 'AuthorityListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Authority list'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'name,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {};
                }]
            }
        })
        .state('authority.authority-list.create', {
            url: '/create',
            data: {
                pageTitle: 'Create authority',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/authority/authority-dialog.html',
                    controller: 'AuthorityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            name: null,
                            enabled: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('authority.app-list', {
            url: '/app-list?page&sort',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app/app-list.html',
                    controller: 'AppListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Application list'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'name,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {};
                }]
            }
        })
        .state('authority.app-list.create', {
            url: '/create',
            data: {
                pageTitle: 'Create application',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/app/app-dialog.html',
                    controller: 'AppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            name: null,
                            authorities: null,
                            enabled: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('authority.app-list.edit', {
            url: '/edit/:id',
            data: {
                pageTitle: 'Edit application',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/app/app-dialog.html',
                    controller: 'AppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AppService', function (AppService) {
                            return AppService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('authority.app-list.view', {
            url: '/view/:id',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app/app-details.html',
                    controller: 'AppDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'View application'
            },
            resolve: {
                entity: ['AppService', '$stateParams', function (AppService, $stateParams) {
                    return AppService.get({id: $stateParams.id}).$promise;
                }]
            }
        })
        .state('authority.user-list', {
            url: '/user-list?page&sort&login',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/user/user-list.html',
                    controller: 'UserListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'User list'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'modifiedTime,desc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        login: $stateParams.login
                    };
                }]
            }
        })
        .state('authority.user-list.create', {
            url: '/create',
            data: {
                pageTitle: 'Create user',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/user/user-dialog.html',
                    controller: 'UserDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            id: null,
                            username: null,
                            firstName: null,
                            lastName: null,
                            email: null,
                            enabled: true,
                            activated: true,
                            createdBy: null,
                            createdTime: null,
                            modifiedBy: null,
                            modifiedTime: null,
                            resetTime: null,
                            resetKey: null
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('authority.user-list.edit', {
            url: '/edit/:id',
            data: {
                pageTitle: 'Edit user',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/user/user-dialog.html',
                    controller: 'UserDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserService', function (UserService) {
                            return UserService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('authority.user-list.view', {
            url: '/view/:id',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/user/user-details.html',
                    controller: 'UserDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'View user'
            },
            resolve: {
                entity: ['UserService', '$stateParams', function (UserService, $stateParams) {
                    return UserService.get({id: $stateParams.id}).$promise;
                }]
            }
        })
        .state('authority.menu-list', {
            url: '/menu-list?page&sort&appId',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/menu/menu-list.html',
                    controller: 'MenuListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Menu list'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'sequence,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        appId: $stateParams.appId
                    };
                }]
            }
        })
        .state('authority.menu-list.create', {
            url: '/create',
            data: {
                pageTitle: 'Create menu',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/menu/menu-dialog.html',
                    controller: 'MenuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            id: null,
                            appId: null,
                            code: null,
                            text: null,
                            path: null,
                            depth: null,
                            sequence: null,
                            parentId: '0',
                            enabled: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('authority.menu-list.edit', {
            url: '/edit/:id',
            data: {
                pageTitle: 'Edit menu',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/menu/menu-dialog.html',
                    controller: 'MenuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MenuService', function (MenuService) {
                            return MenuService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('authority.authority-menus', {
            url: '/authority-menu',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/authority-menu/authority-menu.html',
                    controller: 'AuthorityMenuController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Authority menus'
            },
            criteria: ['$stateParams', function ($stateParams) {
                return {
                    authorityName: ''
                };
            }]
        })
        .state('security', {
            abstract: true,
            parent: 'admin',
            data: {
                pageTitle: 'Security'
            }
        })
        .state('security.oauth2-client-list', {
            url: '/oauth2-client-list?page&sort&clientId',
            /** The stateParam name 'clientId' cannot be identical to the stateParam name 'id' of view or edit state */
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-client/oauth2-client-list.html',
                    controller: 'OAuth2ClientListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'OAuth2 client list'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'clientId,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        clientId: $stateParams.clientId
                    };
                }]
            }
        })
        .state('security.oauth2-client-list.create', {
            url: '/create',
            data: {
                pageTitle: 'Create OAuth2 client',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/oauth2-client/oauth2-client-dialog.html',
                    controller: 'OAuth2ClientDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            client_id: null,
                            rawClientSecret: null,
                            validityDays: 30,
                            clientAuthenticationMethods: [],
                            authorizationGrantTypes: [],
                            redirectUris: [],
                            scopes: []
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('security.oauth2-client-list.edit', {
            url: '/edit/:id',
            data: {
                pageTitle: 'Edit OAuth2 client',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/oauth2-client/oauth2-client-dialog.html',
                    controller: 'OAuth2ClientDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OAuth2ClientService', function (OAuth2ClientService) {
                            return OAuth2ClientService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('security.oauth2-client-list.view', {
            url: '/view/:id',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-client/oauth2-client-details.html',
                    controller: 'OAuth2ClientDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'View OAuth2 client'
            },
            resolve: {
                entity: ['OAuth2ClientService', '$stateParams', function (OAuth2ClientService, $stateParams) {
                    return OAuth2ClientService.get({id: $stateParams.id}).$promise;
                }]
            }
        })
        .state('security.oauth2-access-token-list', {
            url: '/oauth2-access-token-list?page&sort&tokenId&clientId&username&refreshToken',
            /** The stateParam name 'tokenId' cannot be identical to the stateParam name 'id' of view or edit state */
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-access-token/oauth2-access-token-list.html',
                    controller: 'OAuth2AccessTokenListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '访问令牌列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'modifiedTime,desc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        tokenId: $stateParams.tokenId,
                        clientId: $stateParams.clientId,
                        username: $stateParams.username,
                        refreshToken: $stateParams.refreshToken
                    };
                }]
            }
        })
        .state('security.oauth2-access-token-list.view', {
            url: '/view/:id',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-access-token/oauth2-access-token-details.html',
                    controller: 'OAuth2AccessTokenDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '查看访问令牌信息'
            },
            resolve: {
                entity: ['OAuth2AccessTokenService', '$stateParams', function (OAuth2AccessTokenService, $stateParams) {
                    return OAuth2AccessTokenService.get({id: $stateParams.id}).$promise;
                }]
            }
        })
        .state('security.oauth2-refresh-token-list', {
            url: '/oauth2-refresh-token-list?page&sort&tokenId&clientId&username',
            /** The stateParam name 'tokenId' cannot be identical to the stateParam name 'id' of view or edit state */
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-refresh-token/oauth2-refresh-token-list.html',
                    controller: 'OAuth2RefreshTokenListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '刷新令牌列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'modifiedTime,desc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        tokenId: $stateParams.tokenId,
                        clientId: $stateParams.clientId,
                        username: $stateParams.username
                    };
                }]
            }
        })
        .state('security.oauth2-refresh-token-list.view', {
            url: '/view/:id',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-refresh-token/oauth2-refresh-token-details.html',
                    controller: 'OAuth2RefreshTokenDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '查看刷新令牌信息'
            },
            resolve: {
                entity: ['OAuth2RefreshTokenService', '$stateParams', function (OAuth2RefreshTokenService, $stateParams) {
                    return OAuth2RefreshTokenService.get({id: $stateParams.id}).$promise;
                }]
            }
        })
        .state('security.oauth2-approval-list', {
            url: '/oauth2-approval-list?page&sort&approvalId&clientId&username',
            /** The stateParam name 'approvalId' cannot be identical to the stateParam name 'id' of view or edit state */
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-approval/oauth2-approval-list.html',
                    controller: 'OAuth2ApprovalListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '登录授权列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'clientId,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        approvalId: $stateParams.approvalId,
                        clientId: $stateParams.clientId,
                        username: $stateParams.username
                    };
                }]
            }
        })
        .state('security.oauth2-approval-list.view', {
            url: '/view/:id',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth2-approval/oauth2-approval-details.html',
                    controller: 'OAuth2ApprovalDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '查看登录授权信息'
            },
            resolve: {
                entity: ['OAuth2ApprovalService', '$stateParams', function (OAuth2ApprovalService, $stateParams) {
                    return OAuth2ApprovalService.get({id: $stateParams.id}).$promise;
                }]
            }
        })

}
function paginationConfig(uibPaginationConfig, PAGINATION_CONSTANTS) {
    uibPaginationConfig.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    uibPaginationConfig.maxSize = 5;
    uibPaginationConfig.boundaryLinks = true;
    uibPaginationConfig.firstText = '«';
    uibPaginationConfig.previousText = '‹';
    uibPaginationConfig.nextText = '›';
    uibPaginationConfig.lastText = '»';
}
function pagerConfig(uibPagerConfig, PAGINATION_CONSTANTS) {
    uibPagerConfig.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    uibPagerConfig.previousText = '«';
    uibPagerConfig.nextText = '»';
}
function httpConfig($urlRouterProvider, $httpProvider, httpRequestInterceptorCacheBusterProvider, $urlMatcherFactoryProvider) {
    //Cache everything except rest api requests
    httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

    $httpProvider.interceptors.push('alertErrorHandlerInterceptor');
    $httpProvider.interceptors.push('authExpiredInterceptor');
    $httpProvider.interceptors.push('authInterceptor');
    $httpProvider.interceptors.push('alertHandlerInterceptor');

    $urlMatcherFactoryProvider.type('boolean', {
        name: 'boolean',
        decode: function (val) {
            return val === true || val === 'true';
        },
        encode: function (val) {
            return val ? 1 : 0;
        },
        equals: function (a, b) {
            return this.is(a) && a === b;
        },
        is: function (val) {
            return [true, false, 0, 1].indexOf(val) >= 0;
        },
        pattern: /bool|true|0|1/
    });
}
function localStorageConfig($localStorageProvider, $sessionStorageProvider) {
    $localStorageProvider.setKeyPrefix('app-');
    $sessionStorageProvider.setKeyPrefix('app-');
}
function compileServiceConfig($compileProvider, DEBUG_INFO_ENABLED) {
    // disable debug data on prod profile to improve performance
    $compileProvider.debugInfoEnabled(DEBUG_INFO_ENABLED);

    /*
    If you wish to debug an application with this information
    then you should open up a debug console in the browser
    then call this method directly in this console:

    angular.reloadWithDebugInfo();
    */
}