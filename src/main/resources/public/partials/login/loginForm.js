angular.module('learny').directive(
        'learnyloginform',
        function() {
            return {
                restrict : 'E',
                templateUrl : 'partials/login/loginForm.html',
                scope : {},
                controller : [
                        '$scope',
                        '$state',
                        'serverCommunicator',
                        function($scope, $state, serverCommunicator) {
                            
                            $scope.login = function() {
                                serverCommunicator.loginAsync($scope.username, $scope.password)
                                        .success(function(data, status, headers, config) {
                                            console.log('Successfully logged in');
                                            $("#loginModal").one('hidden.bs.modal', function(e) {
                                                $state.go('app.home');
                                            }).modal("hide");
                                           
                                        }).error(function(data, status, headers, config) {
                                            console.log('Login failed');
                                        });

                            };
                            $scope.goToRegister = function() {
                                $("#loginModal").one('hidden.bs.modal', function(e) {
                                    $state.go('createAccount');
                                }).modal("hide");

                            };
                        } ],

            }
        });