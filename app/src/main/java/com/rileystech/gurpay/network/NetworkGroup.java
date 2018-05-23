package com.rileystech.gurpay.network;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Group;
import com.rileystech.gurpay.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkGroup {



    public void JoinGroup(Context ctx, String code, String groupName, String userName,final APICallResponse resp){

        HashMap<String, String> params = new HashMap<>();

        params.put("user_name", userName);
        if(groupName != null)
            params.put("group_name", groupName);
        if(code != null)
            params.put("group_code", code);
        params.put("platform", "android");

        final SharedPreferences prefs = ctx.getSharedPreferences(
                "com.rileystech.gurpay", Context.MODE_PRIVATE);

        NetworkBase.executeRequest(ctx,"group/join", Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    Group group = new Group(jo.getString("name"), jo.getString("code"));

                    if(prefs.edit().putString("group_code",jo.getString("code")).commit())
                        if (prefs.edit().putString("group_name", jo.getString("name")).commit()) {
                           // prefs.edit().putString("user_id",).apply();
                            resp.success(group);
                        } else
                            resp.error(new APIError("Error saving group info."));
                    else
                        resp.error(new APIError("Error saving group info."));
                    
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

    public void LeaveGroup(Context ctx, final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"group/leave", Request.Method.DELETE,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
               resp.success(null);
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void EditGoup(Context ctx, Group group,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();
        params.put("name", group.name);

        NetworkBase.executeRequest(ctx,"group", Request.Method.PUT,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
              resp.success(null);
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void GetGroup(Context ctx,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"EXROUTE", Request.Method.GET,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    Group g = new Group(jo.getString("name"),jo.getString("code"));//TODO:Write this to a device file

                    resp.success(g);
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void GetGroupMembers(Context ctx, final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"group/members", Request.Method.GET,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONArray arr = new JSONArray(json);

                    List<User> users = new ArrayList<>();

                    for(int i = 0; i < arr.length(); i++ ) {
                        JSONObject user = arr.getJSONObject(i);
                        User u = new User(user.getInt("id"),user.getString("name"));
                        users.add(u);
                    }

                    resp.success(users);
                }
                catch (Exception e) {
                    Log.e("TEST","",e);
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

}
