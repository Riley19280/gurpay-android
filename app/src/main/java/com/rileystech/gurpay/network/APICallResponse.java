package com.rileystech.gurpay.network;

import com.rileystech.gurpay.models.APIError;

public abstract class APICallResponse {

    public void success(Object obj) {};

    public void error(APIError err) {};
}
