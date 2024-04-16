
@GET_PAYMENT_METHOD_STATUS_V1

Feature: customer able to check One Time Payment and Autopay Enrollment Exist for a Payment Method Id

	Background: REST API Payload
		Given URL :"v1/payments/method/{paymentMethodId}/status"
		And header 'X-SYF-ClientId' : 'generic'
		And header 'X-SYF-Request-TrackingId' : "a unique identifier"

	@SANITY @SUCCESS

	Scenario Outline: one time payment exist and autopay enrollment exist-scn_id:s1
	scenario outline
	description
		Given an account of type '<accountType>' having account number as header 'X-SYF-Account-Number' : "accountNumber" and client '<clientName>'
		And header 'X-SYF-ChannelId' : '<channel>'
		And payment method id exist
		And path param as 'paymentMethodId' : 'paymentMethodId'
		And payment method id is associated with account number
		And one time payment exist with this payment method id
		And autopay enrollment exist with this payment method id
		When method GET
		Then status code should be '200'
		Then responsebody
                  """
                  {
                      "onetimePaymentsExist": true,
                      "recurringPaymentsExist": true
                  }
                  """
		Examples:
			|accountType  |clientName    |channel           |
			|   plcc      |amazon        |sypi            |
			|   pscc      |carecredit    |digital-servicing |