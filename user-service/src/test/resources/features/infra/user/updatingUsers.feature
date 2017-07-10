@Infra
@UpdateUser
Feature: updating users

  As an admin
  I want to update any information of a user
  In order to let the repository up to date

  Background:
    Given following users has been created
      | login   | password        | role    |
      | admin   | adminPassword   | ADMIN   |
      | client  | clientPassword  | CLIENT  |
      | advisor | advisorPassword | ADVISOR |

  Scenario: An admin should be able to update any user
    Given I am the user 'admin'
    And another user is 'client'
    When I want to set password = 'NEW PASSWORD' for this user
    Then the password or this user should be updated

  Scenario: A non admin should not be able to update a user
    Given I am the user 'advisor'
    And another user is 'admin'
    When I want to set password = 'NEW PASSWORD' for this user
    Then I should not be authorized