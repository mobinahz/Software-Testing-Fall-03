Feature: Manage Reviews and Get Average Rating
  As a restaurant manager
  I want to add reviews and calculate the average rating
  So that I can understand how customers perceive the service

  Scenario: No reviews exist
    Given A restaurant without any reviews
    When The average rating is calculated
    Then All average ratings (food, service, ambiance, and overall) should be 0.0


  Scenario: Adding a single review
    Given A restaurant
    When A review is added
    And The average rating is calculated
    Then All average ratings (food, service, ambiance, and overall) should reflect the review

  Scenario: Adding multiple reviews
    Given A restaurant
    When Multiple reviews are added
    And The average rating is calculated
    Then All average ratings (food, service, ambiance, and overall) should reflect the combined reviews
