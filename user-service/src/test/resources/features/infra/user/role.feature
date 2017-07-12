@Infra
@UserRole
@Ignore
Feature: adding and removing role

  As an admin
  I want to add and remove many roles to users
  In order to let them changing their role

  Background:
    Given following users has been created
      | login  | password       | role  |
      | admin  | adminPassword  | ADMIN |
      | client | clientPassword | ROLE  |

  Scenario: an admin should be able to add a role to another user
    Given I am the user 'admin'
    And another user is 'client'
    When I want to add role 'ADVISOR' to this user
    Then the user should have following roles:
      | roles   |
      | CLIENT  |
      | ADVISOR |

  Scenario: a non admin should not be authorized to add a role to another user
    Given I am the user 'client'
    And another user is 'admin'
    When I want to add role 'ADVISOR' to this user
    Then I should not be authorized

  Scenario: a non admin should not be authorized to add a role to him
    Given I am the user 'client'
    When I want to add role 'ADVISOR' to me
    Then I should not be authorized

  Scenario: an admin should be able to remove a role to another user
    Given I am the user 'admin'
    And another user is 'client'
    When I want to add role 'ADVISOR' to this user
    Then the user should have following roles:
      | roles   |
      | CLIENT  |
      | ADVISOR |