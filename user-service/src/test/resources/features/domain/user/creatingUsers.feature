@Business
Feature: creating users

  As an admin
  I want to create users
  In order to retrieve them in the future

  Scenario: An admin should be able to create any user
    Given I am a user with role 'ADMIN'
    When I want to create a user with role 'CLIENT'
    Then this user is created

  Scenario: A non admin should not be able to create a user
    Given I am a user with role 'CLIENT'
    When I want to create a user with role 'ADMIN'
    Then I am not authorized