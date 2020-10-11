package com.furkanyilmaz.items;

import java.util.List;

public class Publisher {

    public static String Sname = "name";
    public static String Scount = "count";

    private String key;
    private String name;
    private String count;

    public Publisher() {
    }

    public Publisher(String key, String name) {
        this.key = key;
        this.name = name;
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
