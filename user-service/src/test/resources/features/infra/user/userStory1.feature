Feature: creating users

  As an admin
  I want to create users
  In order to retrieve them in the future

  Scenario Outline: An admin should be able to create any user
    Given I am the 'admin' user
    When I want to create a user with role '<CREATING ROLE>'
    Then the user is created

    Examples:
      | CREATING ROLE |
      | ADMIN         |
      | CLIENT        |
      | ADVISOR       |