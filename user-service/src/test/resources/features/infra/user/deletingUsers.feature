@Infra
Feature: deleting users

  As an admin
  I want to remove a user
  In order to clean up repository

  Scenario: An admin should be able to delete any user
    Given I am the user 'admin'
    And the user 'client'
    When I want to delete this user
    Then this user is deleted

  Scenario: A non admin should not be able to delete a user
    Given I am the user 'client'
    And the user 'anotherClient'
    When I want to delete this user
    Then I am not authorized