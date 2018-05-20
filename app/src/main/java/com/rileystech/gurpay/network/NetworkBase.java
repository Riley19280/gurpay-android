package com.rileystech.gurpay.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkBase {
    private static NetworkBase shared;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    static String baseURL = "https://rileystech.com/gurpay/api/";

    /** -----------------------EXAMPLE-------------------*/

    public void EXAMPLE(Context ctx, String param1, String param2,final APICallResponse resp){
        HashMap<String, String> params = new HashMap<>();
        params.put("param1", param1);
        params.put("param2", param2);

        NetworkBase.executeRequest(ctx,"EXROUTE", Request.Method.POST,  params, NetworkBase.getHeaders(ctx), new NetworkResponse() {
            @Override
            public void success(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    resp.success(new Object());
                }
                catch (Exception e) {
                    resp.error(new APIError("Error parsing json response."));
                }
            }
            @Override
            public void error(APIError error){ resp.error(error); }
        });
    }

    /** -----------------------END EXAMPLE-------------------*/

    public static void executeRequest(Context ctx, String route, int method, final HashMap<String,String> params, final Map<String,String> headers, final NetworkResponse response){
        String url = NetworkBase.baseURL.concat(route);


        final JSONObject parameters = new JSONObject(params);

        StringRequest stringRequest =
                new StringRequest(method, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String resp) {
                        Log.e("TEST","successful query");
                        response.success(resp);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            byte[] htmlBodyBytes = error.networkResponse.data;
                            String resp = new String(htmlBodyBytes);
                            Log.e("TEST",resp);
                            JSONArray errorArray = new JSONObject(resp).getJSONArray("errors");

                            APIError newAPIError = new APIError();
                            for (int i = 0; i < errorArray.length(); i++)
                                newAPIError.append(errorArray.getString(i));

                            response.error(newAPIError);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            response.error(new APIError("An unknown error occurred."));
                        }
                    }
                })
                {

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }

                    /** Passing some request headers* */
                    @Override
                    public Map<String,String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                };

        // Access the RequestQueue through your singleton class.
        NetworkBase.shared(ctx).addToRequestQueue(stringRequest);
    }

    private NetworkBase(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
    }

    public static Map<String,String> getHeaders(Context ctx){

        Map<String, String> headers = new HashMap<>();
        headers.put("device-id", Util.getUUID(ctx));
        headers.put("group-code","123456");//TODO:get group code here, yes you can register with an empty group code
        return headers;
    }

    public static synchronized NetworkBase shared(Context context) {
        if (shared == null) {
            shared = new NetworkBase(context);
        }
        return shared;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
