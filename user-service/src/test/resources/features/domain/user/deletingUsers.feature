@Business
@DeleteUser
Feature: deleting users

  As an admin
  I want to remove a user
  In order to clean up repository

  Scenario: An admin should be able to delete any user
    Given I am a user with role 'ADMIN'
    And a user with role 'CLIENT'
    When I want to delete this user
    Then this user is deleted

  Scenario: A non admin should not be able to delete a user
    Given I am a user with role 'ADVISOR'
    And a user with role 'ADMIN'
    When I want to delete this user
    Then I am not authorized