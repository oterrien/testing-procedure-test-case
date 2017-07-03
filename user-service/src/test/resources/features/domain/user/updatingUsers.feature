Feature: updating users

  As an admin
  I want to update any information of a user
  In order to let the repository up to date

  Scenario Outline: An admin should be able to update any user
    Given I am a user with role 'ADMIN'
    And a user with role '<ROLE>'
    When I want to set password = 'NEW PASSWORD' for this user
    Then the password or this user is updated

    Examples:
      | ROLE    |
      | ADMIN   |
      | CLIENT  |
      | ADVISOR |

  Scenario Outline: A non admin should not be able to update a user
    Given I am a user with role '<MY ROLE>'
    And a user with role '<ROLE>'
    When I want to set password = 'NEW PASSWORD' for this user
    Then I am not authorized

    Examples:
      | MY ROLE | ROLE    |
      | CLIENT  | ADMIN   |
      | CLIENT  | CLIENT  |
      | CLIENT  | ADVISOR |
      | ADVISOR | ADMIN   |
      | ADVISOR | CLIENT  |
      | ADVISOR | ADVISOR |