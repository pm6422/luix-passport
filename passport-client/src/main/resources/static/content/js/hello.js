angular.module('hello', [ 'ngRoute' ]).config(function($routeProvider, $httpProvider) {

	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home',
		controllerAs : 'controller'
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	$httpProvider.defaults.headers.common['Accept'] = 'application/json';

}).controller('navigation',

function($rootScope, $http, $location, $route) {

	var self = this;

	self.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};

	$http.get('open-api/accounts/user').then(function(response) {
		if (response.data.userName) {
			$rootScope.authenticated = true;
			$rootScope.userName = response.data.userName;
			
		} else {
			$rootScope.authenticated = false;
		}
	}, function() {
		$rootScope.authenticated = false;
	});

	self.credentials = {};

	self.logout = function() {
//           console.log('Post logout to auth server');
//           $http.post('logout', { withCredentials: true }).finally(function() {
//           });
           console.log('Post logout to ui client');
           $http.post('logout', {}).finally(function() {
               $rootScope.authenticated = false;
           })
        }

}).controller('home', function($http) {
	var self = this;
});
