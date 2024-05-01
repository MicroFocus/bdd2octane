@billing @bicker @annoy
Feature: feature - with hyphens - group one
  A description with
  multiple
  lines

  Background:
    Given a global administrator named "Greg"
    * a blog named "Greg's anti-tax rants"
    * a customer named "Wilson"

  @Quick @full @annoy
  Scenario: Some determinable business situation (hyphenated feature name)
    Given the following people exist:
      | name  | email           | phone |
      | Aila  | aila@email.com  | 123   |
      | Joe   | joe@email.com   | 234   |
    And some precondition 1
    When some action by the actor
    And some other action
    Then some testable outcome is achieved
    And something else we can check happens too

  @Quick @nightly
  Scenario: Some another scenario 2 (hyphenated feature name)
  scenario 2 description
    Given some precondition
    And some other precondition with doc string
		"""
		this is comment
		"""
    When some action by the actor
    And yet another action
    Then some testable outcome is achieved
    * something else we can check happens too
    But I don't see something else
