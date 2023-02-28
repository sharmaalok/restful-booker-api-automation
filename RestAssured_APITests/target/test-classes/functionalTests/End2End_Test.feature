Feature: Automated E2E tests
  Description: The purpose of this feature is to test E2E integration happy flow test

  Background: User generates Token for Authorisation
    Given  I am an authorized user

  Scenario: Authorized user is able to get booking id's
   Given A list of bookings are available without filters
    When booking details are available when specific bookingId is entered
    When A list of bookings are available by Name filters
    When A list of bookings are available By Date filters
    Given A new booking is created
   When A current booking is updated completely
  When A current booking is updated with a partial payload
  Given A current booking is deleted

#
#
# When I add a book to my reading list
#    Then The book is added
#    When I remove a book from my reading list
#    Then The book is removed


  #    When I add a book to my reading list
#    Then The book is added
#    When I remove a book from my reading list
#    Then The book is removed