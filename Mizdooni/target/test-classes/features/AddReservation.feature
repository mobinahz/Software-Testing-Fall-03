Feature: Add Reservation
  As a user
  I want to add a reservation
  So that I can book a restaurant for a specific time

  Scenario: Add a reservation to Downtown Burger
    Given a user with username "JohnnyDepp" and role "client" exists
    When the user adds a reservation for "Downtown Burger" on "2024-12-28T19:00"
    Then the reservation is added to the user's reservation list
    And the reservation number is "0"

  Scenario: Add a reservation to Perperook
    Given a user with username "JohnnyDepp" and role "client" exists
    When the user adds a reservation for "Perperook" on "2024-12-29T20:00"
    Then the reservation is added to the user's reservation list
    And the reservation number is "0"

  Scenario: Add a reservation for a past date at Downtown Burger
    Given a user with username "OldTimer" and role "client" exists
    When the user tries to add a reservation for "Downtown Burger" on "2020-01-01T19:00"
    Then an error message is shown: "Cannot reserve for a past date"

  Scenario: Add a reservation outside operational hours for Perperook
    Given a user with username "NightOwl" and role "client" exists
    When the user tries to add a reservation for "Perperook" on "2024-12-28T23:30"
    Then an error message is shown: "Restaurant is closed at this time"

  Scenario: Add multiple reservations for Downtown Burger
    Given a user with username "JohnnyDepp" and role "client" exists
    When the user adds a reservation for "Downtown Burger" on "2024-12-28T19:00"
    And the user adds another reservation for "Downtown Burger" on "2024-12-29T18:00"
    Then the reservations are added to the user's reservation list
    And the reservation numbers are "0" and "1"

  Scenario: Add a reservation with an invalid restaurant name
    Given a user with username "JohnnyDepp" and role "client" exists
    When the user tries to add a reservation for "Invalid Restaurant" on "2024-12-28T19:00"
    Then an error message is shown: "Restaurant does not exist"

  Scenario: Add a reservation for Perperook with missing details
    Given a user with username "ErrorUser" and role "client" exists
    When the user tries to add a reservation for "Perperook" with no date
    Then an error message is shown: "Reservation date is required"

  Scenario: Multiple users add reservations simultaneously
    Given a user with username "User1" and role "client" exists
    And a user with username "User2" and role "client" exists
    When User1 adds a reservation for "Downtown Burger" on "2024-12-28T19:00"
    And User2 adds a reservation for "Perperook" on "2024-12-28T20:00"
    Then both reservations are added to their respective reservation lists
    And the reservation numbers for User1 and User2 are "0" and "0"
