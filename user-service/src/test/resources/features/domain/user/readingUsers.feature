Feature: reading users

  As a user
  I want to read only my own information
  In order to check them

  Scenario Outline: A user should be able to read his own information
    Given I am a user with role '<ROLE>'
    When I want to read my information
    Then my information are available

    Examples:
      | ROLE    |
      | ADMIN   |
      | CLIENT  |
      | ADVISOR |

  Scenario Outline: A non admin should not be able to retrieve another user
    Given I am a user with role '<MY ROLE>'
    And a user with role '<ROLE>'
    When I want to find this user
    Then I am not authorized

    Examples:
      | MY ROLE | ROLE    |
      | CLIENT  | ADMIN   |
      | CLIENT  | CLIENT  |
      | CLIENT  | ADVISOR |
      | ADVISOR | ADMIN   |
      | ADVISOR | CLIENT  |
      | ADVISOR | ADVISOR |