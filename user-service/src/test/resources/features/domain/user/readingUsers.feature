@Business
@ReadUser
Feature: reading users

  As a user
  I want to read only my own information
  In order to check them

  Scenario: A user should be able to read his own information
    Given I am a user with role 'CLIENT'
    When I want to read my information
    Then my information are available

  Scenario: A non admin should not be able to retrieve another user
    Given I am a user with role 'CLIENT'
    And a user with role 'ADVISOR'
    When I want to find this user
    Then I am not authorized