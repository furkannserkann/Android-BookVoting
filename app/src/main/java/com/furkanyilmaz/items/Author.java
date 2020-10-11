package com.furkanyilmaz.items;

import java.util.List;

public class Author {

    public static String Sname = "name";
    public static String Simage = "image";
    public static String Sgender = "gender";

    private String key;
    private String name;
    private String image;
    private String gender;
    private List<Book> listBook;

    public Author() {
    }

    public Author(String key, String name, String image, String gender) {
        this.key = key;
        this.name = name;
        this.image = image;
        this.gender = gender;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getListBook() {
        return listBook;
    }

    public void setListBook(List<Book> listBook) {
        this.listBook = listBook;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
