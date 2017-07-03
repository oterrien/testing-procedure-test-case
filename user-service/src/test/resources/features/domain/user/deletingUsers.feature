Feature: deleting users

  As an admin
  I want to remove a user
  In order to clean up repository

  Scenario Outline: An admin should be able to delete any user
    Given I am a user with role 'ADMIN'
    And a user with role '<ROLE>'
    When I want to delete this user
    Then the user is deleted

    Examples:
      | ROLE    |
      | ADMIN   |
      | CLIENT  |
      | ADVISOR |

  Scenario Outline: A non admin should not be able to delete a user
    Given I am a user with role '<MY ROLE>'
    And a user with role '<ROLE>'
    When I want to delete this user
    Then I am not authorized

    Examples:
      | MY ROLE | ROLE    |
      | CLIENT  | ADMIN   |
      | CLIENT  | CLIENT  |
      | CLIENT  | ADVISOR |
      | ADVISOR | ADMIN   |
      | ADVISOR | CLIENT  |
      | ADVISOR | ADVISOR |
