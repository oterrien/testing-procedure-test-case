Feature: changing password

  As a client or an advisor
  I want to update my password
  In order to ensure my connections

  Scenario Outline: A user should be able to change his own password
    Given I am a user with role '<ROLE>'
    When I want to change my password to 'NEW PASSWORD'
    Then my password is changed

    Examples:
      | ROLE    |
      | ADMIN   |
      | CLIENT  |
      | ADVISOR |

  Scenario Outline: A user should not be able to change password of another user
    Given I am a user with role '<MY ROLE>'
    And a user with role '<ROLE>'
    When I want to change password of this user to 'NEW PASSWORD'
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