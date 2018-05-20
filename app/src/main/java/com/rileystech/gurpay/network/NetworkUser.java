package com.rileystech.gurpay.network;

import android.content.Context;

import com.android.volley.Request;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkUser {

    public void getUser(Context ctx, String ident, final APICallResponse resp){
        Map<String, String> params = new HashMap<>();
        params.put("id", ident);

        NetworkBase.executeRequest(ctx, "user/1",  Request.Method.GET, params,NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(JSONObject json) {

                try {
                    User u = new User(json.getInt("id"),json.getString("name"));
                    resp.success(u);
                }
                catch (Exception e) {
                    error(new APIError("An error occurred while parsing the json."));
                }

            }

            @Override
            public void error(APIError error) {
                resp.error(error);
            }
        });

    }

}
