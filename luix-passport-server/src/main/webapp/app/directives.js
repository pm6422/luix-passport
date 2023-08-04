/**
 * Directives
 */
angular
    .module('smartcloudserviceApp')
    .directive('pageRibbonDirective', pageRibbonDirective)
    .directive('pageTitleDirective', pageTitleDirective)
    .directive('sideNavigationDirective', sideNavigationDirective)
    .directive('iboxToolsDirective', iboxToolsDirective)
    .directive('minimalizeSidebarDirective', minimalizeSidebarDirective)
    .directive('sortDirective', sortDirective)
    .directive('sortByDirective', sortByDirective)
    .directive('showValidationDirective', showValidationDirective)
    .directive('hasAuthorityDirective', hasAuthorityDirective)
    .directive('hasAnyAuthorityDirective', hasAnyAuthorityDirective)
    .directive('passwordStrengthBarDirective', passwordStrengthBarDirective)
    .directive('treeView', treeView)
    .directive('jhiAlert', jhiAlert)
    .directive('jhiAlertError', jhiAlertError)

    .directive('vectorMap', vectorMap)
    .directive('sparkline', sparkline)
    .directive('icheck', icheck)
    .directive('ionRangeSlider', ionRangeSlider)
    .directive('dropZone', dropZone)
    .directive('responsiveVideo', responsiveVideo)
    .directive('chatSlimScroll', chatSlimScroll)
    .directive('customValid', customValid)
    .directive('fullScroll', fullScroll)
    .directive('closeOffCanvas', closeOffCanvas)
    .directive('clockPicker', clockPicker)
    .directive('landingScrollspy', landingScrollspy)
    .directive('fitHeight', fitHeight)
    .directive('iboxToolsFullScreenDirective', iboxToolsFullScreenDirective)
    .directive('slimScroll', slimScroll)
    .directive('truncate', truncate)
    .directive('touchSpin', touchSpin)
    .directive('markdownEditor', markdownEditor)
    .directive('passwordMeter', passwordMeter);

/**
 * pageRibbonDirective
 */
function pageRibbonDirective(RIBBON_PROFILE) {
    return {
        replace: true,
        restrict: 'AE',
        template: '<div class="ribbon hidden"><a href="" >{{ribbonProfile}}</a></div>',
        link: function (scope, element) {
            if (RIBBON_PROFILE) {
                scope.ribbonProfile = RIBBON_PROFILE.toUpperCase();
                element.addClass(RIBBON_PROFILE);
                element.removeClass('hidden');
            }
        }
    }
}
/**
 * pageTitleDirective - Directive for set Page title - mata title
 */
function pageTitleDirective($rootScope, $timeout, APP_NAME) {
    return {
        link: function(scope, element) {
            var listener = function(event, toState, toParams, fromState, fromParams) {
                // Default title - load on Dashboard
                var title = APP_NAME + ' | Dashboard';
                // Create your own title pattern
                if (toState.data && toState.data.pageTitle) title = APP_NAME + ' | ' + toState.data.pageTitle;
                $timeout(function() {
                    element.text(title);
                });
            };
            $rootScope.$on('$stateChangeStart', listener);
        }
    }
}
/**
 * sideNavigationDirective - Directive for run metsiMenu on sidebar navigation
 */
function sideNavigationDirective($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element) {
//            // Call the metsiMenu plugin and plug it to sidebar navigation
//            $timeout(function(){
//                element.metisMenu();
//            });

            // Collapse menu in mobile mode after click on element
            var menuElement = $('#side-menu a:not([href$="\\#"])');
            menuElement.click(function(){
                if ($(window).width() < 769) {
                    $("body").toggleClass("mini-navbar");
                }
            });

            // Enable initial fixed sidebar
            if ($("body").hasClass('fixed-sidebar')) {
                var sidebar = element.parent();
                sidebar.slimScroll({
                    height: '100%',
                    railOpacity: 0.9,
                });
            }
        }
    };
}
/**
 * iboxToolsDirective - Directive for iBox tools elements in right corner of ibox
 */
