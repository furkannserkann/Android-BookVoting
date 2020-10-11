package com.furkanyilmaz.items;

public class User {
    public static String Sgender = "gender";
    public static String SnameSurname = "nameSurname";
    public static String Sphone = "phone";
    public static String Sauthorit = "authorit";
    public static String Semail = "email";
    public static String Simage = "image";
    public static String SregisterTime = "registerTime";

    private String key;
    private String gender;
    private String nameSurname;
    private String phone;
    private String authorit;
    private String email;
    private String image;
    private long registerTime;

    public User() { }

    public User(String gender, String nameSurname, String phone, String authorit, String email, String image, long registerTime) {
        this.gender = gender;
        this.nameSurname = nameSurname;
        this.phone = phone;
        this.authorit = authorit;
        this.email = email;
        this.image = image;
        this.registerTime = registerTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAuthorit() {
        return authorit;
    }

    public void setAuthorit(String authorit) {
        this.authorit = authorit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }
}
