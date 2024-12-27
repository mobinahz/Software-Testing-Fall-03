Feature: Get Average Rating
  As a restaurant manager
  I want to calculate the average rating for my restaurant
  So that I can understand how my customers perceive the service

  Scenario: No reviews exist
    Given a restaurant named "Downtown Burger"
    When I calculate the average rating
    Then the average food rating should be 0
    And the average service rating should be 0
    And the average ambiance rating should be 0
    And the average overall rating should be 0

  Scenario: Single review exists
    Given a restaurant named "Downtown Burger"
    And a user named "JohnDoe" adds a review with food 4, service 5, ambiance 3, and overall 4
    When I calculate the average rating
    Then the average food rating should be 4
    And the average service rating should be 5
    And the average ambiance rating should be 3
    And the average overall rating should be 4

  Scenario: Multiple reviews exist
    Given a restaurant named "Downtown Burger"
    And a user named "JohnDoe" adds a review with food 4, service 5, ambiance 3, and overall 4
    And a user named "JaneDoe" adds a review with food 5, service 4, ambiance 5, and overall 4.5
    When I calculate the average rating
    Then the average food rating should be 4.5
    And the average service rating should be 4.5
    And the average ambiance rating should be 4
    And the average overall rating should be 4.25

  Scenario: Overwriting a user’s review
    Given a restaurant named "Downtown Burger"
    And a user named "JohnDoe" adds a review with food 4, service 5, ambiance 3, and overall 4
    And the same user named "JohnDoe" adds a review with food 2, service 3, ambiance 4, and overall 3
    When I calculate the average rating
    Then the average food rating should be 3
    And the average service rating should be 4
    And the average ambiance rating should be 3.5
    And the average overall rating should be 3.5
