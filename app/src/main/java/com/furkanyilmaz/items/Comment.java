package com.furkanyilmaz.items;

public class Comment {
    public final static String sStarNum = "starNum";
    public final static String sUserId = "userId";
    public final static String sBookId = "bookId";
    public final static String sPublishDate = "publishDate";
    public final static String sComment = "comment";

    private String key;
    private String starNum;
    private String userId;
    private String bookId;
    private long publishDate;
    private String comment;

    public Comment() {
    }

    public Comment(String starNum, String userId, String bookId, long publishDate, String comment) {
        this.starNum = starNum;
        this.userId = userId;
        this.bookId = bookId;
        this.publishDate = publishDate;
        this.comment = comment;
    }

    public String getStarNum() {
        return starNum;
    }

    public void setStarNum(String starNum) {
        this.starNum = starNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
