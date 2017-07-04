@Infra
Feature: updating users

  As an admin
  I want to update any information of a user
  In order to let the repository up to date

  Scenario: An admin should be able to update any user
    Given I am the user 'admin'
    And the user 'client'
    When I want to set password = 'NEW PASSWORD' for this user
    Then the password or this user is updated

  Scenario: A non admin should not be able to update a user
    Given I am the user 'advisor'
    And the user 'admin'
    When I want to set password = 'NEW PASSWORD' for this user
    Then I am not authorized