function iboxToolsDirective($timeout) {
    return {
        restrict: 'A',
        scope: true,
        templateUrl: 'app/views/common/ibox-tools.html',
        controller: function ($scope, $element) {
            // Function for collapse ibox
            $scope.showhide = function () {
                var ibox = $element.closest('div.ibox');
                var icon = $element.find('i.fa-chevron-up,i.fa-chevron-down');
                var content = ibox.children('.ibox-content');
                content.slideToggle(200);
                // Toggle icon from up to down
                icon.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
                ibox.toggleClass('').toggleClass('border-bottom');
                $timeout(function () {
                    ibox.resize();
                    ibox.find('[id^=map-]').resize();
                }, 50);
            };
            // Function for close ibox
            $scope.closebox = function () {
                var ibox = $element.closest('div.ibox');
                ibox.remove();
            }
        }
    };
}

/**
 * iboxToolsDirective with full screen - Directive for iBox tools elements in right corner of ibox with full screen option
 */
function iboxToolsFullScreenDirective($timeout) {
    return {
        restrict: 'A',
        scope: true,
        templateUrl: 'app/views/common/ibox-tools-full-screen.html',
        controller: function ($scope, $element) {
            // Function for collapse ibox
            $scope.showhide = function () {
                var ibox = $element.closest('div.ibox');
                var icon = $element.find('i.fa-chevron-up,i.fa-chevron-down');
                var content = ibox.children('.ibox-content');
                content.slideToggle(200);
                // Toggle icon from up to down
                icon.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
                ibox.toggleClass('').toggleClass('border-bottom');
                $timeout(function () {
                    ibox.resize();
                    ibox.find('[id^=map-]').resize();
                }, 50);
            };
            // Function for close ibox
            $scope.closebox = function () {
                var ibox = $element.closest('div.ibox');
                ibox.remove();
            };
            // Function for full screen
            $scope.fullscreen = function () {
                var ibox = $element.closest('div.ibox');
                var icon = $element.find('i.fa-expand,i.fa-compress');
                $('body').toggleClass('fullscreen-ibox-mode');
                icon.toggleClass('fa-expand').toggleClass('fa-compress');
                ibox.toggleClass('fullscreen');
                setTimeout(function() {
                    $(window).trigger('resize');
                }, 100);
            }
        }
    };
}

/**
 * minimalizeSidebarDirective - Directive for minimalize sidebar
*/
function minimalizeSidebarDirective($timeout) {
    return {
        restrict: 'A',
        template: '<a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="" ng-click="minimalize()"><i class="fa fa-bars"></i></a>',
        controller: function ($scope, $element) {
            $scope.minimalize = function () {
                $("body").toggleClass('mini-navbar');
                if (!$('body').hasClass('mini-navbar') || $('body').hasClass('body-small')) {
                    // Hide menu in order to smoothly turn on when maximize menu
                    $('#side-menu').hide();
                    // For smoothly turn on menu
                    setTimeout(
                        function () {
                            $('#side-menu').fadeIn(400);
                        }, 200);
                } else if ($('body').hasClass('fixed-sidebar')){
                    $('#side-menu').hide();
                    setTimeout(
                        function () {
                            $('#side-menu').fadeIn(400);
                        }, 100);
                } else {
                    // Remove all inline style from jquery fadeIn function to reset menu state
                    $('#side-menu').removeAttr('style');
                }
            }
        }
    };
}
/**
 * sortDirective
 */
function sortDirective() {
    return {
        restrict: 'A',
        scope: {
            predicate: '=sortDirective',
            ascending: '=',
            callback: '&'
        },
        controller: function($scope, $element) {
            var vm = this;

            vm.applyClass = applyClass;
            vm.resetClasses = resetClasses;
            vm.sort = sort;
            vm.triggerApply = triggerApply;

            $scope.$watchGroup(['vm.predicate', 'vm.ascending'], vm.triggerApply);
            vm.triggerApply();

            function applyClass (element) {
                var thisIcon = element.find('span.fa'),
                    sortIcon = 'fa-sort',
                    sortAsc = 'fa-sort-amount-asc',
                    sortDesc = 'fa-sort-amount-desc',
                    remove = sortIcon + ' ' + sortDesc,
                    add = sortAsc;
                if (!vm.ascending) {
                    remove = sortIcon + ' ' + sortAsc;
                    add = sortDesc;
                }
                vm.resetClasses();
                thisIcon.removeClass(remove);
                thisIcon.addClass(add);
            }

            function resetClasses () {
                var allThIcons = $element.find('span.fa'),
                    sortIcon = 'fa-sort',
                    sortAsc = 'fa-sort-amount-asc',
                    sortDesc = 'fa-sort-amount-desc';
                allThIcons.removeClass(sortAsc + ' ' + sortDesc);
                allThIcons.addClass(sortIcon);
            }

            function sort (field) {
                if (field !== vm.predicate) {
                    vm.ascending = true;
                } else {
                    vm.ascending = !vm.ascending;
                }
                vm.predicate = field;
                $scope.$apply();
                vm.callback();
            }

            function triggerApply (values)  {
                vm.resetClasses();
                if (values && values[0] !== '_score') {
                    vm.applyClass($element.find('th[sort-by-directive=\'' + values[0] + '\']'));
                }
            }
        },
        controllerAs: 'vm',
        bindToController: true
    }
}
/**
 * sortByDirective
 */
