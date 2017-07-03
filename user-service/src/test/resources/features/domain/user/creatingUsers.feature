Feature: creating users

  As an admin
  I want to create users
  In order to retrieve them in the future

  Scenario Outline: An admin should be able to create any user
    Given I am a user with role 'ADMIN'
    When I want to create a user with role '<ROLE>'
    Then the user is created

    Examples:
      | ROLE    |
      | ADMIN   |
      | CLIENT  |
      | ADVISOR |

  Scenario Outline: A non admin should not be able to create a user
    Given I am a user with role '<MY ROLE>'
    When I want to create a user with role '<ROLE>'
    Then I am not authorized

    Examples:
      | MY ROLE | ROLE    |
      | CLIENT  | ADMIN   |
      | CLIENT  | CLIENT  |
      | CLIENT  | ADVISOR |
      | ADVISOR | ADMIN   |
      | ADVISOR | CLIENT  |
      | ADVISOR | ADVISOR |