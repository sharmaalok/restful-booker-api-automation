Feature: Automated E2E tests
  Description: The purpose of this feature is to test E2E integration happy flow test

  Background: User perform healthcheck and generates Token for Authorisation
    Given API is up and running
    When User access the CreateToken endpoint
    Then A new Authorisation token is created

  Scenario: Authorized user is able to get booking id
    Given A list of bookings are fetched without filters via GetBookingIds API
    When A specific bookingId from the list is entered via GetBooking API
    Then Booking details are retrieved for the specified booking id
    When Booking is fetched with Name filters via GetBooking API
    Then Name filtered booking details are retrieved correctly
    When Booking is fetched with Date filters via GetBooking API
    Then Date filtered booking details are retrieved correctly

  Scenario: Authorized user is able to create new booking and update the existing booking
    Given A new booking is created via CreateBooking API
    When new booking with new bookingid is created
    Then Booking created should be available for update via UpdateBooking API
    And Booking should be updated successfully
    When Booking is updated with a partial payload via PartialUpdateBooking
    Then partial update should be successfull

  Scenario: Authorized user is able to delete the existing booking
    Given Previously created booking is deleted via DeleteBooking API
    When Booking details for deleted bookingid are fetched again via GetBooking API
    Then No Details should be retrieved

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