package com.rileystech.gurpay;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.ServiceBase;

public class InstanceIdService extends FirebaseInstanceIdService {

    public InstanceIdService() {
        super();
        Log.d("TEST","SERVICE INIT");
    }

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("TEST", "Refreshed token: " + token);
        ServiceBase.user.RegisterPush(this, token, new APICallResponse() {
            @Override
            public void success(Object obj) {

            }

            @Override
            public void error(APIError err) {
                super.error(err);
            }
        });
    }
}
