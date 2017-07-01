Feature: creating users

  As administrator
  I want to create a user and to assign him a single role
  In order to add new users

  As administror
  I want to retrieve a user by its id
  In order to see his information

  Scenario Outline: An admin should be able to create any user
    Given I am a user with role 'ADMIN'
    When I want to create a user with role '<CREATING ROLE>'
    Then the user is created

    Examples:
      | CREATING ROLE |
      | ADMIN         |
      | CLIENT        |
      | ADVISOR       |

  Scenario Outline: A non admin should not be able to create a user
    Given I am a user with role '<MY ROLE>'
    When I want to create a user with role '<CREATING ROLE>'
    Then I am not authorized

    Examples:
      | MY ROLE | CREATING ROLE |
      | CLIENT  | ADMIN         |
      | CLIENT  | CLIENT        |
      | CLIENT  | ADVISOR       |
      | ADVISOR | ADMIN         |
      | ADVISOR | CLIENT        |
      | ADVISOR | ADVISOR       |