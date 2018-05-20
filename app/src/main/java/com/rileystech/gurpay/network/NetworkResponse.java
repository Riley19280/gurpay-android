package com.rileystech.gurpay.network;

import com.rileystech.gurpay.models.APIError;

import org.json.JSONObject;

public interface NetworkResponse {

    void success(JSONObject json);

    void error(APIError error);
}
