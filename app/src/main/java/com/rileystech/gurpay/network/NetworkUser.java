package com.rileystech.gurpay.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rileystech.gurpay.models.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkUser {

    public void getUser(Context ctx, String ident, final APICallResponse resp){
        Map<String, String> params = new HashMap<>();
        params.put("id", ident);

        NetworkBase.wrapper(ctx, "", params, Request.Method.GET, new NetworkResponse() {
            @Override
            public void success(JSONObject json) {

                User u = new User();
                resp.success(u);
            }

            @Override
            public void error(String error) {
                resp.error(error);
            }
        });

    }

}
