package com.rileystech.gurpay.models;

import java.io.Serializable;

public class User implements Serializable{

    public int id;
    public String device_id;
    public String name;
    public String group_code;


    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(int id, String name, String device_id,  String group_code) {
        this.id = id;
        this.name = name;
        this.device_id = device_id;
        this.group_code = group_code;
    }

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return user.id == this.id;
    }

}
