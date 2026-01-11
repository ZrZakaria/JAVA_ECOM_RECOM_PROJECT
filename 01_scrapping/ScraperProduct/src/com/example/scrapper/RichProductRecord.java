package com.example.scrapper;

public class RichProductRecord {
    private final String title;
    private final String price;
    private final String link;
    private final String image;
    private final String description;
    private final String reviewAuthor;
    private final String reviewRating;
    private final String reviewTitle;
    private final String reviewBody;
    private final String reviewDate;

    public RichProductRecord(
            String title,
            String price,
            String link,
            String image,
            String description,
            String reviewAuthor,
            String reviewRating,
            String reviewTitle,
            String reviewBody,
            String reviewDate) {
        this.title = title == null ? "" : title;
        this.price = price == null ? "" : price;
        this.link = link == null ? "" : link;
        this.image = image == null ? "" : image;
        this.description = description == null ? "" : description;
        this.reviewAuthor = reviewAuthor == null ? "" : reviewAuthor;
        this.reviewRating = reviewRating == null ? "" : reviewRating;
        this.reviewTitle = reviewTitle == null ? "" : reviewTitle;
        this.reviewBody = reviewBody == null ? "" : reviewBody;
        this.reviewDate = reviewDate == null ? "" : reviewDate;
    }

    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getLink() { return link; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public String getReviewAuthor() { return reviewAuthor; }
    public String getReviewRating() { return reviewRating; }
    public String getReviewTitle() { return reviewTitle; }
    public String getReviewBody() { return reviewBody; }
    public String getReviewDate() { return reviewDate; }
}
