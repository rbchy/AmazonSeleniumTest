Feature: Feature to test Gmail functionalitiesty

	Scenario: Login into Gmail
		Given browser is open
		When the user navigates to Gmail login page
		And the user logs in with valid credentials
		Then the user should be on the Inbox page
		And the Compose button should be visible