function sortByDirective() {
    return {
        restrict: 'A',
        scope: false,
        require: '^sortDirective',
        link: function(scope, element, attrs, parentCtrl) {
            element.bind('click', function () {
                parentCtrl.sort(attrs.sortByDirective);
            });
        }
    }
}
/**
 * showValidationDirective
 */
function showValidationDirective() {
    return {
        restrict: 'A',
        require: 'form',
        link: function (scope, element) {
            element.find('.form-group').each(function() {
                var $formGroup = angular.element(this);
                var $inputs = $formGroup.find('input[ng-model],textarea[ng-model],select[ng-model]');

                if ($inputs.length > 0) {
                    $inputs.each(function() {
                        var $input = angular.element(this);
                        scope.$watch(function() {
                            return $input.hasClass('ng-invalid') && $input.hasClass('ng-dirty');
                        }, function(isInvalid) {
                            $formGroup.toggleClass('has-error', isInvalid);
                        });
                    });
                }
            });
        }
    };
}
/**
 * hasAuthorityDirective
 */
function hasAuthorityDirective(PrincipalService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var authority = attrs.hasAuthorityDirective.replace(/\s+/g, '');

            var setVisible = function () {
                    element.removeClass('hidden');
                },
                setHidden = function () {
                    element.addClass('hidden');
                },
                defineVisibility = function (reset) {

                    if (reset) {
                        setVisible();
                    }

                    PrincipalService.hasAuthority(authority)
                        .then(function (result) {
                            if (result) {
                                setVisible();
                            } else {
                                setHidden();
                            }
                        });
                };

            if (authority.length > 0) {
                defineVisibility(true);

                scope.$watch(function() {
                    return PrincipalService.isAuthenticated();
                }, function() {
                    defineVisibility(true);
                });
            }
        }
    };
}
/**
 * hasAnyAuthorityDirective
 */
function hasAnyAuthorityDirective(PrincipalService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var authorities = attrs.hasAnyAuthorityDirective.replace(/\s+/g, '').split(',');

            var setVisible = function () {
                    element.removeClass('hidden');
                },
                setHidden = function () {
                    element.addClass('hidden');
                },
                defineVisibility = function (reset) {
                    var result;
                    if (reset) {
                        setVisible();
                    }

                    result = PrincipalService.hasAnyAuthority(authorities);
                    if (result) {
                        setVisible();
                    } else {
                        setHidden();
                    }
                };

            if (authorities.length > 0) {
                defineVisibility(true);

                scope.$watch(function() {
                    return PrincipalService.isAuthenticated();
                }, function() {
                    defineVisibility(true);
                });
            }
        }
    };
}
/**
 * passwordStrengthBarDirective
 */
