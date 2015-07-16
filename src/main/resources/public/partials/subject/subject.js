angular
        .module('learny')
        .controller(
                'subjectController',
                [
                        '$scope',
                        '$state',
                        'serverCommunicator',
                        'tests',
                        'subject',
                        function($scope, $state, serverCommunicator, tests, subject) {

                            $scope.tests = tests.value.data;
                            $scope.subject = subject.value.data;

                            if ($scope.currentUser.isGlobalAdmin()
                                    || $scope.currentUser.isAdminOf($scope.subject.id)) {
                                $scope.userIsAdmin = true;
                            } else {
                                $scope.userIsAdmin = false;
                            }

                            if ($scope.currentUser.isEnroledIn($scope.subject.id)) {
                                $scope.userIsEnroled = true;
                            } else {
                                $scope.userIsEnroled = false;
                            }

                            if (!$scope.userIsAdmin && !$scope.userIsEnroled) {
                                $scope.userIsNew = true;
                            } else {
                                $scope.userIsNew = false;
                            }

                            $scope.joinSubject = function() {
                                serverCommunicator.joinSubjectAsync($scope.subject).then(
                                        function(data) {
                                            $state.go($state.current, {}, {
                                                reload : true
                                            });
                                        }, function() {
                                            console.log("Eintragen fehlgeschlagen");
                                        });
                            }

                            $scope.leaveSubject = function() {
                                serverCommunicator.leaveSubjectAsync($scope.subject).then(
                                        function(data) {
                                            $state.go($state.current, {}, {
                                                reload : true
                                            });
                                        }, function() {
                                            console.log("Austragen fehlgeschlagen");
                                        });
                            }

                            $scope.addResponsible = function(user) {
                                serverCommunicator.addUserAsAdministratorToSubjectAsync(
                                        user.accountName, $scope.subject.id).then(function(data) {
                                    $state.go($state.current, {}, {
                                        reload : true
                                    });
                                }, function(data) {
                                    $state.go($state.current, {}, {
                                        reload : true
                                    });
                                })
                            }

                            $scope.removeFromResponsibles = function(person) {
                                serverCommunicator.removeAdministratorFromSubjectAsync(person.id,
                                        $scope.subject.id).then(function(data) {
                                    $state.go($state.current, {}, {
                                        reload : true
                                    });
                                }, function(data) {
                                });
                            }

                            $scope.deleteTest = function(test) {
                                serverCommunicator.deleteTestAsync(test.id).then(function(data) {
                                    $state.go($state.current, {}, {
                                        reload : true
                                    });
                                });
                            }

                            $scope.deleteTestModal = function(test) {
                                $scope.global
                                        .showGlobalModal(
                                                "Sicher, dass du den Test löschen möchtest? Alle Ergebnisse werden damit mit gelöscht!",
                                                function() {
                                                    $scope.deleteTest(test);
                                                });
                            }

                            $scope.updateSubject = function() {
                                serverCommunicator.updateSubjectAsync($scope.subject).then(
                                        function() {
                                            $scope.editDescriptionBool = false;
                                        });
                            }

                            $scope.editDescription = function() {
                                $scope.editDescriptionBool = true;
                            }

                            $scope.removeResponsibleModal = function(responsible) {
                                $scope.global.showGlobalModal(
                                        "Willst du den Verantwortlichen wirklich entfernen?",
                                        function() {
                                            $scope.removeFromResponsibles(responsible)
                                        });
                            }

                        } ]);