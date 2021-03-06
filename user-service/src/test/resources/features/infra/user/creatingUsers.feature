@Infra
@CreateUser
Feature: creating users

  As an admin
  I want to create users
  In order to retrieve them in the future

  Background:
    Given following users has been created
      | login   | password        | role    |
      | admin   | adminPassword   | ADMIN   |
      | client  | clientPassword  | CLIENT  |
      | advisor | advisorPassword | ADVISOR |

  Scenario: An admin should be able to create any user
    Given I am the user 'admin'
    When I want to create a user with role 'CLIENT'
    Then this user should be created

  Scenario: A non admin should not be able to create a user
    Given I am the user 'client'
    When I want to create a user with role 'ADVISOR'
    Then I should not be authorized