function passwordStrengthBarDirective() {
    return {
        replace: true,
        restrict: 'E',
        template: '<div id="strength">' +
            '<small>Password strength:</small>' +
            '<ul id="strengthBar">' +
            '<li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li>' +
            '</ul>' +
            '</div>',
        scope: {
            passwordToCheck: '='
        },
        link: function (scope, iElement) {
        	var strength = {
                colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                mesureStrength: function (p) {

                    var _force = 0;
                    var _regex = /[$-/:-?{-~!"^_`\[\]]/g; // "

                    var _lowerLetters = /[a-z]+/.test(p);
                    var _upperLetters = /[A-Z]+/.test(p);
                    var _numbers = /[0-9]+/.test(p);
                    var _symbols = _regex.test(p);

                    var _flags = [_lowerLetters, _upperLetters, _numbers, _symbols];
                    var _passedMatches = $.grep(_flags, function (el) {
                        return el === true;
                    }).length;

                    _force += 2 * p.length + ((p.length >= 10) ? 1 : 0);
                    _force += _passedMatches * 10;

                    // penality (short password)
                    _force = (p.length <= 6) ? Math.min(_force, 10) : _force;

                    // penality (poor variety of characters)
                    _force = (_passedMatches === 1) ? Math.min(_force, 10) : _force;
                    _force = (_passedMatches === 2) ? Math.min(_force, 20) : _force;
                    _force = (_passedMatches === 3) ? Math.min(_force, 40) : _force;

                    return _force;

                },
                getColor: function (s) {

                    var idx = 0;
                    if (s <= 10) {
                        idx = 0;
                    }
                    else if (s <= 20) {
                        idx = 1;
                    }
                    else if (s <= 30) {
                        idx = 2;
                    }
                    else if (s <= 40) {
                        idx = 3;
                    }
                    else {
                        idx = 4;
                    }

                    return { idx: idx + 1, col: this.colors[idx] };
                }
            };
            scope.$watch('passwordToCheck', function (password) {
                if (password) {
                    var c = strength.getColor(strength.mesureStrength(password));
                    iElement.removeClass('ng-hide');
                    iElement.find('ul').children('li')
                        .css({ 'background-color': '#DDD' })
                        .slice(0, c.idx)
                        .css({ 'background-color': c.col });
                }
            });
        }
    };
}
/**
 * 参数说明
 *  tree-data：树形数组
 *  text-field：显示的文本字段
 *  value-field:id
 *  check-field:是否被选中属性名称
 *  children-field：子类选项名称
 *  item-clicked：可在controller中自定义点击事件
 *  item-checked-changed：checkbox选中状态改变回调函数
 *  can-checked：是否显示checkbox多选框
 * 使用示例
 * <tree-view tree-data="allMenus" text-field="label" value-field='adminMenuId' check-field="check" children-field="children" item-clicked="itemClicked($item)" item-checked-changed="itemCheckedChanged($item)" can-checked="true" ></tree-view>
 * 树形模板
 *<script type="text/ng-template" id="/treeView.html">
 <ul class="tree-view">
 <li ng-repeat="item in treeData" ng-include="itemTemplateUrl || '/treeItem.html'" ></li>
 </ul>
 </script>

 <script type="text/ng-template" id="/treeItem.html">
 <i ng-click="itemExpended(item, $event);" class="{{getItemIcon(item)}}"></i>

 <input type="checkbox" ng-model="item[checkField]" class="check-box" ng-if="canChecked"  ng-checked="item[checkField]"  ng-click="isCheck(treeData,item)" ng-change="warpCallback('itemCheckedChanged', item, $event);">
 <span class='text-field' ng-click="warpCallback('itemClicked', item, $event);">{{item[textField]}}</span>
 <ul ng-if="!isLeaf(item)"
 ng-show="item.$$isExpend"
 >
 <li ng-repeat="item in item[childrenField]" ng-include="itemTemplateUrl || '/treeItem.html'">
 </li>
 </ul>
 </script>
 */
function treeView() {
    return {
        restrict: 'E',
        templateUrl: '/treeView.html',
        scope: {
            treeData: '=',
            canChecked: '=',
            textField: '@',
            checkField: '@',
            childrenField: '@',
            itemClicked: '&',
            itemCheckedChanged: '&',
            itemTemplateUrl: '@'
        },
        controller: ['$scope', function ($scope) {
            $scope.itemExpended = function (item, $event) {
                item.$$isExpend = !item.$$isExpend;
                $event.stopPropagation();
            };
            $scope.getItemIcon = function (item) {
                var isLeaf = $scope.isLeaf(item);

                if (isLeaf) {
                    return 'fa fa-leaf';
                }
                return item.$$isExpend ? 'fa fa-minus' : 'fa fa-plus';
            };

            $scope.isLeaf = function (item) {
                return !item.children || !item.children.length;
            };

            $scope.warpCallback = function (callback, item, $event) {
                ($scope[callback] || angular.noop)({
                    $item: item,
                    $event: $event
                });
            };
        }],
        link: function (scope, element, attrs) {
            scope.isCheck = function (treeData, item) {
                //子选项被选中,对应父选项选中
                var check = attrs.checkField;
                var checkValue = attrs.valueField;
                var children = attrs.childrenField;

                var isCheck = item[check];
                var id = item[checkValue];
                if (isCheck) {
                    for (var i = 0; i < treeData.length; i++) {
                        if (id == treeData[i][checkValue]) {
                            break;
                        }
                        if (treeData[i][children]) {
                            isParentCheck(id, treeData[i][children], treeData[i], checkValue, check)
                        }
                    }
                }
                //子项都未被选中
                if (!isCheck) {
                    for (var i = 0; i < treeData.length; i++) {
                        if (id == treeData[i][checkValue]) {
                            break;
                        }
                        if (treeData[i][children]) {
                            isParentUnCheck(id, treeData[i][children], treeData[i], checkValue, check)
                        }
                    }
                }
                //父项被选中，子项也被选中
                item[children] = changeCheck(item[children], isCheck, check, children);
            };

            function isParentUnCheck(id, children, parent, checkValue, check) {
                var flag = true;
                var isEqual = false;
                for (var i = 0; i < children.length; i++) {
                    //有被选中的子项
                    if (children[i][check]) {
                        flag = false;
                    }
                    //子项在父项中
                    if (children[i][checkValue] == id) {
                        isEqual = true;
                    }
                }
                if (isEqual && flag) {
                    parent[check] = false;
                }
            }

            function isParentCheck(id, children, parent, checkValue, check) {
                for (var i = 0; i < children.length; i++) {
                    if (children[i][checkValue] == id) {
                        parent[check] = true;
                    }
                }
            }

            //子元素 全选、全不选
            function changeCheck(items, isCheck, check, children) {
                if (items) {
                    for (var i = 0; i < items.length; i++) {
                        items[i][check] = isCheck;
                        changeCheck(items[i][children], isCheck);
                    }
                }
                return items;
            }
        }
    };
}
/**
 * does not work
 */
function jhiAlert() {
    return {
        restrict: 'A',
        template: '<div class="alerts" ng-cloak="">' +
                      '<div ng-repeat="alert in $ctrl.alerts" ng-class="[alert.position, {\'toast\': alert.toast}]">' +
                          '<uib-alert ng-cloak="" type="{{alert.type}}" close="alert.close($ctrl.alerts)"><pre ng-bind-html="alert.msg"></pre></uib-alert>' +
                      '</div>' +
                  '</div>',
        controller: function ($scope, AlertUtils) {
            var vm = this;

            vm.alerts = AlertUtils.get();
            $scope.$on('$destroy', function () {
                vm.alerts = [];
            });
        }
    };
}
/**
 * does not work
 */
function jhiAlertError() {
    return {
        restrict: 'A',
        template: '<div class="alerts" ng-cloak="">' +
                      '<div ng-repeat="alert in $ctrl.alerts" ng-class="[alert.position, {\'toast\': alert.toast}]">' +
                          '<uib-alert ng-cloak="" type="{{alert.type}}" close="alert.close($ctrl.alerts)"><pre>{{ alert.msg }}</pre></uib-alert>' +
                      '</div>' +
                  '</div>',
        controller: function ($rootScope, $scope, AlertUtils) {
            var vm = this;

            vm.alerts = [];

            function addErrorAlert (message, key, data) {
                vm.alerts.push(
                    AlertUtils.add(
                        {
                            type: 'danger',
                            msg: message,
                            toast: AlertUtils.isToast(),
                            scoped: true
                        },
                        vm.alerts
                    )
                );
            }

            var cleanHttpErrorListener = $rootScope.$on('smartcloudserviceApp.httpError', function (event, httpResponse) {
                vm.alerts = [];
                var i;
                event.stopPropagation();
                switch (httpResponse.status) {
                // connection refused, server not reachable
                case 0:
                    addErrorAlert('Server not reachable', 'error.server.not.reachable');
                    break;
                case 400:
                    if (httpResponse.data && httpResponse.data.fieldErrors) {
                        for (i = 0; i < httpResponse.data.fieldErrors.length; i++) {
                            var fieldError = httpResponse.data.fieldErrors[i];
                            addErrorAlert(fieldError.message);
                        }
                    } else if (httpResponse.data && httpResponse.data.message) {
                        addErrorAlert(httpResponse.data.message, httpResponse.data.message, httpResponse.data);
                    } else {
                        addErrorAlert(httpResponse.data);
                    }
                    break;
                case 404:
                    addErrorAlert('Not found', 'error.url.not.found');
                    break;
                default:
                    if (httpResponse.data && httpResponse.data.message) {
                        addErrorAlert(httpResponse.data.message);
                    } else {
                        addErrorAlert(angular.toJson(httpResponse));
                    }
                }
            });

            $scope.$on('$destroy', function () {
                if(angular.isDefined(cleanHttpErrorListener) && cleanHttpErrorListener !== null){
                    cleanHttpErrorListener();
                    vm.alerts = [];
                }
            });
        }
    };
}
/**
 * responsibleVideo - Directive for responsive video
 */
function responsiveVideo() {
    return {
        restrict: 'A',
        link:  function(scope, element) {
            var figure = element;
            var video = element.children();
            video
                .attr('data-aspectRatio', video.height() / video.width())
                .removeAttr('height')
                .removeAttr('width');

            //We can use $watch on $window.innerWidth also.
            $(window).resize(function() {
                var newWidth = figure.width();
                video
                    .width(newWidth)
                    .height(newWidth * video.attr('data-aspectRatio'));
            }).resize();
        }
    }
}
function closeOffCanvas() {
    return {
        restrict: 'A',
        template: '<a class="close-canvas-menu" ng-click="closeOffCanvas()"><i class="fa fa-times"></i></a>',
        controller: function ($scope, $element) {
            $scope.closeOffCanvas = function () {
                $("body").toggleClass("mini-navbar");
            }
        }
    };
}

/**
 * vectorMap - Directive for Vector map plugin
 */
function vectorMap() {
    return {
        restrict: 'A',
        scope: {
            myMapData: '=',
        },
        link: function (scope, element, attrs) {
            var map = element.vectorMap({
                map: 'world_mill_en',
                backgroundColor: "transparent",
                regionStyle: {
                    initial: {
                        fill: '#e4e4e4',
                        "fill-opacity": 0.9,
                        stroke: 'none',
                        "stroke-width": 0,
                        "stroke-opacity": 0
                    }
                },
                series: {
                    regions: [
                        {
                            values: scope.myMapData,
                            scale: ["#1ab394", "#22d6b1"],
                            normalizeFunction: 'polynomial'
                        }
                    ]
                },
            });
            var destroyMap = function(){
                element.remove();
            };
            scope.$on('$destroy', function() {
                destroyMap();
            });
        }
    }
}


/**
 * sparkline - Directive for Sparkline chart
 */
function sparkline() {
    return {
        restrict: 'A',
        scope: {
            sparkData: '=',
            sparkOptions: '=',
        },
        link: function (scope, element, attrs) {
            scope.$watch(scope.sparkData, function () {
                render();
            });
            scope.$watch(scope.sparkOptions, function(){
                render();
            });
            var render = function () {
                $(element).sparkline(scope.sparkData, scope.sparkOptions);
            };
        }
    }
}
/**
 * icheck - Directive for custom checkbox icheck
 */
function icheck($timeout) {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function($scope, element, $attrs, ngModel) {
            return $timeout(function() {
                var value;
                value = $attrs['value'];

                $scope.$watch($attrs['ngModel'], function(newValue){
                    $(element).iCheck('update');
                });

                return $(element).iCheck({
                    checkboxClass: 'icheckbox_square-green',
                    radioClass: 'iradio_square-green'

                }).on('ifChanged', function(event) {
                    if ($(element).attr('type') === 'checkbox' && $attrs['ngModel']) {
                        $scope.$apply(function () {
                            return ngModel.$setViewValue(event.target.checked);
                        });
                    }
                    if ($(element).attr('type') === 'radio' && $attrs['ngModel']) {
                        return $scope.$apply(function () {
                            return ngModel.$setViewValue(value);
                        });
                    }
                });
            });
        }
    };
}

/**
 * ionRangeSlider - Directive for Ion Range Slider
 */
function ionRangeSlider() {
    return {
        restrict: 'A',
        scope: {
            rangeOptions: '='
        },
        link: function (scope, elem, attrs) {
            elem.ionRangeSlider(scope.rangeOptions);
        }
    }
}

/**
 * dropZone - Directive for Drag and drop zone file upload plugin
 */
function dropZone() {
    return {
        restrict: 'C',
        link: function(scope, element, attrs) {

            var config = {
                url: 'http://localhost:8080/upload',
                maxFilesize: 100,
                paramName: "uploadfile",
                maxThumbnailFilesize: 10,
                parallelUploads: 1,
                autoProcessQueue: false
            };

            var eventHandlers = {
                'addedfile': function(file) {
                    scope.file = file;
                    if (this.files[1]!=null) {
                        this.removeFile(this.files[0]);
                    }
                    scope.$apply(function() {
                        scope.fileAdded = true;
                    });
                },

                'success': function (file, response) {
                }

            };

            dropzone = new Dropzone(element[0], config);

            angular.forEach(eventHandlers, function(handler, event) {
                dropzone.on(event, handler);
            });

            scope.processDropzone = function() {
                dropzone.processQueue();
            };

            scope.resetDropzone = function() {
                dropzone.removeAllFiles();
            }
        }
    }
}

/**
 * chatSlimScroll - Directive for slim scroll for small chat
 */
function chatSlimScroll($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element) {
            $timeout(function(){
                element.slimscroll({
                    height: '234px',
                    railOpacity: 0.4
                });

            });
        }
    };
}

