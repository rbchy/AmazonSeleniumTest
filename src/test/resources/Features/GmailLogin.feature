@gmail @manual
Feature: Gmail login
  Needs a real Google account in GMAIL_USER / GMAIL_PASSWORD and a normal (non-CI) network.
  Google blocks sign-in from automated browsers, so this scenario is excluded by default.

  Scenario: Login into Gmail
    Given browser is open
    When the user navigates to Gmail login page
    And the user logs in with valid credentials
    Then the user should be on the Inbox page
    And the Compose button should be visible
