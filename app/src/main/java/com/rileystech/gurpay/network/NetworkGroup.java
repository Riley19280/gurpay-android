package com.rileystech.gurpay.network;


import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Group;
import com.rileystech.gurpay.models.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkGroup {



    public void JoinGroup(Context ctx, String code, String groupName, String userName,final APICallResponse resp){

        Map<String, String> params = new HashMap<>();
        params.put("group_name", groupName);
        params.put("user_name", userName);
        params.put("group_code", code);
        params.put("platform", "android");

        NetworkBase.executeRequest(ctx,"group/join", Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(JSONObject json) {
                try {
                    Group group = new Group(json.getString("name"), json.getString("code"));
                    //TODO:save this group to device storage and then move headers out
                    resp.success(group);
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response"));
                }

            }

            @Override
            public void error(APIError error) {
                resp.error(error);
            }
        });
    }


}
