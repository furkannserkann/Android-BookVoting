package com.furkanyilmaz.items;

import java.util.List;

public class Category {

    public static String Sname = "name";
    public static String Scount = "count";

    private String key;
    private String name;
    private String count;

    public Category() {
    }

    public Category(String key, String name, String count) {
        this.key = key;
        this.name = name;
        this.count = count;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
