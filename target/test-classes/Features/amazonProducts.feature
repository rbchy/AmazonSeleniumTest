Feature: Amazon product search functionality

  Scenario: Amazon product search
    Given the user is logged in in amazon account
    When the user searches for the product name in the search bar
    And the user clicks the search button
    And the user filters by price range
    And the user selects filters for condition and carrier
    Then the product should be visible in the search result