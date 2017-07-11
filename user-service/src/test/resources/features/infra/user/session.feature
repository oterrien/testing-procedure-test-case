@Infra
@UserSession
Feature: session users

  As a user
  I want to be authentified to service
  In order to be able to use its functionalities

  Background:
    Given following users has been created
      | login | password      | role  |
      | admin | adminPassword | ADMIN |

  Scenario: a user should be authorized when his credentials are good
    Given I am the user 'admin'
    And my credentials are: admin/adminPassword
    When I want to read my information with my credentials
    Then I should be authorized

  Scenario: a user should not be authorized when his credentials are bad
    Given I am the user 'admin'
    And my credentials are: admin/badPassword
    When I want to read my information with my credentials
    Then I should not be authorized

  Scenario: a user who has been firstly authenticated can use the session-token to connect
    Given I am the user 'admin'
    And my credentials are: admin/adminPassword
    When I want to read my information with my credentials
    And I want to read my information with my session token
    Then I should be authorized