/**
 * customValid - Directive for custom validation example
 */
function customValid(){
    return {
        require: 'ngModel',
        link: function(scope, ele, attrs, c) {
            scope.$watch(attrs.ngModel, function() {

                // You can call a $http method here
                // Or create custom validation

                var validText = "Inspinia";

                if(scope.extras == validText) {
                    c.$setValidity('cvalid', true);
                } else {
                    c.$setValidity('cvalid', false);
                }

            });
        }
    }
}


/**
 * fullScroll - Directive for slimScroll with 100%
 */
function fullScroll($timeout){
    return {
        restrict: 'A',
        link: function(scope, element) {
            $timeout(function(){
                element.slimscroll({
                    height: '100%',
                    railOpacity: 0.9
                });

            });
        }
    };
}

/**
 * slimScroll - Directive for slimScroll with custom height
 */
function slimScroll($timeout){
    return {
        restrict: 'A',
        scope: {
            boxHeight: '@'
        },
        link: function(scope, element) {
            $timeout(function(){
                element.slimscroll({
                    height: scope.boxHeight,
                    railOpacity: 0.9
                });

            });
        }
    };
}

/**
 * clockPicker - Directive for clock picker plugin
 */
function clockPicker() {
    return {
        restrict: 'A',
        link: function(scope, element) {
                element.clockpicker();
        }
    };
}
/**
 * landingScrollspy - Directive for scrollspy in landing page
 */
