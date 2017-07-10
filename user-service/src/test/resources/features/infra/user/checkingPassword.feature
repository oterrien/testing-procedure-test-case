@Infra
@CheckPassword
Feature: checking password

  As a user
  I want to check my password is correct
  In order to be able to change my password or to read my information

  Background:
    Given following users has been created
      | login   | password        | role    |
      | admin   | adminPassword   | ADMIN   |
      | client  | clientPassword  | CLIENT  |
      | advisor | advisorPassword | ADVISOR |

  Scenario: A user should be able to check his own password is correct
    Given I am the user 'advisor'
    When I want to check my password is 'advisorPassword'
    Then this password should be correct

  Scenario: A user should be able to check his own password is incorrect
    Given I am the user 'client'
    When I want to check my password is 'bad password'
    Then this password should not be correct

  Scenario: A user should not be able to check password of another user
    Given I am the user 'client'
    And another user is 'advisor'
    When I want to check his password is 'advisorPassword'
    Then I should not be authorized