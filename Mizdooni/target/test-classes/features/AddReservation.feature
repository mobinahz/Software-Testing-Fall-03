Feature: Add Reservation
  As a user
  I want to add a reservation
  So that I can book a table at a restaurant

  Scenario: Add a reservation successfully
    Given A sample user with a client role
    And A sample restaurant
    When The user adds a reservation for the restaurant at a specific date and time
    Then The reservation should be added to the user's reservation list
    And The reservation should have a unique reservation number

  Scenario: Add a reservation for a past date
    Given A sample user with a client role
    And A sample restaurant
    When The user tries to add a reservation for a past date
    Then An error message should be shown indicating "Cannot reserve for a past date"

  Scenario: Add a reservation outside operational hours
    Given A sample user with a client role
    And A sample restaurant
    When The user tries to add a reservation outside the restaurant's operational hours
    Then An error message should be shown indicating "Restaurant is closed at this time"

  Scenario: Add a reservation with missing details
    Given A sample user with a client role
    And A sample restaurant
    When The user tries to add a reservation with incomplete details
    Then An error message should be shown indicating "Reservation details are incomplete"

  Scenario: Add a reservation for a non-existent restaurant
    Given A sample user with a client role
    When The user tries to add a reservation for a non-existent restaurant
    Then An error message should be shown indicating "Restaurant does not exist"


  Scenario: Add reservations for multiple users simultaneously
    Given A sample restaurant
    When Each user adds a reservation for the restaurant at the same time
    Then Each reservation should be added to the respective user's reservation list
    And Each reservation should have a unique reservation number
