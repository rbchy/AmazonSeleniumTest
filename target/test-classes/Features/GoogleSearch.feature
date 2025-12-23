Feature: Feature to test Google Search functionality

	Scenario: Validate Google search
		Given browser is open
		And the user is on google search page
		When the user enters a text in the search box
		And the user hits enter
		Then the user should be navigated to search result
