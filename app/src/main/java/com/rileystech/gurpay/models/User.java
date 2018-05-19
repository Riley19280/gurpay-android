package com.rileystech.gurpay.models;

public class User {

    int id;
    String device_id;
    String name;
    String group_code;


    public User(int id, String device_id, String name, String group_code) {
        this.id = id;
        this.device_id = device_id;
        this.name = name;
        this.group_code = group_code;
    }

}
