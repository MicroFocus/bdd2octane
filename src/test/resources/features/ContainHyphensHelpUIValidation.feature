#Auto generated Octane revision tag
@BSPID183048REV0.5.0
Feature: MyAccount - Footer - Help UI Validation

  @TSCID564947 @TemenosReactTeam
  Scenario: Verify Help page on My Account
    Given User logs into My Account with a policy
    When User clicks on Help button
    Then User should be directed to the Hastings Direct Help page