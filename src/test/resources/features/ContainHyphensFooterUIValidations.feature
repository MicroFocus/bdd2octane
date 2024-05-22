#Auto generated Octane revision tag
@BSPID174090REV0.18.0
Feature: MyAccount - Footer

  @TSCID523715 @TemenosReactTeam
  Scenario: Verify Footer - Our Fees Hyperlink
    Given User logs into My Account with a policy
    When User clicks on "Our Fees" link in My Account Footer
    Then User should be navigated to the respective footer page
  @TSCID523721 @TemenosReactTeam
  Scenario: Verify Footer - Important Information About Us Hyperlink
    Given User login into My Account with Multi Product Policies
      |Bike	|	1 |
      |Van	|	1 |
    When User clicks on "Important Information About Us" link in My Account Footer
    Then User should be navigated to the respective footer page