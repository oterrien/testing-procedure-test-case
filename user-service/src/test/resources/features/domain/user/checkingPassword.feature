Feature: checking password

  As a user
  I want to check my password is correct
  In order to be able to change my password or to read my information

  Scenario Outline: A user should be able to check his own password is correct
    Given I am a user with role '<ROLE>'
    And my password is 'anyPassword'
    When I want to check my password is 'anyPassword'
    Then the password is correct

    Examples:
      | ROLE    |
      | ADMIN   |
      | CLIENT  |
      | ADVISOR |


  Scenario Outline: A user should be able to check his own password
    Given I am a user with role '<ROLE>'
    And my password is 'anyPassword'
    When I want to check my password is 'bad password'
    Then the password is not correct

    Examples:
      | ROLE    |
      | ADMIN   |
      | CLIENT  |
      | ADVISOR |

  Scenario Outline: A user should not be able to check password of another user
    Given I am a user with role '<MY ROLE>'
    And a user with role '<ROLE>'
    And his password is 'anyPassword'
    When I want to check password of this user is 'anyPassword'
    Then I am not authorized

    Examples:
      | MY ROLE | ROLE    |
      | ADMIN   | ADMIN   |
      | ADMIN   | CLIENT  |
      | ADMIN   | ADVISOR |
      | CLIENT  | ADMIN   |
      | CLIENT  | CLIENT  |
      | CLIENT  | ADVISOR |
      | ADVISOR | ADMIN   |
      | ADVISOR | CLIENT  |
      | ADVISOR | ADVISOR |