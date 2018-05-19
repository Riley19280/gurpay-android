package com.rileystech.gurpay.network;

public abstract class APICallResponse {

    public void success(Object obj) {};

    public void error(String str) {};
}
