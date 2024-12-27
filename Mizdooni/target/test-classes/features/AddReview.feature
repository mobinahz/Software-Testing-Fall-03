Feature: Add Reviews
  As a restaurant manager
  I want users to add reviews
  So that I can collect feedback for my restaurant

  Scenario: Add a single review to a restaurant
    Given A sample restaurant
    And A sample user
    When The user adds a review for the restaurant
    Then The restaurant should have one review
    And The review should belong to the user
    And The review's overall rating should be recorded

  Scenario: Add reviews from multiple users
    Given A sample restaurant
    And Multiple sample users
    When Each user adds a review for the restaurant
    And The average rating is calculated
    Then The restaurant should have multiple reviews
    And Each user's review should be included
    And The average overall rating should be calculated

  Scenario: Add a null review
    Given A sample restaurant
    And A sample user
    When The user tries to add a null review for the restaurant
    Then An error message should be shown indicating "Review cannot be null"
    And The restaurant should not have any reviews

  Scenario: Add multiple reviews by one user
    Given A sample restaurant
    And A sample user
    When The user adds multiple reviews for the restaurant
    And The average rating is calculated
    Then Only the most recent review should be recorded
    And The average overall rating should reflect the most recent review

  Scenario: Add multiple reviews by multiple users
    Given A sample restaurant
    And Multiple sample users
    When Each user adds multiple reviews for the restaurant
    And The average rating is calculated
    Then Only the most recent review by each user should be recorded
    And The average overall rating should reflect the most recent reviews
