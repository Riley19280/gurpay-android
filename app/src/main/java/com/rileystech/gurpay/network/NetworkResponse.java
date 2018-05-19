package com.rileystech.gurpay.network;

import org.json.JSONObject;

public interface NetworkResponse {

    void success(JSONObject json);

    void error(String error);
}
