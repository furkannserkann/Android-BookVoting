package com.furkanyilmaz.items;

public class Book {

    public static String SISBN = "isbn";
    public static String Sname = "name";
    public static String SpublishDate = "publishDate";
    public static String SpublisherId = "publisherId";
    public static String SpageNumber = "pageNumber";
    public static String Sinformation = "information";
    public static String Sauthors = "authors";
    public static String Simage = "image";
    public static String SstarRate = "starRate";
    public static String ScommentCount = "commentCount";
    public static String ScategoryCount = "categoryCount";

    private String key;
    private String ISBN;
    private String name;
    private String publishDate;
    private String publisherId;
    private String pageNumber;
    private String information;
    private String authors;
    private String image;
    private String starRate;
    private String commentCount;
    private String categoryCount;

    public Book() {
    }

    public Book(String ISBN, String name, String publishDate, String publisherId, String pageNumber, String information, String authors, String image, String starRate, String commentCount, String categoryCount) {
        this.ISBN = ISBN;
        this.name = name;
        this.publishDate = publishDate;
        this.publisherId = publisherId;
        this.pageNumber = pageNumber;
        this.information = information;
        this.authors = authors;
        this.image = image;
        this.starRate = starRate;
        this.commentCount = commentCount;
        this.categoryCount = categoryCount;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStarRate() {
        return starRate;
    }

    public void setStarRate(String starRate) {
        this.starRate = starRate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(String categoryCount) {
        this.categoryCount = categoryCount;
    }
}
