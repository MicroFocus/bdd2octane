#Auto generated Octane revision tag
@BSPID182030REV0.12.0
Feature: MyAccount - Footer - GoGreen Overlay Ui Validations

  @TSCID545389 @TemenosReactTeam
  Scenario: Verify the Help us go green modal overlay for postal document delivery in My Account
    Given User logs into My Account with Postal document delivery method policy
    When User is on Dashboard screen for Go Green Modal
    Then User should be presented with Help us go green modal overlay