function landingScrollspy(){
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.scrollspy({
                target: '.navbar-fixed-top',
                offset: 80
            });
        }
    }
}

/**
 * fitHeight - Directive for set height fit to window height
 */
function fitHeight(){
    return {
        restrict: 'A',
        link: function(scope, element) {
            element.css("height", $(window).height() + "px");
            element.css("min-height", $(window).height() + "px");
        }
    };
}

/**
 * truncate - Directive for truncate string
 */
function truncate($timeout){
    return {
        restrict: 'A',
        scope: {
            truncateOptions: '='
        },
        link: function(scope, element) {
            $timeout(function(){
                element.dotdotdot(scope.truncateOptions);

            });
        }
    };
}


/**
 * touchSpin - Directive for Bootstrap TouchSpin
 */
function touchSpin() {
    return {
        restrict: 'A',
        scope: {
            spinOptions: '='
        },
        link: function (scope, element, attrs) {
            scope.$watch(scope.spinOptions, function(){
                render();
            });
            var render = function () {
                $(element).TouchSpin(scope.spinOptions);
            };
        }
    }
}
/**
 * markdownEditor - Directive for Bootstrap Markdown
 */
function markdownEditor() {
    return {
        restrict: "A",
        require:  'ngModel',
        link:     function (scope, element, attrs, ngModel) {
            $(element).markdown({
                savable:false,
                onChange: function(e){
                    ngModel.$setViewValue(e.getContent());
                }
            });
        }
    }
}
/**
 * passwordMeter - Directive for jQuery Password Strength Meter
 */
function passwordMeter() {
    return {
        restrict: 'A',
        scope: {
            pwOptions: '='
        },
        link: function (scope, element, attrs) {
            scope.$watch(scope.pwOptions, function(){
                render();
            });
            var render = function () {
                $(element).pwstrength(scope.pwOptions);
            };
        }
    }
}
