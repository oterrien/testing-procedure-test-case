@Business
Feature: changing password

  As a client or an advisor
  I want to update my password
  In order to ensure my connections

  Scenario: A user should be able to change his own password
    Given I am a user with role 'CLIENT'
    When I want to change my password to 'NEW PASSWORD'
    Then my password is changed

  Scenario: A user should not be able to change password of another user
    Given I am a user with role 'CLIENT'
    And a user with role 'ADVISOR'
    When I want to change password of this user to 'NEW PASSWORD'
    Then I am not authorized