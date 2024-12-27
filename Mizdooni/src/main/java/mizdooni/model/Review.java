package mizdooni.model;

import lombok.Getter;

import java.time.LocalDateTime;

public class Review {
    private Rating rating;
    @Getter
    private String comment;
    private LocalDateTime datetime;
    private User user;

    public Review(User user, Rating rating, String comment, LocalDateTime datetime) {
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.datetime = datetime;
    }

    public Rating getRating() {
        return rating;
    }

    public int getStarCount() {
        return rating.getStarCount();
    }

    public User getUser() {
        return user;
    }


}
