@amazon @live
Feature: Amazon product search
  A shopper searches Amazon for a phone, narrows the price range and refines the results.
  (No login: the old "Given the user is logged in" step never logged in.)

  Scenario: Search for a phone and filter by price and attributes
    Given the user is on the Amazon home page
    When the user searches for "Google Pixel 9"
    And the user filters by price from 400 to 2500 dollars
    And the user selects these filters if available:
      | Unlocked |
      | Google   |
    Then the results should be for that product
    And the price filter should be applied
