@Infra
@ReadUser
Feature: reading users

  As a user
  I want to read only my own information
  In order to check them

  Background:
    Given following users has been created
      | login   | password        | role    |
      | admin   | adminPassword   | ADMIN   |
      | client  | clientPassword  | CLIENT  |
      | advisor | advisorPassword | ADVISOR |

  Scenario: A user should be able to read his own information
    Given I am the user 'client'
    When I want to read my information
    Then I should be authorized
    And my information should be available

  Scenario: An admin should be able to read any user's information
    Given I am the user 'admin'
    And another user is 'advisor'
    When I want to find this user
    Then I should be authorized
    And his information should be available

  Scenario: A non admin should not be able to retrieve another user
    Given I am the user 'client'
    And another user is 'advisor'
    When I want to find this user
    Then I should not be authorized

  Scenario: An admin should be able to retrieve all users
    Given I am the user 'admin'
    When I want to find all users
    Then I should be authorized
    And I should be able to retrieve following users:
      | login   |
      | admin   |
      | client  |
      | advisor |

  Scenario: A non admin should not be able to retrieve all users excepted himself
    Given I am the user 'client'
    When I want to find all users
    Then I should be authorized
    And I should be able to retrieve only myself