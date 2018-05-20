package com.rileystech.gurpay.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APIError {

    List<String> errors = new ArrayList<>();


    public APIError(){

    }

    public APIError(String[] errors) {
        this.errors.addAll(Arrays.asList(errors));
    }

    public APIError(String message) {
       errors.add(message);
    }

    public void append(String error){
        errors.add(error);
    }

    @Override
    public String toString(){
        String str = "";

      for(String error : errors)
          str += error + "\n";

      return  str;
    }

}
