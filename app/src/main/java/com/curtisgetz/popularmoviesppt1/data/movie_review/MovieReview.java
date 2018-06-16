package com.curtisgetz.popularmoviesppt1.data.movie_review;

public class MovieReview {


    private String mReviewId, mReviewAuthor, mReviewContent, mReviewUrl;
    private int mMovieId;


    public MovieReview() {
    }

    public MovieReview(String reviewId, String reviewAuthor, String reviewContent, String reviewUrl) {
        this.mReviewId = reviewId;
        this.mReviewAuthor = reviewAuthor;
        this.mReviewContent = reviewContent;
        this.mReviewUrl = reviewUrl;

    }


    public String getmReviewId() {
        return mReviewId;
    }

    public void setmReviewId(String mReviewId) {
        this.mReviewId = mReviewId;
    }

    public String getmReviewAuthor() {
        return mReviewAuthor;
    }

    public void setmReviewAuthor(String mReviewAuthor) {
        this.mReviewAuthor = mReviewAuthor;
    }

    public String getmReviewContent() {
        return mReviewContent;
    }

    public void setmReviewContent(String mReviewContent) {
        this.mReviewContent = mReviewContent;
    }

    public String getmReviewUrl() {
        return mReviewUrl;
    }

    public void setmReviewUrl(String mReviewUrl) {
        this.mReviewUrl = mReviewUrl;
    }
}
