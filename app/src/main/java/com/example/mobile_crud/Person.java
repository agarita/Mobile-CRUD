package com.example.mobile_crud;

import java.io.Serializable;

public class Person {
    private int id;
    private String name, last_name;

    public Person(int id, String name, String last_name) {
        this.id = id;
        this.name = name;
        this.last_name = last_name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLast_name(){
        return last_name;
    }
}
