@Infra
@ResetPassword
Feature: changing password

  As a client or an advisor
  I want to update my password
  In order to ensure my connections

  Background:
    Given following users has been created
      | login   | password        | role    |
      | admin   | adminPassword   | ADMIN   |
      | client  | clientPassword  | CLIENT  |
      | advisor | advisorPassword | ADVISOR |

  Scenario: A user should be able to change his own password
    Given I am the user 'client'
    When I want to change my password to 'NEW PASSWORD'
    Then my password should be updated

  Scenario: A user should not be able to change password of another user
    Given I am the user 'client'
    And another user is 'advisor'
    When I want to change his password to 'NEW PASSWORD'
    Then I should not be authorized