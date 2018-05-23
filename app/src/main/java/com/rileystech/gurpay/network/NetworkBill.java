package com.rileystech.gurpay.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Bill;
import com.rileystech.gurpay.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NetworkBill {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void GetBills(Context ctx, boolean archive, final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        String route;
        if(archive)
            route = "group/archive";
        else
            route = "group/bills";

        NetworkBase.executeRequest(ctx,route, Request.Method.GET,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONArray jo = new JSONArray(json);
                    List<Bill> bills = new ArrayList<>();



            // Bill(int id, int owner_id, String name, Date dateAssigned, Date datePaid, Date dateDue, boolean isArchive){
                    for(int i = 0; i < jo.length(); i++ ) {
                        JSONObject b = jo.getJSONObject(i);
                        Bill bill = new Bill(
                                b.getInt("id"),
                                b.getInt("owner_id"),
                                b.getString("name"),
                                b.getDouble("total"),
                                formatter.parse(b.getString("date_assigned")),
                                b.getString("date_paid") != "null" ? formatter.parse(b.getString("date_paid")) : null,
                                formatter.parse(b.getString("date_due")),
                                b.getInt("archived") == 1 );

                        bill.split_cost = b.getDouble("split_cost");
                        bill.subtotal = b.getDouble("subtotal");

                        JSONArray payers = b.getJSONArray("payers");
                        for( int j = 0; j < payers.length(); j++) {
                            JSONObject u = payers.getJSONObject(j);
                            User user = new User(u.getInt("id"),u.getString("name"));
                            bill.payers.put(user,u.getJSONObject("pivot").getInt("has_paid")==1);
                        }

                        bills.add(bill);
                    }

                    resp.success(bills);
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

    public void CreateBill(final Context ctx,final Bill bill,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();
        params.put("name", bill.name);
        params.put("total", bill.total.toString());
        params.put("date_assigned", Util.dateForAPI(bill.date_assigned));
        params.put("date_due", Util.dateForAPI(bill.date_due));
        params.put("date_paid", null);

        NetworkBase.executeRequest(ctx,"bill", Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                     JSONObject b = new JSONObject(json);
                    Bill bill2 = new Bill(
                            b.getInt("id"),
                            b.getInt("owner_id"),
                            b.getString("name"),
                            b.getDouble("total"),
                            Util.parseServerDate(b.getString("date_assigned")),
                            Util.parseServerDate(b.getString("date_paid")),
                            Util.parseServerDate(b.getString("date_due")),
                            b.getInt("archived") == 1);
                    //bill.subtotal = b.getDouble("subtotal");
                    //bill.split_cost = b.getDouble("split_cost");

                    List<User> users = new ArrayList<>();

                    for (HashMap.Entry<User, Boolean> user : bill.payers.entrySet()) {
                       users.add(user.getKey());
                    }
                    AddPayers(ctx, bill2, users, new APICallResponse() {
                        @Override
                        public void success(Object obj) {

                        }

                        @Override
                        public void error(APIError err) {
                            super.error(err);
                        }
                    });


                    resp.success(null);
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void GetBillDetail(Context ctx, int bill_id,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();
        params.put("bill_id",Integer.toString(bill_id));

        NetworkBase.executeRequest(ctx,"bill/"+bill_id, Request.Method.GET,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                resp.success(null);

            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void UpdateBill(Context ctx,Bill bill,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();
        params.put("name", bill.name);
        params.put("total", bill.total.toString());
        params.put("date_assigned", Util.dateForAPI(bill.date_assigned));
        params.put("date_due", Util.dateForAPI(bill.date_due));
        params.put("date_paid", Util.dateForAPI(bill.date_paid));

        NetworkBase.executeRequest(ctx,"bill/"+bill.id, Request.Method.PUT,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONObject b = new JSONObject(json);
                    Bill bill = new Bill(
                            b.getInt("id"),
                            b.getInt("owner_id"),
                            b.getString("name"),
                            b.getDouble("total"),
                            Date.valueOf(b.getString("date_assigned")),
                            Date.valueOf(b.getString("date_paid")),
                            Date.valueOf(b.getString("date_due")),
                            b.getInt("archived") == 1);
                    resp.success(bill);
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void DeleteBill(Context ctx, Bill bill,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"bill/"+bill.id, Request.Method.DELETE,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                resp.success(null);
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void AddPayers(Context ctx, Bill bill, List<User> users,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        for(User u : users)
            NetworkBase.executeRequest(ctx,"bill/"+bill.id+"/payer/"+u.id, Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
                @Override
                public void success(String json) {
                    try {
                        resp.success(null);
                    }
                    catch (Exception e) {
                        resp.error(new APIError("Error parsing json response."));
                    }
                }
                @Override
                public void error(APIError error){ resp.error(error); }
            });
    }

    public void DeletePayers(Context ctx,  Bill bill, List<User> users,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        for(User u : users)
            NetworkBase.executeRequest(ctx,"bill/"+bill.id+"/payer/"+u.id, Request.Method.DELETE,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
                @Override
                public void success(String json) {
                    try {
                        resp.success(null);
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

    public void MarkPayerPaid(Context ctx, Bill bill,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"bill/"+bill.id+"/payer/pay", Request.Method.PUT,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                    resp.success(null);
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    public void ArchiveBills(Context ctx,List<Bill> bills,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        for(Bill b : bills) {
            int payerUnpaidCount = 0;
            if (b.date_paid == null) { continue; }
            for(HashMap.Entry<User, Boolean> entry : b.payers.entrySet()) {
                if (!entry.getValue())
                    payerUnpaidCount ++;
            }
            if (payerUnpaidCount != 0) { continue; }


            NetworkBase.executeRequest(ctx,"bill/"+b.id+"/archive", Request.Method.PUT,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
                @Override
                public void success(String json) {
                    try {
                        resp.success(null);
                    }
                    catch (Exception e) {
                        resp.error(new APIError("Error parsing json response."));
                    }
                }
                @Override
                public void error(APIError error){ resp.error(error); }
            });
        }
    }

    public void DuplicateBills(Context ctx, List<Bill> bills,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        for(Bill b : bills)
            NetworkBase.executeRequest(ctx,"bill/"+b.id+"/duplicate", Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
                @Override
                public void success(String json) {
                    try {
                        resp.success(null);
                    }
                    catch (Exception e) {
                        resp.error(new APIError("Error parsing json response."));
                    }
                }
                @Override
                public void error(APIError error){ resp.error(error); }
            });
    }

    public void NotifyUnpaidPayers(Context ctx, Bill bill,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();

        NetworkBase.executeRequest(ctx,"bill/"+bill.id+"/duplicate", Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    resp.success(null);
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

}
