@Infra
Feature: deleting users

  As an admin
  I want to remove a user
  In order to clean up repository

  Background:
    Given following users has been created
      | login   | password        | role    |
      | admin   | adminPassword   | ADMIN   |
      | client  | clientPassword  | CLIENT  |
      | advisor | advisorPassword | ADVISOR |

  Scenario: An admin should be able to delete any user
    Given I am the user 'admin'
    And another user is 'client'
    When I want to delete this user
    Then this user should be deleted

  Scenario: A non admin should not be able to delete a user
    Given I am the user 'client'
    And another user is 'advisor'
    When I want to delete this user
    Then I should not be authorized