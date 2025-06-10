Feature: Refunds

  Rule: Refunds are only available for unused tickets
    Scenario: Refund eligibility based on ticket usage
      Given the ticket has been "used"
      And the ticket was purchased 10 days ago
      When the customer requests a refund
      Then the refund should be "denied"

  Rule: Refunds must be requested within 30 days
    Scenario Outline: Refund eligibility based on purchase date
      Given the ticket has not been used
      And the ticket was purchased <days> days ago
      When the customer requests a refund
      Then the refund should be "<expected_result>"

      Examples:
        | days | expected_result |
        | 25   | approved        |
        | 40   | denied          |

Rule: Refunds are not allowed for promotional tickets

    Scenario Outline: Refund eligibility for promotional tickets
      Given the ticket is a "<promo_status>" promotional ticket
      And the ticket has not been used
      And the ticket was purchased 15 days ago
      When the customer requests a refund
      Then the refund should be "<expected_result>"

      Examples: Promotion
        | promo_status | expected_result |
        | regular      | approved        |
        | promo        | denied          |
        | promo        | approved        |