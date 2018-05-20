package com.rileystech.gurpay.network;

import android.content.Context;

import com.android.volley.Request;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Dashboard;
import com.rileystech.gurpay.models.User;

import org.json.JSONObject;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class NetworkUser {

    private HashMap<String, User> userCache = new HashMap<>();
    public void getUser(Context ctx, final String ident, final APICallResponse resp){

        User u = userCache.get(ident);
        if(u != null)
            resp.success(u);

        HashMap<String, String> params = new HashMap<>();
        params.put("id", ident);

        NetworkBase.executeRequest(ctx, "user"+ident,  Request.Method.GET, params,NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    User u = new User(jo.getInt("id"),jo.getString("name"));
                    userCache.put(ident,u);
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

    public void UpdateUser(Context ctx, String name, final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);

        NetworkBase.executeRequest(ctx,"user", Request.Method.PUT,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    User u = new User(jo.getInt("id"),jo.getString("name"));

                    resp.success(u);
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void GetDashboard(Context ctx,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"dashboard", Request.Method.GET,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONObject jo = new JSONObject(json);

                    Dashboard d = new Dashboard(
                        jo.getInt("myBillCount"),
                        jo.getInt("myUnpaidBillCount"),
                        jo.getInt("myBillsPayerCount"),
                        jo.getInt("myBillsPayersPaidTotal"),
                        jo.getDouble("myBillsPayerPaidAmount"),
                        jo.getDouble("myBillsPayerPaidToDate"),
                        jo.getDouble("payTotal"),
                        jo.getDouble("payTotalToDate"),
                        jo.getInt("payTotalCount"),
                        jo.getInt("payTotalCountToDate"),
                        Date.valueOf(jo.getString("nextDueDate"))//TODO: this date conversion probably doesn't work
                    );

                    resp.success(d);
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void RegisterPush(Context ctx, String push_id,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();
        params.put("push_id", push_id);

        NetworkBase.executeRequest(ctx,"user/push", Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                resp.success(null);
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void UnregisterPush(Context ctx, final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"user/push", Request.Method.DELETE,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                    resp.success(null);
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

}
