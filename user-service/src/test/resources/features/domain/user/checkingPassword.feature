@Business
@CheckPassword
Feature: checking password

  As a user
  I want to check my password is correct
  In order to be able to change my password or to read my information

  Scenario: A user should be able to check his own password is correct
    Given I am a user with role 'ADVISOR'
    And my password is 'anyPassword'
    When I want to check my password is 'anyPassword'
    Then this password is correct

  Scenario: A user should be able to check his own password is incorrect
    Given I am a user with role 'CLIENT'
    And my password is 'anyPassword'
    When I want to check my password is 'bad password'
    Then this password is not correct

  Scenario: A user should not be able to check password of another user
    Given I am a user with role 'CLIENT'
    And a user with role 'ADMIN'
    And his password is 'anyPassword'
    When I want to check password of this user is 'anyPassword'
    Then I am not authorized