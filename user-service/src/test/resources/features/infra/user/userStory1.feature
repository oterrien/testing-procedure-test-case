Feature: creating users

  As administrator
  I want to create a user and to assign him a single role
  In order to add new users

  Scenario Outline: An admin should be able to create any user
    Given I am a user with role 'ADMIN'
    When I want to create a user with role '<CREATING ROLE>'
    Then the user is created

    Examples:
      | CREATING ROLE |
      | ADMIN         |
      | CLIENT        |
      | ADVISOR       |